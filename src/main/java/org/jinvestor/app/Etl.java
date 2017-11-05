package org.jinvestor.app;

import static org.jinvestor.model.Instruments.CNY;
import static org.jinvestor.model.Instruments.USD;

import java.io.IOException;
import java.sql.SQLException;

import org.jinvestor.EtlService;

/**
 *
 * @author Adam
 */
public class Etl {

    private static final String DATASOURCE_ROOT_PATH = "datasource/";
    private static final String STANDARD_DAILY_DB_PATH = "datasource/sqlite/bar-daily.sqlite";


    private Etl() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public static void main(String[] args) throws Exception {
    }

    protected static void executeYahooCsvDailyBarsToSqliteEtl() throws IOException, SQLException {
        //#### CONFIGURATION #####
        String csvPath = DATASOURCE_ROOT_PATH + "csv/yahoo.csv";
        String dbPath = STANDARD_DAILY_DB_PATH;
        String currencyCode = USD;
        //########################

        new EtlService().yahooCsvDailyBarsToSqlite(csvPath, dbPath, currencyCode);
    }

    protected static void executeStooqFxCsvDailyBarsToSqliteEtl() throws IOException, SQLException {
        //#### CONFIGURATION #####
        String csvPath = DATASOURCE_ROOT_PATH + "csv/cnyusd_daily_stooq.csv";
        String dbPath = STANDARD_DAILY_DB_PATH;
        String symbol = CNY;
        String currencyCode = USD;
        //########################

        new EtlService().stooqFxCsvDailyBarsToSqlite(csvPath, dbPath, symbol, currencyCode);
    }
}
