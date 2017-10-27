package org.jinvestor.timeseriesfeed;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Instrument;
import org.jinvestor.model.Instruments;
import org.junit.Before;
import org.junit.Test;

public class BarFeedTest {

    private static final String TEST_RES_PATH =
            "src/test/resources/org/jinvestor/timeseriesfeed/bar-time-series-feed-test/";
    private static final String DB_PATH = TEST_RES_PATH + "test.sqlite";
    private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
    private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

    private static final String SYMBOL = Instruments.SPY;
    private static final String CURRENCY_CODE = Instruments.USD;

    private static final Instant FROM_FOREVER = Instant.parse("0000-01-01T23:59:59.999Z");
    private static final Instant TO_FOREVER = Instant.parse("9999-01-01T23:59:59.999Z");


    @Before
    public void setUp() throws SQLException {
        StaticJavaConfiguration<ConfKeys> testConfiguration = new StaticJavaConfiguration<>(ConfKeys.class);
        testConfiguration.setValue(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING, DB_CONNECTION_STRING);
        Configuration.INSTANCE.initialize(testConfiguration);
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldGetCorrectBars() throws IOException {
        // given
        List<Bar> expected = Arrays.asList(new Bar(SYMBOL,
                                                   Timestamp.valueOf("1993-01-29 23:59:59.999"),
                                                   43.96870000, 43.96870000, 43.75000000, 43.93750000, 1003200L,
                                                   CURRENCY_CODE),
                                           new Bar(SYMBOL,
                                                   Timestamp.valueOf("1993-02-01 23:59:59.999"),
                                                   43.96870000, 44.25000000, 43.96870000, 44.25000000, 480500L,
                                                   CURRENCY_CODE));

        // then
        List<Bar> actual = new Instrument(SYMBOL, CURRENCY_CODE)
                                .streamDaily(FROM_FOREVER, TO_FOREVER).collect(Collectors.toList());
        assertThat(actual, is(expected));
    }
}
