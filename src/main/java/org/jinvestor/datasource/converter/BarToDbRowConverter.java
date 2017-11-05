package org.jinvestor.datasource.converter;

import java.time.format.DateTimeFormatter;

import org.jinvestor.model.Bar;
import org.jinvestor.time.DateTimeFormatterFactory;

/**
 *
 * @author Adam
 */
public class BarToDbRowConverter implements IConverter<Bar, Object[]> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatterFactory.standardTimestamp();

    @Override
    public Object[] apply(Bar bar) {
        return new Object[] {
                bar.getSymbol(),
                FORMATTER.format(bar.getTimestamp().toLocalDateTime()),
                bar.getOpen(),
                bar.getHigh(),
                bar.getLow(),
                bar.getClose(),
                bar.getVolume(),
                bar.getCurrencyCode()
        };
    }
}
