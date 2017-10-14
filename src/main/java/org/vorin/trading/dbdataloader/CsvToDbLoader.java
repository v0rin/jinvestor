package org.vorin.trading.dbdataloader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.simpleflatmapper.csv.CsvParser;
import org.vorin.trading.model.Bar;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

/**
*
* @author vorin
*/
public class CsvToDbLoader implements IDbDataLoader<String> {

	private static final Logger LOG = LogManager.getLogger();

	private static int BATCH_SIZE = 1000;

	private Connection connection;
	private DSLContext dsl;
	private String tableName;
	private List<String> columns;
	private Map<String, String> columnMappings;
	private String separator;

	private long rowNo;

	public CsvToDbLoader(Connection connection,
						 String tableName,
						 Map<String, String> columnMappings,
						 String separator) throws SQLException {
		checkArgument(columnMappings != null, "columnMappings needs to be set");
		checkArgument(!Strings.isNullOrEmpty(separator), "columnMappings needs to be set");

		this.connection = connection;
		this.tableName = tableName;
		this.dsl = DSL.using(connection, SQLDialect.SQLITE);
		this.columnMappings = columnMappings;
		this.separator = separator;
	}


	@Override
	public void load(String csvPath) throws IOException {
		try (Reader reader = new FileReader(csvPath)) {
		    CsvParser.stream(reader).forEach(this::parseLine);
		}
	}


	private void parseLine(String[] row) {
		if (rowNo++ == 0) {
			Preconditions.checkArgument(row.length == columnMappings.size(),
					"Declared columnMappings count[" + columnMappings.size() + "] " +
					"differs from column count in the csv file[" + row.length + "]");

			columns = Arrays.asList(row).stream()
						.map(csvColName -> columnMappings.get(csvColName))
						.collect(Collectors.toList());

			return;
		}

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Bar bar = new Bar(LocalDate.parse(row[0], dateTimeFormatter).atStartOfDay().toInstant(ZoneOffset.UTC),
						  Double.parseDouble(row[1]),
						  Double.parseDouble(row[2]),
						  Double.parseDouble(row[3]),
						  Double.parseDouble(row[4]),
						  Long.parseLong(row[5]));

		// TODO (AF) save to db in batches
		dsl.batch(
				dsl.insertInto(DSL.table(tableName))
				    .set(DSL.field(Bar.DATE_TIME_COL_NAME, String.class), row[0])
					.set(DSL.field(Bar.OPEN_COL_NAME, Double.class), bar.getOpen())
					.set(DSL.field(Bar.HIGH_COL_NAME, Double.class), bar.getHigh())
					.set(DSL.field(Bar.LOW_COL_NAME, Double.class), bar.getLow())
					.set(DSL.field(Bar.CLOSE_COL_NAME, Double.class), bar.getClose())
					.set(DSL.field(Bar.VOLUME_COL_NAME, Long.class), bar.getVolume())
				)
			.execute();

		LOG.debug(Arrays.asList(row));
	}


	private void insertBatch() {

	}


	@Override
	public void close() throws Exception {
		// intentionally left empty
	}
}