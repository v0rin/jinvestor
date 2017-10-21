package org.jinvestor.datasource.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.IReader;

/**
 *
 * @author Adam
 */
public class DbUtilsReader<T> implements IReader<T> {

	private static final Logger LOG = LogManager.getLogger();

	private String dbConnectionString;
	private Properties dbConnectionProperties;
	private String sqlQuery;

	private Connection connection;
	private QueryRunner run = new QueryRunner();
	private ResultSetHandler<List<T>> resultHandler;


	public DbUtilsReader(String dbConnectionString, String sqlQuery, Class<T> entityCLass) {
		this(dbConnectionString, new Properties(), sqlQuery, entityCLass);
	}

	public DbUtilsReader(String dbConnectionString,
						 Properties dbConnectionProperties,
						 String sqlQuery,
						 Class<T> entityClass) {

		this.dbConnectionString = dbConnectionString;
		this.dbConnectionProperties = dbConnectionProperties;
		this.sqlQuery = sqlQuery;
		this.resultHandler = new BeanListHandler<>(entityClass);
	}

	@Override
	public Stream<T> stream() throws IOException {
		try {
			connection = DriverManager.getConnection(dbConnectionString, dbConnectionProperties);
			LOG.debug(() -> "Connection to the database[" + dbConnectionString + "] has been established.");
		}
		catch (SQLException e) {
			throw new IOException(e);
		}

		try {
			return run.query(connection, sqlQuery, resultHandler).stream();
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
