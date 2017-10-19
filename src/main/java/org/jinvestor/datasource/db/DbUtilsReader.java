package org.jinvestor.datasource.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Adam
 */
public class DbUtilsReader<T> implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger();

	private String dbConnectionString;
	private Properties dbConnectionProperties;
	private String sqlQuery;
	private ResultSetHandler<List<T>> resultHandler;

	private Connection connection;


	public DbUtilsReader(String dbConnectionString,
			 			 String sqlQuery,
			 			 ResultSetHandler<List<T>> resultHandler) {
		this(dbConnectionString, new Properties(), sqlQuery, resultHandler);
	}


	public DbUtilsReader(String dbConnectionString,
						 Properties dbConnectionProperties,
						 String sqlQuery,
						 ResultSetHandler<List<T>> resultHandler) {

		this.dbConnectionString = dbConnectionString;
		this.dbConnectionProperties = dbConnectionProperties;
		this.sqlQuery = sqlQuery;
		this.resultHandler = resultHandler;
	}


	public List<T> get() throws IOException {
		try {
			connection = DriverManager.getConnection(dbConnectionString, dbConnectionProperties);
//			LOG.info("Connection to the database[" + dbConnectionString + "] has been established.");
		}
		catch (SQLException e) {
			throw new IOException(e);
		}

		try {
//			QueryRunner run = new QueryRunner();
			return resultHandler.handle(connection.prepareStatement(sqlQuery).executeQuery());
//			return run.query(connection, sqlQuery, resultHandler);
		}
		catch (SQLException e) {
			throw new IOException("Could not execute sqlQuery=" + sqlQuery, e);
		}
	}


	@Override
	public void close() throws Exception {
		connection.close();
	}
}