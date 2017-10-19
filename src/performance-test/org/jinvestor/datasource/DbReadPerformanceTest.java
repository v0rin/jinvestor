package org.jinvestor.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.converter.FastRawResultSetToPojoConverter;
import org.jinvestor.datasource.converter.ResultSetToBarConverter;
import org.jinvestor.datasource.db.DbReader;
import org.jinvestor.datasource.db.DbUtilsReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.junit.Before;
import org.junit.Test;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class DbReadPerformanceTest {

	private static final Logger LOG = LogManager.getLogger();

	private static final String TEST_RES_PATH = "datasource/sqlite/";
	private static final String DB_PATH = TEST_RES_PATH + "test-big.sqlite";
//	private static final String DB_PATH = TEST_RES_PATH + "test.sqlite";
	private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
	private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

	private static final int iterCount = 50;

	private IEntityMetaData<Bar> entityMetaData;
	private String selectQuery;

	@Before
	public void setUp() {
		entityMetaData = EntityMetaDataFactory.get(Bar.class);
		selectQuery = SQLiteQueryBuilder
			.select("*")
			.from(entityMetaData.getTableName())
			.limit(50*250)
			.build();
	}


	@Test
	public void shouldReadBarPojoFromSqliteUsingReflectionConverter() throws Exception {
		IConverter<ResultSet, Bar> barConverter = new FastRawResultSetToPojoConverter<Bar>(Bar.class);
		readAndTimeExecution(barConverter);
	}


	@Test
	public void shouldReadBarPojoFromSqliteUsingHardcodedConverter() throws Exception {
		IConverter<ResultSet, Bar> barConverter = new ResultSetToBarConverter(Bar.class);
		readAndTimeExecution(barConverter);
	}

	@Test
	public void shouldReadBarPojoFromSqliteUsingBasicRowProcessor() throws Exception {
		IConverter<ResultSet, Bar> barConverter = new ResultSetToBarDbUtilsConverter(Bar.class);
		readAndTimeExecution(barConverter);
	}


	@Test
	public void dbUtilsTest2() throws Exception {
		ResultSetHandler<List<Bar>> rsh = new BeanListHandler<>(Bar.class);
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			try (DbUtilsReader<Bar> dbReader = new DbUtilsReader<>(DB_CONNECTION_STRING, selectQuery, rsh)) {
				readAndTimeExecution(() -> {
					try {
						return dbReader.get();
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}},
					rsh.getClass().getName());
			}
		}
		LOG.info("total read time=" + sw.elapsed() + " [{}]", rsh.getClass().getName());
	}


	@Test
	public void dbUtilsTest() throws SQLException {
		ResultSetHandler<List<Bar>> rsh = new BeanListHandler<>(Bar.class);
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING)) {
				QueryRunner run = new QueryRunner();
				List<Bar> bars = run.query(connection, selectQuery, rsh);
//				LOG.info("Read time=" + sw.elapsed() + " [{}]", rsh.getClass().getName());
//				LOG.info("bar count=" + bars.size());
//				LOG.info(bars.get(0));
			}
		}
		LOG.info("total read time=" + sw.elapsed() + " [{}]", rsh.getClass().getName());
	}


	private static class ResultSetToBarDbUtilsConverter extends FastRawResultSetToPojoConverter<Bar> {

		ResultSetHandler<Bar> converter = new BeanHandler<Bar>(Bar.class);

		public ResultSetToBarDbUtilsConverter(Class<Bar> type) {
			super(type);
		}

		@Override
		protected Bar createPojo(ResultSet resultSet) throws SQLException {
			return converter.handle(resultSet);
		}
	}


	private void readAndTimeExecution(IConverter<ResultSet, Bar> barConverter) throws Exception {
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			try (IReader<Bar> dbReader = new DbReader<>(DB_CONNECTION_STRING, selectQuery, barConverter)) {
				readAndTimeExecution(() -> {
					try {
						return dbReader.stream().collect(Collectors.toList());
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}},
					barConverter.getClass().getName());
			}
		}
		LOG.info("total read time=" + sw.elapsed() + " [{}]", barConverter.getClass().getName());
	}


	private Duration readAndTimeExecution(Supplier<List<Bar>> supplier, String converterName) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
//		Stream<Bar> barStream = dbReader.stream();
		List<Bar> bars = supplier.get();
//		Stream<Bar> barStream = dbReader.stream();
//		List<Bar> bars = new ArrayList<>();
//		barStream.forEach(bars::add);
		Duration elapsed = sw.elapsed();
//		LOG.info("Read time=" + elapsed + " [{}]", converterName);
//		LOG.info("bar count=" + bars.size());
//		LOG.info(bars.get(0));
		return elapsed;
	}
}
