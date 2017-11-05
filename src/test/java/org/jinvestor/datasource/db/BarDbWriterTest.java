package org.jinvestor.datasource.db;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Instrument;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BarDbWriterTest {
    private static final String TEST_RES_PATH = "src/test/resources/";
    private static final String DB_PATH = TEST_RES_PATH + "test-bar-db-writer.sqlite";
    private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
    private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

    private static final String SYMBOL = SPY;
    private static final String CURRENCY_CODE = USD;

    private static final Instant FROM_FOREVER = Instant.parse("0000-01-01T23:59:59.999Z");
    private static final Instant TO_FOREVER = Instant.parse("9999-01-01T23:59:59.999Z");

    private IEntityMetaData<Bar> barEntityMetaData = EntityMetaDataFactory.get(Bar.class);
    private ITimeSeriesFeed<Bar> barFeed;


    @Before
    public void setUp() throws SQLException {
        StaticJavaConfiguration<ConfKeys> testConfiguration = new StaticJavaConfiguration<>(ConfKeys.class);
        testConfiguration.setValue(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING, DB_CONNECTION_STRING);
        Configuration.initialize(testConfiguration);

        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING)) {
            connection.prepareStatement(barEntityMetaData.getCreateTableSql()).executeUpdate();
        }

        barFeed = new Instrument(SYMBOL, CURRENCY_CODE).getBarDailyFeed();
    }


    @After
    public void tearDown() throws Exception {
        barFeed.close();
        new File(DB_PATH).delete();
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldWriteBarsCorrectlyToDatabase() throws Exception {
        // given
        List<Bar> expected = Arrays.asList(new Bar(SYMBOL,
                                                   Timestamp.valueOf("1993-01-29 23:59:59.999"),
                                                   43.96870000, 43.96870000, 43.75000000, 43.93750000, 1003200L,
                                                   CURRENCY_CODE),
                                           new Bar(SYMBOL,
                                                   Timestamp.valueOf("1993-02-01 23:59:59.999"),
                                                   43.96870000, 44.25000000, 43.96870000, 44.25000000, 480500L,
                                                   CURRENCY_CODE));
        // when
        try (IWriter<Bar> writer = new BarDbWriter(DB_CONNECTION_STRING)) {
            writer.write(expected.stream());
        }

        // then
        List<Bar> actual = barFeed.stream(FROM_FOREVER, TO_FOREVER).collect(Collectors.toList());
        assertThat(actual, is(expected));
    }
}
