package org.jinvestor.datasource.converter;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.Bar;

/**
 *
 * @author Adam
 */
public class BarToDbRowConverter implements IConverter<Bar, Object[]> {

    @Override
    public Object[] apply(Bar bar) {
        return new Object[] {
                bar.getSymbol(),
                bar.getTimestamp(),
                bar.getOpen(),
                bar.getHigh(),
                bar.getLow(),
                bar.getClose(),
                bar.getVolume(),
                bar.getCurrency()
        };
    }
}
