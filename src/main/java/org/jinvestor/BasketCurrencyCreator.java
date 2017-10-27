package org.jinvestor;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
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
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instruments;

import com.google.common.util.concurrent.AtomicDouble;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class BasketCurrencyCreator implements IBasketCurrencyCreator {

    private static final Logger LOG = LogManager.getLogger();

    private static final String REF_CURRENCY_CODE = Instruments.USD;

    private IInstrument basketCurrency;
    private Map<IInstrument, Double> basketComposition;


    public BasketCurrencyCreator(IInstrument currency, Map<IInstrument, Double> basketComposition) {
        checkArgument(basketComposition.values().stream().mapToDouble(Double::doubleValue).sum() == 1d,
                      "Weights need to add up to 1; basketComposition=" + basketComposition);
        this.basketComposition = basketComposition;
        this.basketCurrency = currency;
    }


    @Override
    public Stream<Bar> create(Instant from, Instant to) {
        checkArgument(!from.isAfter(to), "'from' is after 'to'");

        Map<Iterator<Bar>, Double> iteratorMap = getStreamIteratorMapForBasket(basketComposition, from, to);

        // TODO (AF) should be working days
        // based on the testFxDates
        long daysBetween = ChronoUnit.DAYS.between(from, to);

//        for (int offset = 0; offset <= daysBetween; offset++) {
//        if (curr.atZone(ZoneOffset.UTC).getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
//            continue;
//        }

        Function<Long, Bar> barMapper = getIntToBarMapper(iteratorMap, from);

        return LongStream.rangeClosed(0, daysBetween).boxed().map(barMapper);
    }

    private Function<Long, Bar> getIntToBarMapper(Map<Iterator<Bar>, Double> iteratorMap, Instant from) {
        return offset -> {
            Instant curr = from.plus(offset, ChronoUnit.DAYS);
            AtomicPrices atomicPrices = new AtomicPrices();
            Timestamp currTimestamp = Timestamp.from(curr);
            iteratorMap.forEach((barStream, weight) -> {
                Bar bar = null;
                if (barStream != null) {
                    bar = barStream.next();
                }
                else {
                    bar = new Bar("", currTimestamp, 1d, 1d, 1d, 1d, 0L, REF_CURRENCY_CODE);
                }
//                if (!bar.getTimestamp().equals(currTimestamp)) {
//                    throw new AppRuntimeException(
//                            "Malformed data for instrument " + bar.getSymbol() +
//                            ". Timestamp should be " + currTimestamp + " but was " + bar.getTimestamp());
//                }
                atomicPrices.updateFromBarWithWeight(bar, weight);
            });
            return new Bar(basketCurrency.getSymbol(),
                           currTimestamp,
                           1 / atomicPrices.open.get(),
                           1 / atomicPrices.high.get(),
                           1 / atomicPrices.low.get(),
                           1 / atomicPrices.close.get(),
                           Long.MAX_VALUE,
                           REF_CURRENCY_CODE);
        };
    }

    private static class AtomicPrices {
        AtomicDouble open = new AtomicDouble();
        AtomicDouble high = new AtomicDouble();
        AtomicDouble low = new AtomicDouble();
        AtomicDouble close = new AtomicDouble();

        void updateFromBarWithWeight(Bar bar, Double weight) {
            open.addAndGet(bar.getOpen() * weight);
            high.addAndGet(bar.getHigh() * weight);
            low.addAndGet(bar.getLow() * weight);
            close.addAndGet(bar.getClose() * weight);
        }
    }

    private Map<Iterator<Bar>, Double> getStreamIteratorMapForBasket(Map<IInstrument, Double> basketComposition,
                                                                     Instant from,
                                                                     Instant to) {
        Map<Iterator<Bar>, Double> iteratorMap = new HashMap<>();
        basketComposition.forEach((currency, weight) -> {
            try {
                Iterator<Bar> barIterator = null;
                if (!currency.equals(REF_CURRENCY_CODE)) {
                    barIterator = currency.streamDaily(from, to).iterator();
                }
                // puts a null key if currency is REF_CURRENCY
                iteratorMap.put(barIterator, weight);
            }
            catch (IOException e) {
                throw new AppRuntimeException(e);
            }
        });

        return iteratorMap;
    }
}
