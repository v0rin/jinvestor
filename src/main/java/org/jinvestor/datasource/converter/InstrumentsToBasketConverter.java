package org.jinvestor.datasource.converter;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jinvestor.model.Bar;
import org.jinvestor.time.TimestampUtil;

import com.google.common.util.concurrent.AtomicDouble;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Adam
 */
public class InstrumentsToBasketConverter implements IConverter<List<Bar>, Bar> {

    private static final int DEFAULT_BAR_NOT_PRESENT_FOR_N_DAYS_WARNING_THRESHOLD = 12;

    private static final double INSTRUMENT_COUNT_THRESHOLD_RATIO = 0.5;

    private String basketName;
    private Map<String, Double> basketComposition;
    private Map<String, Bar> lastBars;
    private double instrumentCountThreshold;
    private String refCurrency;
    private double refCurrencyWeight;
    private int barNotPresentForNDaysWarningThreshold = DEFAULT_BAR_NOT_PRESENT_FOR_N_DAYS_WARNING_THRESHOLD;

    private int firstIncorrectBarsCount;


    public InstrumentsToBasketConverter(String basketName, String refCurrency, Map<String, Double> basketComposition) {
        checkBasketComposition(refCurrency, basketComposition);
        this.basketName = basketName;
        this.refCurrency = refCurrency;
        this.refCurrencyWeight = basketComposition.get(refCurrency);
        this.basketComposition = basketComposition.entrySet()
                                                  .stream()
                                                  .filter(e -> e.getKey() != refCurrency)
                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.instrumentCountThreshold = (double)this.basketComposition.size() * INSTRUMENT_COUNT_THRESHOLD_RATIO;
        this.lastBars = new HashMap<>();
    }


    @Override
    public Bar apply(List<Bar> bars) {
        if (firstIncorrectBarsCount >= 0 && !areAllBarsPresent(bars, lastBars)) {
            firstIncorrectBarsCount++;
            if (firstIncorrectBarsCount >= barNotPresentForNDaysWarningThreshold) {
                throw new IllegalStateException("There wasnt't all bars present in the first " +
                                                barNotPresentForNDaysWarningThreshold + " bars");
            }
            bars.forEach(bar -> lastBars.put(bar.getSymbol(), bar));
            return null;
        }
        firstIncorrectBarsCount = -1;

        Set<String> symbolsToProcess = basketComposition.keySet().stream().collect(Collectors.toSet());

        // creates a basket bar only if the number of existing bars for the timestamp exceeds the given threshold
        if (bars.size() < instrumentCountThreshold) {
            return null;
        }

        Timestamp currentTimestamp = bars.get(0).getTimestamp();
        AtomicBar basketBar = new AtomicBar(refCurrency, refCurrencyWeight, currentTimestamp, basketName);
        bars.forEach(bar -> {
            basketBar.updateFromBarWithWeight(bar, basketComposition.get(bar.getSymbol()));

            symbolsToProcess.remove(bar.getSymbol());
            lastBars.put(bar.getSymbol(), bar);
        });

        symbolsToProcess.forEach(symbol -> {
            Bar bar = lastBars.get(symbol);
            checkLastBarTimestampNotTooOld(bar, currentTimestamp);

            basketBar.updateFromBarWithWeight(bar, basketComposition.get(bar.getSymbol()));
        });

        return basketBar.get();
    }


    private boolean areAllBarsPresent(List<Bar> bars, Map<String, Bar> lastBars2) {
        Set<String> presentBarsForSymbols = new HashSet<>();
        presentBarsForSymbols.addAll(lastBars2.keySet());
        presentBarsForSymbols.addAll(bars.stream().map(Bar::getSymbol).collect(Collectors.toList()));
        return presentBarsForSymbols.containsAll(basketComposition.keySet());
    }


    public void setBarNotPresentForNDaysWarningThreshold(int barNotPresentForNDaysWarningThreshold) {
        this.barNotPresentForNDaysWarningThreshold = barNotPresentForNDaysWarningThreshold;
    }


    private static class AtomicBar {
        private AtomicDouble open = new AtomicDouble();
        private AtomicDouble high = new AtomicDouble();
        private AtomicDouble low = new AtomicDouble();
        private AtomicDouble close = new AtomicDouble();

        private String symbol;
        private Timestamp timestamp;
        private String currencyCode;

        AtomicBar(String symbol, double refCurrencyWeight, Timestamp timestamp, String currencyCode) {
            this.symbol = symbol;
            this.timestamp = timestamp;
            this.currencyCode = currencyCode;

            updateFromBarWithWeight(new Bar(null, null, 1d, 1d, 1d, 1d, null, null), refCurrencyWeight);
        }

        Bar get() {
            return new Bar(symbol, timestamp,
                           1 / open.get(), 1 / high.get(), 1 / low.get(), 1 / close.get(),
                           Long.MAX_VALUE, currencyCode);
        }

        void updateFromBarWithWeight(Bar bar, Double weight) {
            open.addAndGet(bar.getOpen() * weight);
            high.addAndGet(bar.getHigh() * weight);
            low.addAndGet(bar.getLow() * weight);
            close.addAndGet(bar.getClose() * weight);
        }
    }


    private void checkLastBarTimestampNotTooOld(Bar bar, Timestamp currentTimestamp) {
        Timestamp timestamp = TimestampUtil.addTo(bar.getTimestamp(),
                                                  barNotPresentForNDaysWarningThreshold,
                                                  ChronoUnit.DAYS);
        checkArgument(timestamp.after(currentTimestamp),
                      "Bar [" + bar + "] not present for more than " + barNotPresentForNDaysWarningThreshold +
                      " days. Current timestamp is " + currentTimestamp);
    }


    private void checkBasketComposition(String refCurrency2, Map<String, Double> basketComposition2) {
        checkArgument(basketComposition2.containsKey(refCurrency2),
                     "Reference currency not present in the basket - " + basketComposition2);
        checkArgument(basketComposition2.size() > 1,
                      "There should be more than 1 symbol in the basket. It doesn't make sense otherwise" +
                      basketComposition2);
        double weightSum = basketComposition2.values().stream().mapToDouble(Double::doubleValue).sum();
        checkArgument(weightSum == 1d);
    }
}
