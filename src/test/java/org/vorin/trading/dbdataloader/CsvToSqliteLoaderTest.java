package org.vorin.trading.dbdataloader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
*
* @author vorin
*/
public class CsvToSqliteLoaderTest {

	private final static String DB_PATH = "src/test/resources/com/vorin/trading/dbdataloader/test.sqlite";
	private final static String BARS_TABLE_NAME = "bars";
	private final static String CSV_PATH = "src/test/resources/com/vorin/trading/dbdataloader/with-headers.csv";

	private final static String SEPARATOR = ",";

	private IDbDataLoader<String> loader;

	@Before
	public void setUp() throws SQLException {
		Map<String, String> columnMappings = Arrays.asList("Date", "Open", "High", "Low", "Close", "Volume")
												.stream()
												.collect(Collectors.toMap(Function.identity(), Function.identity()));

		loader = new CsvToSqliteLoader(DB_PATH, BARS_TABLE_NAME, columnMappings, SEPARATOR);
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
		loader.load(CSV_PATH);

		// then

	}
}
