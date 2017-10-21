package org.jinvestor.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.EtlJobFactory;
import org.jinvestor.model.Bar;
import org.jinvestor.model.entity.EntityMetaDataFactory;

import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class Etl {

	private static final Logger LOG = LogManager.getLogger();

	private static final String DATASOURCE_ROOT_PATH = "datasource/";
	private static final String SQLITE_CONNECTION_PREFIX = "jdbc:sqlite:";


	public static void main(String[] args) throws Exception {
		executeYahooCsvDailyBarsToSqliteEtl();
	}

	private static void executeYahooCsvDailyBarsToSqliteEtl() throws IOException, SQLException {
		//#### CONFIGURATION #####
		String csvPath = DATASOURCE_ROOT_PATH + "csv/yahoo.csv";
		String dbPath = "datasource/sqlite/test-big2.sqlite";
		//########################
		createDbTableForEntity(SQLITE_CONNECTION_PREFIX + dbPath, Bar.class);

		Stopwatch sw = Stopwatch.createStarted();
		EtlJobFactory.getYahooCsvDailyBarsToDbEtl(csvPath, SQLITE_CONNECTION_PREFIX + dbPath).execute();
		LOG.info("ETL job took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
	}


	private static void createDbTableForEntity(String dbConnectionString, Class<?> entityClass) throws SQLException {
		String createTableSql = EntityMetaDataFactory.get(entityClass).getCreateTableSql();
		try (Connection connection = DriverManager.getConnection(dbConnectionString);
			 PreparedStatement preparedStatement = connection.prepareStatement(createTableSql)) {

			preparedStatement.executeUpdate();
		}
	}

}
