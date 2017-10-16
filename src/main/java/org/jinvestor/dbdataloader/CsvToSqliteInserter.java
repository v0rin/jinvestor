package org.jinvestor.dbdataloader;

import java.io.File;
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

	private static String URL_PREFIX = "jdbc:sqlite:";

	private Connection connection;
	private IDbDataInserter<String> csvToDbLoader;
	private String tableName;


	public CsvToSqliteInserter(String dbPath,
							 String tableName,
							 Map<String, String> columnMappings,
							 char separator) throws SQLException {
		this.tableName = tableName;
		connection = connect(dbPath);
		csvToDbLoader = new CsvToDbInserter(connection, tableName, columnMappings, separator);
	}


	private void createTable(String tableName) throws SQLException {
//	    connection.prepareStatement(createTableQuery).executeUpdate();
	}


	@Override
	public void insert(String csvPath) throws IOException {
		csvToDbLoader.insert(csvPath);
	}


	private Connection connect(String dbPath) {
		boolean createTable = false;
		if (!new File(dbPath).exists()) {
			LOG.info("Since database [" + dbPath + "] does not exists it will be created");
			createTable = true;
		}

        String url = URL_PREFIX + dbPath;
        try {
        	connection = DriverManager.getConnection(url, new Properties());
            LOG.info("Connection to database has been established.");

			if (createTable) {
				createTable(tableName);
			}

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
