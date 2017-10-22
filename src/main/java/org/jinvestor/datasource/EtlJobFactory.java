package org.jinvestor.datasource;

import org.jinvestor.datasource.converter.YahooCsvDailyBarToDbRowConverter;
import org.jinvestor.datasource.db.FastRawDbWriter;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;

/**
 *
 * @author Adam
 */
public class EtlJobFactory {

    private static final char STANDARD_CSV_SEPARATOR = ',';

    private EtlJobFactory() {
        throw new InstantiationError("This class should not instantiated");
    }

    public static IEtlJob getYahooCsvDailyBarsToDbEtl(String csvPath, String dbConnectionString, Currency currency) {
        IReader<String[]> reader = new CsvReader(csvPath, STANDARD_CSV_SEPARATOR);
        IConverter<String[], Object[]> converter = new YahooCsvDailyBarToDbRowConverter(currency);
        IWriter<Object[]> writer = new FastRawDbWriter(dbConnectionString, Bar.class);

        return new EtlJob<String[], Object[]>(reader, converter, writer);
    }
}
