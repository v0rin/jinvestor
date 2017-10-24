package org.jinvestor.datasource;

import org.jinvestor.datasource.converter.CsvBarToDbRowConverter;
import org.jinvestor.datasource.converter.Stooq;
import org.jinvestor.datasource.converter.Yahoo;
import org.jinvestor.datasource.db.FastRawDbWriter;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Instrument;
import org.jinvestor.time.DateTimeConverterFactory;

/**
 *
 * @author Adam
 */
public class EtlJobFactory {

    private static final char STANDARD_CSV_SEPARATOR = ',';

    private EtlJobFactory() {
        throw new InstantiationError("This class should not instantiated");
    }

    public static IEtlJob getYahooCsvStocksDailyBarsToDbEtl(String csvPath,
                                                            String dbConnectionString,
                                                            Currency currency) {
        IReader<String[]> reader = new CsvReader(csvPath, STANDARD_CSV_SEPARATOR);

        IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter.Builder(
                    Yahoo.getStocksCsvToDbColumnsMappings(),
                    DateTimeConverterFactory.getDateToDateTimeEodConverter(),
                    currency)
                .build();

        IWriter<Object[]> writer = new FastRawDbWriter(dbConnectionString, Bar.class);

        return new EtlJob<String[], Object[]>(reader, converter, writer);
    }

    public static IEtlJob getStooqCsvFxDailyBarsToDbEtl(String csvPath,
                                                        String dbConnectionString,
                                                        Instrument instrument,
                                                        Currency currency) {
        IReader<String[]> reader = new CsvReader(csvPath, STANDARD_CSV_SEPARATOR);

        IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter.Builder(
                    Stooq.getFxCsvToDbColumnsMappings(),
                    DateTimeConverterFactory.getDateToDateTimeEodConverter(),
                    currency)
                .instrument(instrument)
                .volume(Long.MAX_VALUE)
                .build();

        IWriter<Object[]> writer = new FastRawDbWriter(dbConnectionString, Bar.class);

        return new EtlJob<String[], Object[]>(reader, converter, writer);
    }
}
