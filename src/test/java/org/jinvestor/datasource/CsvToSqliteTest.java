package org.jinvestor.datasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.converter.CsvBarToDbRowConverter;
import org.jinvestor.datasource.converter.YahooCsvDailyBarToDbRowConverter;
import org.jinvestor.datasource.db.FastRawDbWriter;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.jinvestor.time.DateTimeConverter;
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
//	private static final String CSV_PATH = TEST_RES_PATH + "with-headers.csv";
	private static final String CSV_PATH = "datasource/csv/yahoo.csv";
//	private static final String DB_PATH = TEST_RES_PATH + "test.sqlite";
	private static final String DB_PATH = "datasource/sqlite/test-big.sqlite1";
	private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
	private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

	private final static char SEPARATOR = ',';

	private IEntityMetaData<Bar> barEntityMetaData = EntityMetaDataFactory.get(Bar.class);


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
//		IConverter<String[], Object[]> converter = new FastRawStringArrToObjectArrConverter(
//															BarTestUtil.getStandardCsvColumnsMappings(),
//															barEntityMetaData);
		DateTimeFormatter fromDateTimeFormatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd")
		        .optionalStart()
		        .appendPattern(" HH:mm")
		        .optionalEnd()
		        .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
		        .parseDefaulting(ChronoField.MILLI_OF_SECOND, 999)
		        .toFormatter();
		DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		IConverter<String, String> dateTimeConverter = new DateTimeConverter(fromDateTimeFormatter,
																			 toDateTimeFormatter);
		IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter(
														YahooCsvDailyBarToDbRowConverter.getCsvToDbColumnsMappings(),
														dateTimeConverter);
		IWriter<Object[]> writer = new FastRawDbWriter(DB_CONNECTION_STRING,
											  barEntityMetaData.getTableName(),
											  barEntityMetaData.getColumns());

		// when
		final int iterCount = 1;
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			IEtlJob etlJob = new EtlJob<String[], Object[]>(reader, converter, writer);
			etlJob.execute();
		}
		LOG.info("elapsed=" + sw.elapsed());

		// then

	}
}
