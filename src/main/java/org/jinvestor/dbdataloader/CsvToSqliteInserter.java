package org.jinvestor.dbdataloader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
*
* @author Adam
*/
public class CsvToSqliteInserter implements IDbDataInserter<String>, AutoCloseable {

	private static final Logger LOG = LogManager.getLogger();

	private static final String URL_PREFIX = "jdbc:sqlite:";

	private Connection connection;
	private IDbDataInserter<String> csvToDbLoader;

	public CsvToSqliteInserter(String dbPath,
							 String tableName,
							 Map<String, String> columnMappings,
							 char separator) throws SQLException {
		connection = connect(dbPath);
		csvToDbLoader = new CsvToDbInserter(connection, tableName, columnMappings, separator);
	}


	@Override
	public void insert(String csvPath) throws IOException {
		csvToDbLoader.insert(csvPath);
	}


	private Connection connect(String dbPath) {
        String url = URL_PREFIX + dbPath;
        try {
        	connection = DriverManager.getConnection(url, new Properties());
            LOG.info("Connection to database has been established.");
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
