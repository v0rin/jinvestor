package org.jinvestor.app;

import static org.jinvestor.model.Instruments.CNY;
import static org.jinvestor.model.Instruments.USD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.etl.EtlJobFactory;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.io.YesNoCommandPrompt;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
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
    private static final String STANDARD_DAILY_DB_PATH = "datasource/sqlite/bar_daily.sqlite";


    private Etl() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public static void main(String[] args) throws Exception {
//        executeYahooCsvDailyBarsToSqliteEtl();
        executeStooqFxCsvDailyBarsToSqliteEtl();
    }

    private static void executeYahooCsvDailyBarsToSqliteEtl() throws IOException, SQLException {
        //#### CONFIGURATION #####
        String csvPath = DATASOURCE_ROOT_PATH + "csv/yahoo.csv";
        String dbPath = STANDARD_DAILY_DB_PATH;
        String currencyCode = USD;
        //########################

        if (!new File(dbPath).exists() || (new File(dbPath).exists() && deleteOldDbWizard(dbPath))) {
            createDbTableForEntity(SQLITE_CONNECTION_PREFIX + dbPath, Bar.class);
        }

        Stopwatch sw = Stopwatch.createStarted();
        EtlJobFactory.getYahooCsvStocksDailyBarsToDbEtl(csvPath, SQLITE_CONNECTION_PREFIX + dbPath, currencyCode)
            .execute();
        LOG.info("ETL job took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void executeStooqFxCsvDailyBarsToSqliteEtl() throws IOException, SQLException {
        //#### CONFIGURATION #####
        String csvPath = DATASOURCE_ROOT_PATH + "csv/cnyusd_daily_stooq.csv";
        String dbPath = STANDARD_DAILY_DB_PATH;
        IInstrument instrument = new Instrument(CNY, USD);
        String currencyCode = USD;
        //########################

        if (!new File(dbPath).exists() || (new File(dbPath).exists() && deleteOldDbWizard(dbPath))) {
            createDbTableForEntity(SQLITE_CONNECTION_PREFIX + dbPath, Bar.class);
        }

        Stopwatch sw = Stopwatch.createStarted();
        EtlJobFactory.getStooqCsvFxDailyBarsToDbEtl(
                csvPath, SQLITE_CONNECTION_PREFIX + dbPath, instrument, currencyCode).execute();
        LOG.info("ETL job took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void createDbTableForEntity(String dbConnectionString, Class<?> entityClass) throws SQLException {
        String createTableSql = EntityMetaDataFactory.get(entityClass).getCreateTableSql();
        try (Connection connection = DriverManager.getConnection(dbConnectionString);
             PreparedStatement preparedStatement = connection.prepareStatement(createTableSql)) {

            preparedStatement.executeUpdate();
        }
    }

    private static boolean deleteOldDbWizard(String dbPath) {
        String promptString = "Db[" + dbPath + "] exists. Do you want to delete it and create a new one?";

        Supplier<Boolean> yesAction = () -> {
            try {
                Files.deleteIfExists(Paths.get(dbPath));
            }
            catch (IOException e) {
                throw new AppRuntimeException("Could not delete db [" + dbPath + "]");
            }
            LOG.info("Db [{}] deleted", dbPath);
            return true;
        };

        Supplier<Boolean> noAction = () -> {
            LOG.info("You chose not to delete the old database. Appending data to existing one...");
            return false;
        };

        YesNoCommandPrompt<Boolean> prompt = new YesNoCommandPrompt<>(promptString, yesAction, noAction);
        return prompt.run();
    }
}
