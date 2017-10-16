package org.jinvestor.dbdataloader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private final static String DB_PATH = "src/test/resources/com/jinvestor/dbdataloader/test.sqlite";
	private final static String BARS_TABLE_NAME = "bars";
	private final static String CSV_PATH = "src/test/resources/com/jinvestor/dbdataloader/with-headers.csv";

	private final static char SEPARATOR = ',';

	private IDbDataInserter<String> loader;

	@Before
	public void setUp() throws SQLException, IOException {
		if (!new File(DB_PATH).delete()) {
			LOG.warn("Could not remove the test db file[" + DB_PATH + "]");
		}
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
//		new File(DB_PATH).delete();
	}


	// TODO (AF) check if creates db
	// TODO (AF) check if all records are in the db
	// TODO (AF) check if maps columns correctly
	// TODO (AF) check if checks for columns or columnmappings

	@Test
	public void shouldLoadCsvToDb() throws IOException {
		// given
		// when
		Stopwatch sw = Stopwatch.createStarted();
		loader.insert(CSV_PATH);
		System.out.println("elapsed=" + sw.elapsed());

		// then

	}

}
