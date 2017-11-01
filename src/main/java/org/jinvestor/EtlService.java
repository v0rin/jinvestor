package org.jinvestor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.dataprovider.Stooq;
import org.jinvestor.dataprovider.Yahoo;
import org.jinvestor.datasource.IReader;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.datasource.SyncedBarsReader;
import org.jinvestor.datasource.converter.CsvBarToDbRowConverter;
import org.jinvestor.datasource.converter.IConverter;
import org.jinvestor.datasource.converter.InstrumentsToBasketConverter;
import org.jinvestor.datasource.db.FastRawDbWriter;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.etl.EtlJob;
import org.jinvestor.etl.IEtlJob;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.io.YesNoCommandPrompt;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.time.DateTimeConverterFactory;

import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class EtlService {
    private static final Logger LOG = LogManager.getLogger();

    private static final char STANDARD_CSV_SEPARATOR = ',';
    private static final String SQLITE_CONNECTION_PREFIX = "jdbc:sqlite:";

    private String dbConnectionPrefix;

    /**
     *  Using default SQLite database
     */
    public EtlService() {
        this(SQLITE_CONNECTION_PREFIX);
    }


    public EtlService(String dbConnectionPrefix) {
        this.dbConnectionPrefix = dbConnectionPrefix;
    }


    public void createBasketCurrency(String basketCurrency,
                                     Map<String, Double> basketComposition,
                                     String refCurrency,
                                     Instant from,
                                     Instant to) throws Exception {

        List<IInstrument> instruments = basketComposition.keySet().stream()
                                                         .filter(symbol -> !symbol.equals(refCurrency))
                                                         .map(symbol -> new Instrument(symbol, refCurrency))
                                                         .collect(Collectors.toList());

        try (IReader<List<Bar>> syncedBarsReader = new SyncedBarsReader(instruments, from, to)) {
            IConverter<List<Bar>, Bar> converter = new InstrumentsToBasketConverter(
                    basketCurrency, refCurrency, basketComposition);
            List<Bar> basketBars = syncedBarsReader.stream().map(converter).collect(Collectors.toList());
        }
    }


    public void yahooCsvDailyBarsToSqlite(String csvPath, String dbPath, String currencyCode)
            throws IOException, SQLException {
        if (!new File(dbPath).exists() || (new File(dbPath).exists() && deleteOldDbWizard(dbPath))) {
            createDbTableForEntity(dbConnectionPrefix + dbPath, Bar.class);
        }

        Stopwatch sw = Stopwatch.createStarted();
        getYahooCsvStocksDailyBarsToDbEtl(csvPath, dbConnectionPrefix + dbPath, currencyCode).execute();
        LOG.info("ETL job took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }


    public void stooqFxCsvDailyBarsToSqlite(String csvPath, String dbPath, String symbol, String currencyCode)
            throws IOException, SQLException {
        if (!new File(dbPath).exists() || (new File(dbPath).exists() && deleteOldDbWizard(dbPath))) {
            createDbTableForEntity(dbConnectionPrefix + dbPath, Bar.class);
        }

        Stopwatch sw = Stopwatch.createStarted();
        getStooqCsvFxDailyBarsToDbEtl(csvPath, dbConnectionPrefix + dbPath, symbol, currencyCode).execute();
        LOG.info("ETL job took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }


    private static IEtlJob getYahooCsvStocksDailyBarsToDbEtl(String csvPath,
            String dbConnectionString,
            String currencyCode) {
        IReader<String[]> reader = new CsvReader(csvPath, STANDARD_CSV_SEPARATOR);

        IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter.Builder(
                Yahoo.getStocksCsvToDbColumnsMappings(),
                DateTimeConverterFactory.getDateToDateTimeEodConverter(),
                currencyCode)
                .build();

        IWriter<Object[]> writer = new FastRawDbWriter(dbConnectionString, Bar.class);

        return new EtlJob<String[], Object[]>(reader, converter, writer);
    }


    private static IEtlJob getStooqCsvFxDailyBarsToDbEtl(String csvPath,
            String dbConnectionString,
            String symbol,
            String currencyCode) {
        IReader<String[]> reader = new CsvReader(csvPath, STANDARD_CSV_SEPARATOR);

        IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter.Builder(
                Stooq.getFxCsvToDbColumnsMappings(),
                DateTimeConverterFactory.getDateToDateTimeEodConverter(),
                currencyCode)
                .symbol(symbol)
                .volume(Long.MAX_VALUE)
                .build();

        IWriter<Object[]> writer = new FastRawDbWriter(dbConnectionString, Bar.class);

        return new EtlJob<String[], Object[]>(reader, converter, writer);
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
