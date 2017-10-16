package org.jinvestor.dbdataloader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleflatmapper.csv.CsvParser;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkArgument;

/**
*
* @author Adam
*/
public class CsvToDbInserter implements IDbDataInserter<String> {

	private static final Logger LOG = LogManager.getLogger();

	private static int BATCH_SIZE = 1000;

	private Connection connection;
	private String tableName;
	private Map<String, String> csvToDbColumnMappings;
	private char separator;

	private String[] dbColumns;
	private long rowNo;
	private List<Object[]> buffer;

	public CsvToDbInserter(Connection connection,
						 String tableName,
						 Map<String, String> csvToDbColumnMappings,
						 char separator) throws SQLException {
		checkArgument(csvToDbColumnMappings != null, "csvToDbColumnMappings needs to be set");

		this.connection = connection;
		this.tableName = tableName;
		this.csvToDbColumnMappings = csvToDbColumnMappings;
		this.separator = separator;

		this.buffer = new ArrayList<>();

		this.connection.setAutoCommit(false);
	}


	@Override
	public void insert(String csvPath) throws IOException {
		try (Reader reader = new FileReader(csvPath)) {
		    CsvParser.separator(separator).stream(reader).forEach(this::processRow);
		}
		flushBuffer();
	}


	private void processRow(String[] row) {
		if (rowNo++ == 0) {
			Preconditions.checkArgument(row.length == csvToDbColumnMappings.size(),
					"Declared columnMappings count[" + csvToDbColumnMappings.size() + "] " +
					"differs from column count in the csv file[" + row.length + "]");

			dbColumns = getColumns(row, csvToDbColumnMappings);
			return;
		}

		buffer.add((Object[])row);
		if (buffer.size() == BATCH_SIZE) {
			flushBuffer();
			buffer.clear();
		}
	}


	private String[] getColumns(String[] csvColumns, Map<String, String> csvToDbColMappings) {
		return Arrays.asList(csvColumns).stream()
				.map(csvColName -> csvToDbColMappings.get(csvColName))
				.collect(Collectors.toList())
				.toArray(new String[csvToDbColMappings.size()]);
	}


	private void flushBuffer() {
		buffer.forEach(row -> {
			String insertSql = SQLiteQueryBuilder
					.insert()
					.into(tableName)
					.columns(dbColumns)
					.values((Object[])row)
					.build();

			try {
				connection.createStatement().executeUpdate(insertSql);
			}
			catch (SQLException e) {
				throw new RuntimeException("Could not add query to the batch; query=" + insertSql, e);
			}
		});
		try {
			connection.commit();
		}
		catch (SQLException e) {
			throw new RuntimeException("Could not commit batch", e);
		}
	}


	@Override
	public void close() throws Exception {
		// intentionally left empty
	}
}