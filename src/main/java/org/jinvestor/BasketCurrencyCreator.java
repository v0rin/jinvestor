package org.jinvestor;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Currency.Code;
import org.jinvestor.model.Instrument;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.jinvestor.timeseriesfeed.TimeSeriesFeedFactory;

import com.google.common.util.concurrent.AtomicDouble;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class BasketCurrencyCreator implements IBasketCurrencyCreator {

    private static final Logger LOG = LogManager.getLogger();

    private static final Currency REF_CURRENCY = Currency.of(Code.USD);

    private Map<Instrument, Double> basketComposition;

    private Instrument basketCurrency;


    public BasketCurrencyCreator(String name, Map<Currency, Double> basketComposition) {
        checkArgument(basketComposition.values().stream().mapToDouble(Double::doubleValue).sum() == 1d,
                      "Weights need to add up to 1; basketComposition=" + basketComposition);
        this.basketComposition = convertCurrencyMapToInstrumentMap(basketComposition);
        basketCurrency = Instrument.of(name + REF_CURRENCY.getCode());
    }


    @Override
    public Stream<Bar> create(Instant from, Instant to) {
        checkArgument(!from.isAfter(to), "'from' is after 'to'");

        Map<Iterator<Bar>, Double> iteratorMap = getStreamIteratorMapForBasket(basketComposition, from, to);

        Function<Long, Bar> barMapper = counter -> {
            Bar bar = new Bar();
            bar.setOpen(Double.valueOf(counter));
            return bar;
        };
        long daysBetween = ChronoUnit.DAYS.between(from, to); // should be working days
        Stream<Bar> barStream1 = LongStream.rangeClosed(0, daysBetween).boxed().map(barMapper);


        for (int offset = 0; offset <= daysBetween; offset++) {
            Instant curr = from.plus(offset, ChronoUnit.DAYS);
            if (curr.isAfter(to)) {
                throw new AppRuntimeException("curr.isAfter(to)");
            }
            if (curr.atZone(ZoneOffset.UTC).getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
                continue;
            }
            AtomicDouble open = new AtomicDouble();
            AtomicDouble high = new AtomicDouble();
            AtomicDouble low = new AtomicDouble();
            AtomicDouble close = new AtomicDouble();

            Timestamp currTimestamp = Timestamp.from(curr);
            iteratorMap.forEach((barStream, weight) -> {
                Bar bar = barStream.next();
//                if (!bar.getTimestamp().equals(currTimestamp)) {
//                    throw new AppRuntimeException(
//                            "Malformed data for instrument " + bar.getSymbol() +
//                            ". Timestamp should be " + currTimestamp + " but was " + bar.getTimestamp());
//                }
                open.addAndGet(bar.getOpen() * weight);
                high.addAndGet(bar.getHigh() * weight);
                low.addAndGet(bar.getLow() * weight);
                close.addAndGet(bar.getClose() * weight);
            });
            Bar bar = new Bar(basketCurrency,
                              currTimestamp,
                              open.get(),
                              high.get(),
                              low.get(),
                              close.get(),
                              Long.MAX_VALUE,
                              REF_CURRENCY);

            LOG.info(curr + ": " + bar);
        }

        return null;
    }


    public Stream<Bar> createOld(Instant from, Instant to) {
        checkArgument(!from.isAfter(to), "'from' is after 'to'");

        Map<Iterator<Bar>, Double> iteratorMap = getStreamIteratorMapForBasket(basketComposition, from, to);

        Instant curr = from;
        while (!curr.isAfter(to)) {
            if (curr.atZone(ZoneOffset.UTC).getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
                curr = curr.plus(1, ChronoUnit.DAYS);
                continue;
            }
            AtomicDouble open = new AtomicDouble();
            AtomicDouble high = new AtomicDouble();
            AtomicDouble low = new AtomicDouble();
            AtomicDouble close = new AtomicDouble();

            Timestamp currTimestamp = Timestamp.from(curr);
            iteratorMap.forEach((barStream, weight) -> {
                Bar bar = barStream.next();
                if (!bar.getTimestamp().equals(currTimestamp)) {
                    throw new AppRuntimeException(
                            "Malformed data for instrument " + bar.getSymbol() +
                            ". Timestamp should be " + currTimestamp + " but was " + bar.getTimestamp());
                }
                open.addAndGet(bar.getOpen() * weight);
                high.addAndGet(bar.getHigh() * weight);
                low.addAndGet(bar.getLow() * weight);
                close.addAndGet(bar.getClose() * weight);
            });
            Bar bar = new Bar(basketCurrency,
                              currTimestamp,
                              open.get(),
                              high.get(),
                              low.get(),
                              close.get(),
                              Long.MAX_VALUE,
                              REF_CURRENCY);

            LOG.info(curr + ": " + bar);
            curr = curr.plus(1, ChronoUnit.DAYS);
        }

        return null;
    }


    private Map<Iterator<Bar>, Double> getStreamIteratorMapForBasket(Map<Instrument, Double> basketComposition,
                                                                     Instant from,
                                                                     Instant to) {
        Map<Iterator<Bar>, Double> iteratorMap = new HashMap<>();
        basketComposition.forEach((instrument, weight) -> {
            ITimeSeriesFeed<Bar> feed = TimeSeriesFeedFactory.getDailyBarFeed(instrument, REF_CURRENCY);
            try {
                iteratorMap.put(feed.get(from, to).iterator(), weight);
            }
            catch (IOException e) {
                throw new AppRuntimeException(e);
            }
        });

        return iteratorMap;
    }


    protected Map<Instrument, Double> convertCurrencyMapToInstrumentMap(Map<Currency, Double> currencyMap) {
        Map<Instrument, Double> instrumentMap = new HashMap<>();
        currencyMap.forEach((currency, weight) -> instrumentMap.put(currencyToInstrument(currency), weight));

        return instrumentMap;
    }

    private Instrument currencyToInstrument(Currency currency) {
        return Instrument.of(currency.getCode() + REF_CURRENCY.getCode());
    }
}
