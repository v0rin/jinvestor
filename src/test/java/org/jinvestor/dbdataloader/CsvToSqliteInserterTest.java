package org.jinvestor.dbdataloader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
public class CsvToSqliteInserterTest {

	private static final Logger LOG = LogManager.getLogger();

	private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
	private final static String DB_PATH = "src/test/resources/com/jinvestor/dbdataloader/test.sqlite";
	private final static String BARS_TABLE_NAME = "bars";
	private final static String CSV_PATH = "src/test/resources/com/jinvestor/dbdataloader/with-headers.csv";

	private final static char SEPARATOR = ',';

	private IEntityMetaData<Bar> barEntityMetaData = EntityMetaDataFactory.get(Bar.class);
	private IDbDataInserter<String> loader;

	@Before
	public void setUp() throws SQLException, IOException {
		Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING_PREFIX + DB_PATH);
		connection.prepareStatement(barEntityMetaData.getCreateTableSql()).executeUpdate();

		Map<String, String> columnMappings = new HashMap<>();
		columnMappings.put("Symbol", "symbol");
		columnMappings.put("Date", "datetime");
		columnMappings.put("Open", "open");
		columnMappings.put("High", "high");
		columnMappings.put("Low", "low");
		columnMappings.put("Close", "close");
		columnMappings.put("Volume", "volume");

		loader = new CsvToSqliteInserter(DB_PATH, BARS_TABLE_NAME, columnMappings, SEPARATOR);
	}


	@After
	public void tearDown() throws Exception {
		loader.close();
		new File(DB_PATH).delete();
	}


	// TODO (AF) check if creates db
	// TODO (AF) check if all records are in the db
	// TODO (AF) check if maps columns correctly
	// TODO (AF) check if checks for columns or columnmappings

	@Test
	public void shouldLoadCsvToDb() throws IOException {
		// given
		// when
		final int iterCount = 5;
		Stopwatch sw = Stopwatch.createStarted();
		for (int i = 0; i < iterCount; i++) {
			loader.insert(CSV_PATH);
		}
		LOG.info("elapsed=" + sw.elapsed());
		// then

	}

}
