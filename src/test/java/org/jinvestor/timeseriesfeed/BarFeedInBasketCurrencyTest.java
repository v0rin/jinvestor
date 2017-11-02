package org.jinvestor.timeseriesfeed;

import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.CNY;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.GBP;
import static org.jinvestor.model.Instruments.JPY;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BarFeedInBasketCurrencyTest {

    private static final Logger LOG = LogManager.getLogger();

    private static final Instant FROM = Instant.parse("2015-01-01T23:59:59.999Z");
    private static final Instant TO = Instant.parse("2015-01-14T23:59:59.999Z");

    private static final String BASKET_CURRENCY = BC1;

    private ITimeSeriesFeed<Bar> bcBarFeed;

    @Before
    public void setUp() {
        Configuration.initialize(new StaticJavaConfiguration<>(ConfKeys.class));
    }


    @After
    public void tearDown() throws Exception {
        if (bcBarFeed != null) bcBarFeed.close();
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void test() throws IOException {
        // given
        Map<String, Double> basketComposition = new HashMap<>();
        basketComposition.put(USD, 0.3);
        basketComposition.put(EUR, 0.3);
        basketComposition.put(CNY, 0.2);
        basketComposition.put(JPY, 0.15);
        basketComposition.put(GBP, 0.05);

        IInstrument instrument = new Instrument(SPY, USD);
        bcBarFeed = instrument.getBarDailyFeedInBasketCurrency(BASKET_CURRENCY, basketComposition);

        // when
        Stopwatch sw = Stopwatch.createStarted();
        List<Bar> bars = bcBarFeed.stream(FROM, TO).collect(Collectors.toList());
        LOG.info("elapsed=" + sw.elapsed());
        LOG.info("bars.size()=" + bars.size());

        // then
//        bars.forEach(LOG::info);

        sw = Stopwatch.createStarted();
//        String json = jacksonTest(bars);
        String json = gsonTest(bars);
        LOG.info("elapsed=" + sw.elapsed());
        LOG.info("json=" + json);

//        List<Bar> straightBars = instrument.getBarDailyFeed().stream(FROM, TO).collect(Collectors.toList());
//        straightBars.forEach(LOG::info);
    }

    private String gsonTest(List<Bar> bars) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(bars);
    }
}
