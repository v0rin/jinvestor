package org.vorin.trading.dbdataloader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.vorin.trading.model.Bar;

/**
*
* @author vorin
*/
public class CsvToSqliteLoader implements IDbDataLoader<String>, AutoCloseable {

	private static final Logger LOG = LogManager.getLogger();

	private static String URL_PREFIX = "jdbc:sqlite:";

	private Connection connection;
	private IDbDataLoader<String> csvToDbLoader;
	private String tableName;


	public CsvToSqliteLoader(String dbPath,
							 String tableName,
							 Map<String, String> columnMappings,
							 String separator) throws SQLException {
		this.tableName = tableName;
		connection = connect(dbPath);
		csvToDbLoader = new CsvToDbLoader(connection, tableName, columnMappings, separator);


		DSLContext dsl = DSL.using(connection, SQLDialect.SQLITE);
		dsl.createTable(tableName)
			.column(Bar.DATE_TIME_COL_NAME, SQLDataType.DATE)
			.column(Bar.OPEN_COL_NAME, SQLDataType.REAL)
			.column(Bar.HIGH_COL_NAME, SQLDataType.REAL)
			.column(Bar.LOW_COL_NAME, SQLDataType.REAL)
			.column(Bar.CLOSE_COL_NAME, SQLDataType.REAL)
			.column(Bar.VOLUME_COL_NAME, SQLDataType.INTEGER)
		    .constraints(DSL.constraint("PK_" + Bar.DATE_TIME_COL_NAME).primaryKey(Bar.DATE_TIME_COL_NAME))
			.execute();

		// ADD symbol
		// are there any other useful columns?
	}


	@Override
	public void load(String csvPath) throws IOException {
		csvToDbLoader.load(csvPath);
	}


	private Connection connect(String dbPath) {
		if (!new File(dbPath).exists()) {
			LOG.info("Since database [" + dbPath + "] does not exists it will be created");
		}

        String url = URL_PREFIX + dbPath;
        try {
        	connection = DriverManager.getConnection(url);
            LOG.info("Connection to SQLite has been established.");
        }
        catch (SQLException e) {
            LOG.error(e);
        }

        return connection;
	}


	@Override
	public void close() throws Exception {
		csvToDbLoader.close();
		connection.close();
	}
}
