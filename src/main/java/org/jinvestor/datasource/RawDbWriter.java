package org.jinvestor.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.exception.AppRuntimeException;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO (AF) rollback if fails??
 * Inserts a stream of Objects into a database
 *
 * @author Adam
 */
public class RawDbWriter implements IWriter<Object[]> {

	private static final Logger LOG = LogManager.getLogger();

	private static final int BATCH_SIZE = 1000;

	private String dbConnectionString;
	private Properties dbConnectionProperties;
	private String dbTableName;

	private Connection connection;
	private String[] dbColumns;
	private List<Object[]> buffer;
	private int insertCount;


	public RawDbWriter(String dbConnectionString,
					String dbTableName,
					String[] dbColumns) {
		this(dbConnectionString, new Properties(), dbTableName, dbColumns);
	}


	public RawDbWriter(String dbConnectionString,
					Properties dbConnectionProperties,
					String dbTableName,
					String[] dbColumns) {
		checkArgument(dbColumns != null, "dbColumns needs to be set");

		this.dbConnectionString = dbConnectionString;
		this.dbConnectionProperties = dbConnectionProperties;
		this.dbTableName = dbTableName;
		this.dbColumns = dbColumns;
	}


	@Override
	public void write(Stream<Object[]> incomingStream) throws IOException {
		this.buffer = new ArrayList<>();
		try {
			connection = DriverManager.getConnection(dbConnectionString, dbConnectionProperties);
			LOG.info("Connection to the database[" + dbConnectionString + "] has been established.");
			connection.setAutoCommit(false);
			insertCount = 0;
		}
		catch (SQLException e) {
			throw new IOException(e);
		}

		incomingStream.filter(((Predicate<Object[]>)ArrayUtils::isEmpty).negate()).forEach(this::processRow);
		flushBuffer();
	}


	private void processRow(Object[] row) {
		buffer.add((Object[])row);
		if (buffer.size() == BATCH_SIZE) {
			flushBuffer();
		}
	}


	private void flushBuffer() {
		buffer.forEach(row -> {
			String insertSql = SQLiteQueryBuilder
					.insert()
					.into(dbTableName)
					.columns(dbColumns)
					.values((Object[])row)
					.build();

			try {
				insertCount += connection.createStatement().executeUpdate(insertSql);
			}
			catch (SQLException e) {
				throw new AppRuntimeException("Could not add query to the batch; query=" + insertSql, e);
			}
		});

		try {
			connection.commit();
		}
		catch (SQLException e) {
			throw new AppRuntimeException("Could not commit batch", e);
		}
		buffer.clear();
		buffer.clear();
	}


	@Override
	public void close() throws Exception {
		connection.close();
		LOG.debug("Inserted {} rows", insertCount);
	}
}
