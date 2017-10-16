package org.jinvestor.datasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.model.Bar;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class CsvToSqliteTest {

	private static final Logger LOG = LogManager.getLogger();

	private static final String TEST_RES_PATH = "src/test/resources/org/jinvestor/datasource/csv-to-sqlite-test/";
	private static final String CSV_PATH = TEST_RES_PATH + "with-headers.csv";
	private static final String DB_PATH = TEST_RES_PATH + "test.sqlite";
	private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
	private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

	private final static char SEPARATOR = ',';

	private IEntityMetaData barEntityMetaData = EntityMetaDataFactory.get(Bar.class);


	@Before
	public void setUp() throws SQLException {
		new File(DB_PATH).delete();
		try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING)) {
			connection.prepareStatement(barEntityMetaData.getCreateTableSql()).executeUpdate();
		}
	}


	@After
	public void tearDown() {
//		new File(DB_PATH).delete();
	}


	@Test
	public void simpleCsvToSqliteRawTest() throws SQLException, IOException {
		// given
		IReader<String[]> reader = new CsvReader(CSV_PATH, SEPARATOR);
		IWriter<Object[]> writer = new RawDbWriter(DB_CONNECTION_STRING,
											  barEntityMetaData.getTableName(),
											  barEntityMetaData.getColumns());



		// when
		final int iterCount = 8;
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			IEtlJob converter = new EtlJob<String[], Object[]>(reader, new RawAdapter(), writer);
			converter.execute();
		}
		LOG.info("elapsed=" + sw.elapsed());

		// then

	}
}
