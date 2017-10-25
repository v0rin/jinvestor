package org.jinvestor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Currency.Code;
import org.jinvestor.model.Instrument;
import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.AtomicDouble;

import static com.google.common.base.Preconditions.checkArgument;

public class BasketCurrencyCreatorIT {

    private static final Logger LOG = LogManager.getLogger();

    @Before
    public void setUp() {
        Configuration.INSTANCE.initialize(new StaticJavaConfiguration(ConfKeys.class));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shoudGenerateStream() {
        int from = 0;
        int to = 10;

        Function<Integer, Bar> barMapper = i -> {
            Bar bar = new Bar();
            bar.setOpen(Double.valueOf(i));
            return bar;
        };
        Stream<Bar> barStream = IntStream.range(from, to).boxed().map(barMapper);
        barStream.forEach(LOG::info);
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testCombiningStreams() {
        // given
        Instant from = Instant.parse("1999-12-31T00:00:00Z");
        Instant to = Instant.parse("2000-01-05T23:59:59.999Z");

        Map<Iterator<Integer>, Double> iterMap = new HashMap<>();
        iterMap.put(Arrays.asList(1, 2, 3, 4, 1).stream().iterator(), 1d);
        iterMap.put(Arrays.asList(3, 3, 1, 2, 1).stream().iterator(), 1d);
        iterMap.put(Arrays.asList(2, 1, 5, 8, 4).stream().iterator(), 1d);

        checkArgument(!from.isAfter(to), "'from' is after 'to'");
        Instant curr = from;
        while (!curr.isAfter(to)) {
            if (curr.atZone(ZoneOffset.UTC).getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
                curr = curr.plus(1, ChronoUnit.DAYS);
                continue;
            }
            AtomicDouble d = new AtomicDouble();
            iterMap.forEach((barStream, weight) -> {
                // TODO (AF) verify date
                d.addAndGet(barStream.next() * weight);
            });
            LOG.info(curr + ": " + d);
            curr = curr.plus(1, ChronoUnit.DAYS);
        }
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void workInProgress() {
        //#### CONFIGURATION #####
        String dbPath = "datasource/sqlite/bar_daily.sqlite";
        Instant from = Instant.parse("2000-01-01T00:00:00.000Z");
        Instant to = Instant.parse("2000-01-10T23:59:59.999Z");

        Map<Currency, Double> basketComposition = new HashMap<>();
//        basketComposition.put(Currency.of(Code.USD), 0.3d);
        basketComposition.put(Currency.of(Code.EUR), 0.4d);
        basketComposition.put(Currency.of(Code.CNY), 0.3d);
        basketComposition.put(Currency.of(Code.JPY), 0.2d);
        basketComposition.put(Currency.of(Code.GBP), 0.1d);
        //########################

        IBasketCurrencyCreator creator = new BasketCurrencyCreator(Currency.of(Code.BC1), basketComposition);

        Stream<Bar> currencyBasketBars = creator.create(from, to);

        currencyBasketBars.forEach(LOG::info);
        // TODO (AF) check if the instrument exists in db and ask if use wants to continue
        // should it be in DbWriter or on some higher level? like here
//        String dbConnectionString = Configuration.INSTANCE.getString(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING);
//        try (IWriter<Bar> barDbWriter = new BarDbWriter(dbConnectionString + dbPath)) {
//            barDbWriter.write(currencyBasketBars);
//        }
    }


    @Test
    public void shouldConvertCurrencyMapToInstrumentMap() {
        // given
        Map<Currency, Double> currencyMap = new HashMap<>();
        currencyMap.put(Currency.of(Code.USD), 0d);
        currencyMap.put(Currency.of(Code.EUR), 1d);
        BasketCurrencyCreator creator = new BasketCurrencyCreator(Currency.of(Code.BC1), currencyMap);

        Map<Instrument, Double> expected = new HashMap<>();
        expected.put(Instrument.of(Code.USD.name() + Code.USD.name()), currencyMap.get(Currency.of(Code.USD)));
        expected.put(Instrument.of(Code.EUR.name() + Code.USD.name()), currencyMap.get(Currency.of(Code.EUR)));

        // when
        Map<Instrument, Double> instrumentMap = creator.convertCurrencyMapToInstrumentMap(currencyMap);

        // then
        assertThat(instrumentMap, is(expected));
    }
}
