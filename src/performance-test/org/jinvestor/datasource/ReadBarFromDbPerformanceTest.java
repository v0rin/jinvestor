package org.jinvestor.datasource;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class ReadBarFromDbPerformanceTest {

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
//			.limit(5)
			.limit(50*250)
			.build();
	}


	@Test
	public void shouldReadBarPojoFromSqliteUsingHardcodedConverter() {
		timeExecution(new DbReader<>(DB_CONNECTION_STRING,
									 selectQuery,
									 new ResultSetToBarConverter()),
					  "ResultSetToBarConverter");
	}


	@Test
	public void shouldReadBarPojoFromSqliteUsingDbUtilsBeanListHandler() {
		timeExecution(new DbUtilsReader<>(DB_CONNECTION_STRING,
										  selectQuery,
										  Bar.class),
					  "DbUtilsBeanListHandler");
	}

	private void timeExecution(IReader<Bar> dbReader, String description) {
		timeExecution(() -> {
			try {
				return dbReader.stream().collect(Collectors.toList());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				try {
					dbReader.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, description);
	}


	private void timeExecution(Supplier<List<Bar>> supplier, String description) {
		List<Bar> bars = null;
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			bars = supplier.get();
		}
		LOG.info("Read time=" + sw.elapsed() + " [{}]", description);
		LOG.info("bars.size()=" + bars.size());
	}
}
