package org.jinvestor.timeseriesfeed;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.datasource.db.BarDbWriter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.time.TimestampUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adam
 */
public class BarFeedInBasketCurrencyTest {

    private static final Instant FROM = Instant.parse("2015-01-01T23:59:59.999Z");
    private static final Instant TO = FROM;

    private static final String TEST_RES_PATH = "src/test/resources/";
    private static final String DB_PATH = TEST_RES_PATH + "bar-feed-in-basket-currency-test.sqlite";
    private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
    private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

    private static final String BASKET_CURRENCY = BC1;
    private static final String SYMBOL = SPY;
    private static final double USD_WEIGHT = 0.5;
    private static final double EUR_WEIGHT = 0.5;
    private static final Timestamp TIMESTAMP = TimestampUtil.fromInstantInUTC(FROM);

    private ITimeSeriesFeed<Bar> barFeed;

    private Map<String, Double> basketComposition;


    @Before
    public void setUp() throws SQLException {
        StaticJavaConfiguration<ConfKeys> configuration = new StaticJavaConfiguration<>(ConfKeys.class);
        configuration.setValue(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING, DB_CONNECTION_STRING);
        Configuration.initialize(configuration);

        basketComposition = ImmutableMap.of(USD, USD_WEIGHT, EUR, EUR_WEIGHT);
    }


    @After
    public void tearDown() throws Exception {
        if (barFeed != null) barFeed.close();
        if (!new File(DB_PATH).delete()) {
            throw new IOException("Could not delete test db file");
        }
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldCorrectlyConvertInstrumentCurrencyToBasketCurrency() throws Exception {
        // given
        IInstrument instrument = new Instrument(SYMBOL, USD);
        barFeed = instrument.getBarDailyFeedInBasketCurrency(BASKET_CURRENCY, basketComposition);
        Bar[] testBars = new Bar[] {
            new Bar(EUR, TIMESTAMP, 3d, 4d, 7d, 9d, 1L, USD),
            new Bar(SPY, TIMESTAMP, 1d, 1d, 1d, 1d, 2L, USD),
        };
        prepareTestDb(testBars);

        List<Bar> expectedBars = Arrays.asList(new Bar(SPY, TIMESTAMP, 0.5, 0.4, 0.25d, 0.2d, 2L, BC1));

        // when
        List<Bar> actualBars = barFeed.stream(FROM, TO).collect(Collectors.toList());

        // then
        assertThat(actualBars, is(expectedBars));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldCorrectlyConvertInstrumentCurrencyToBasketCurrencyViaProxy() throws Exception {
        // given
        IInstrument instrument = new Instrument("WIG", "PLN");
        barFeed = instrument.getBarDailyFeedInBasketCurrencyByProxy(BASKET_CURRENCY, basketComposition, USD);
        Bar[] testBars = new Bar[] {
            new Bar(EUR, TIMESTAMP, 3d, 3d, 3d, 3d, 1L, USD),
            new Bar(SPY, TIMESTAMP, 1d, 1d, 1d, 1d, 2L, USD),
            new Bar("PLN", TIMESTAMP, 0.3d, 0.5d, 0.2d, 0.1d, 3L, USD),
            new Bar("WIG", TIMESTAMP, 1d, 1d, 1d, 1d, 4L, "PLN"),
        };
        prepareTestDb(testBars);

        List<Bar> expectedBars = Arrays.asList(new Bar("WIG", TIMESTAMP, 0.15, 0.25, 0.1, 0.05, 4L, BC1));

        // when
        List<Bar> actualBars = barFeed.stream(FROM, TO).collect(Collectors.toList());

        // then
        assertThat(actualBars, is(expectedBars));
    }


    private void prepareTestDb(Bar[] bars) throws Exception {
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING)) {
            connection.prepareStatement(EntityMetaDataFactory.get(Bar.class).getCreateTableSql()).executeUpdate();
        }
        try (IWriter<Bar> dbWriter = new BarDbWriter(DB_CONNECTION_STRING)) {
            dbWriter.write(Stream.of(bars));
        }
    }
}
