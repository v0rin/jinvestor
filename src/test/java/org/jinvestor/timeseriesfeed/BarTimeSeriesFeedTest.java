package org.jinvestor.timeseriesfeed;

import java.io.IOException;
import java.time.Instant;

import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Instrument;
import org.junit.Before;
import org.junit.Test;

public class BarTimeSeriesFeedTest {

    @Before
    public void setUp() {
        Configuration.INSTANCE.initialize(new StaticJavaConfiguration());
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void test() throws IOException {
        Instant from = Instant.parse("2000-01-01T00:00:00.999Z");
        Instant to = Instant.parse("2000-01-10T23:59:59.999Z");

        ITimeSeriesFeed<Bar> dailyBarFeed = TimeSeriesFeedFactory.getDailyBarFeed(
                Instrument.of(Instrument.Code.SPY), Currency.of(Currency.Code.USD));

        dailyBarFeed.get(from, to).forEach(System.out::println);
    }
}
