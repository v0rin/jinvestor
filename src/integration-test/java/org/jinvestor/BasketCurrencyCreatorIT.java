package org.jinvestor;

import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.CNY;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.GBP;
import static org.jinvestor.model.Instruments.JPY;
import static org.jinvestor.model.Instruments.USD;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.Before;
import org.junit.Test;

public class BasketCurrencyCreatorIT {

    private static final Logger LOG = LogManager.getLogger();

    @Before
    public void setUp() {
        Configuration.INSTANCE.initialize(new StaticJavaConfiguration<ConfKeys>(ConfKeys.class));
    }


    @Test
    public void testFxDates() throws Exception {
        //#### CONFIGURATION #####
        Instant from = Instant.parse("1971-01-04T23:59:59.999Z");
        Instant to = Instant.parse("2020-01-15T23:59:59.999Z");
        String currency = CNY;
        String refCurrency = USD;
        //########################

        IInstrument instrument = new Instrument(currency, refCurrency);
        ITimeSeriesFeed<Bar> barFeed = instrument.getBarDailyFeed();
        Iterator<Bar> barIter = barFeed.stream(from, to).iterator();

        long daysBetween = ChronoUnit.DAYS.between(from, to);
        Timestamp timestamp = barIter.next().getTimestamp();
        for (int i = 0; i < daysBetween; i++) {
            Instant curr = ChronoUnit.DAYS.addTo(from, i);

            Instant barInstant = timestamp.toLocalDateTime().atOffset(ZoneOffset.UTC).toInstant();
            int dayOfWeek = curr.atZone(ZoneOffset.UTC).getDayOfWeek().getValue();
//            LOG.info("curr=     " + curr);
//            LOG.info("barInstant=" + barInstant);
            if (!curr.equals(barInstant)) {
                if (dayOfWeek < DayOfWeek.SATURDAY.getValue()) {
                    LOG.info("no entry=" + curr);
                }
                continue;
            }
            if (dayOfWeek >= DayOfWeek.SATURDAY.getValue()) {
                LOG.info("WEEKEND entry=" + curr);
            }
            timestamp = barIter.next().getTimestamp();
        }
        barFeed.close();
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldGenerateStream() {
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
    public void workInProgress() {
        //#### CONFIGURATION #####
        Instant from = Instant.parse("2000-01-01T00:00:00.000Z");
        Instant to = Instant.parse("2000-01-10T23:59:59.999Z");

        Map<IInstrument, Double> basketComposition = new HashMap<>();
        String refCurrencyCode = USD;
        basketComposition.put(new Instrument(USD, refCurrencyCode), 0.3d);
        basketComposition.put(new Instrument(EUR, refCurrencyCode), 0.3d);
        basketComposition.put(new Instrument(CNY, refCurrencyCode), 0.2d);
        basketComposition.put(new Instrument(JPY, refCurrencyCode), 0.1d);
        basketComposition.put(new Instrument(GBP, refCurrencyCode), 0.1d);
        //########################

        IBasketCurrencyCreator creator = new BasketCurrencyCreator(new Instrument(BC1, USD), basketComposition);

        Stream<Bar> currencyBasketBars = creator.create(from, to);

        currencyBasketBars.forEach(LOG::info);
        // TODO (AF) check if the instrument exists in db and ask if use wants to continue
        // should it be in DbWriter or on some higher level? like here
//        String dbConnectionString = Configuration.INSTANCE.getString(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING);
//        try (IWriter<Bar> barDbWriter = new BarDbWriter(dbConnectionString + dbPath)) {
//            barDbWriter.write(currencyBasketBars);
//        }
    }
}
