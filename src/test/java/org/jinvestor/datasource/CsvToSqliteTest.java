package org.jinvestor.datasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.converter.CsvBarToDbRowConverter;
import org.jinvestor.datasource.converter.YahooCsvDailyBarToDbRowConverter;
import org.jinvestor.datasource.db.FastRawDbWriter;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Currency.Code;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.jinvestor.time.DateTimeConverterFactory;
import org.jinvestor.time.IDateTimeConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class CsvToSqliteTest {

    private static final Logger LOG = LogManager.getLogger();

    private static final String TEST_RES_PATH = "src/test/resources/org/jinvestor/datasource/csv-to-sqlite-test/";
//    private static final String CSV_PATH = TEST_RES_PATH + "with-headers.csv";
    private static final String CSV_PATH = "datasource/csv/yahoo.csv";
//    private static final String DB_PATH = TEST_RES_PATH + "test.sqlite";
    private static final String DB_PATH = "datasource/sqlite/test-big.sqlite1";
    private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
    private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

    private static final char SEPARATOR = ',';

    private IEntityMetaData<Bar> barEntityMetaData = EntityMetaDataFactory.get(Bar.class);


    @Before
    public void setUp() throws SQLException {
        new File(DB_PATH).delete();
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING)) {
            connection.prepareStatement(barEntityMetaData.getCreateTableSql()).executeUpdate();
        }
    }


    @After
    public void tearDown() {
//        new File(DB_PATH).delete();
    }


    @Test
    public void simpleCsvToSqliteRawTest() throws SQLException, IOException {
        // given
        IReader<String[]> reader = new CsvReader(CSV_PATH, SEPARATOR);
//        IConverter<String[], Object[]> converter = new FastRawStringArrToObjectArrConverter(
//                                                            BarTestUtil.getStandardCsvColumnsMappings(),
//                                                            barEntityMetaData);
        IDateTimeConverter<String, String> dateTimeConverter = DateTimeConverterFactory.getDateToDateTimeEodConverter();
        IConverter<String[], Object[]> converter = new CsvBarToDbRowConverter(
                                                        YahooCsvDailyBarToDbRowConverter.getCsvToDbColumnsMappings(),
                                                        dateTimeConverter,
                                                        Currency.of(Code.USD));
        IWriter<Object[]> writer = new FastRawDbWriter(DB_CONNECTION_STRING,
                                              barEntityMetaData.getTableName(),
                                              barEntityMetaData.getColumns());

        // when
        final int iterCount = 1;
        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < iterCount; i++) {
            IEtlJob etlJob = new EtlJob<String[], Object[]>(reader, converter, writer);
            etlJob.execute();
        }
        LOG.info("elapsed=" + sw.elapsed());

        // then

    }
}
