package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BarsToDataPointsReducingConverterTest {

    private static final Logger LOG = LogManager.getLogger();

    private static final Instant FROM = Instant.parse("2010-01-01T23:59:59.999Z");
    private static final Instant TO_DONT_REDUCE = Instant.parse("2011-01-04T23:59:59.999Z");
    private static final Instant TO_REDUCE = Instant.parse("2015-01-04T23:59:59.999Z");

    private static final int REDUCE_THRESHOLD = 500;

    private IConverter<Stream<Bar>, List<DataPointWithTimestamp>> converter = new BarsToDataPointsReducingConverter();
    private ITimeSeriesFeed<Bar> barFeed;

    @Before
    public void setUp() {
        Configuration.initialize(new StaticJavaConfiguration<>(ConfKeys.class));
    }

    @After
    public void tearDown() throws Exception {
        barFeed.close();
    }


    @Test
    public void shouldReduceWhenBarsLargerThanThreshold() throws Exception {
        // given
        IInstrument instrument = new Instrument(SPY, USD);
        barFeed = instrument.getBarDailyFeed();
        List<Bar> bars = barFeed.stream(FROM, TO_REDUCE).collect(Collectors.toList());
        final int maxExpectedSize = 600;

        // when
        List<DataPointWithTimestamp> reducedDataPoints = converter.apply(barFeed.stream(FROM, TO_REDUCE));

        // then
        assertTrue(bars.size() > REDUCE_THRESHOLD);
        assertTrue(reducedDataPoints.size() < maxExpectedSize);
    }

    @Test
    public void shouldNotReduceWhenBarsSmallerThanThreshold() throws Exception {
        // given
        IInstrument instrument = new Instrument(SPY, USD);
        barFeed = instrument.getBarDailyFeed();
        List<Bar> bars = barFeed.stream(FROM, TO_DONT_REDUCE).collect(Collectors.toList());

        // when
        List<DataPointWithTimestamp> reducedDataPoints = converter.apply(barFeed.stream(FROM, TO_DONT_REDUCE));

        // then
        assertTrue(bars.size() < REDUCE_THRESHOLD);
        assertThat(reducedDataPoints.size(), is(bars.size()));
    }

}
