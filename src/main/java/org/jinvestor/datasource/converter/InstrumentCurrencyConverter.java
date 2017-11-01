package org.jinvestor.datasource.converter;

import java.util.Iterator;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Adam
 */
public class InstrumentCurrencyConverter implements IConverter<Bar, Bar> {

    private Iterator<Bar> targetCurrencyIter;

    private Bar lastBar;

    public InstrumentCurrencyConverter(Stream<Bar> targetCurrencyStream) {
        this.targetCurrencyIter = targetCurrencyStream.iterator();
    }

    @Override
    public Bar apply(Bar bar) {
        Bar targetCurrencyBar = lastBar;
        while ((targetCurrencyBar == null || targetCurrencyBar.getTimestamp().before(bar.getTimestamp()))
                && targetCurrencyIter.hasNext()) {
            // targetCurrencyBar needs to catch up to the bar
            targetCurrencyBar = targetCurrencyIter.next();
        }

        if (targetCurrencyBar == null) {
            throw new IllegalStateException("It seems that there is no bars for the target currency");
        }

        Bar convertedBar = null;
        if (!targetCurrencyBar.getTimestamp().after(bar.getTimestamp())) {
            convertedBar = createBarWithConvertedCurrency(bar, targetCurrencyBar);
        }
        else if (lastBar != null && !lastBar.getTimestamp().after(bar.getTimestamp())) {
            convertedBar = createBarWithConvertedCurrency(bar, lastBar);
        }

        lastBar = targetCurrencyBar;

        return convertedBar;
    }

    private Bar createBarWithConvertedCurrency(Bar sourceBar, Bar conversionBar) {
        checkCompatibility(sourceBar, conversionBar);
        return new Bar(sourceBar.getSymbol(),
                       sourceBar.getTimestamp(),
                       sourceBar.getOpen() * conversionBar.getOpen(),
                       sourceBar.getHigh() * conversionBar.getHigh(),
                       sourceBar.getLow() * conversionBar.getLow(),
                       sourceBar.getClose() * conversionBar.getClose(),
                       sourceBar.getVolume(),
                       conversionBar.getCurrencyCode());

    }

    private void checkCompatibility(Bar sourceBar, Bar conversionBar) {
        checkArgument(sourceBar.getCurrencyCode().equals(conversionBar.getSymbol()));
    }
}
