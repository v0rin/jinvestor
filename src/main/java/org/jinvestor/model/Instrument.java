package org.jinvestor.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jinvestor.timeseriesfeed.BarFeed;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.jinvestor.timeseriesfeed.TimeSeriesFreq;

/**
 * @author Adam
 */
public class Instrument implements IInstrument {

    private String symbol;
    private List<String> aliases;
    private String currencyCode;
    private String description;

    private Map<TimeSeriesFreq, ITimeSeriesFeed<Bar>> barFeeds;

    public Instrument(String symbol, String currencyCode) {
        this.symbol = symbol;
        this.aliases = new ArrayList<>();
        this.aliases.add(symbol);
        this.currencyCode = currencyCode;
        this.description = symbol;
        barFeeds = new EnumMap<>(TimeSeriesFreq.class);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarDailyFeed() {
        return getBarFeed(TimeSeriesFreq.DAILY);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarFeed(TimeSeriesFreq frequency) {
        return barFeeds.computeIfAbsent(frequency, freq -> new BarFeed(freq, this));
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Instrument)) {
            return false;
        }
        Instrument rhs = (Instrument) object;
        return new EqualsBuilder().append(this.symbol, rhs.symbol)
                                  .append(this.aliases, rhs.aliases)
                                  .append(this.currencyCode, rhs.currencyCode)
                                  .append(this.description, rhs.description)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(-2103055395, 1354241493)
                    .append(this.symbol)
                    .append(this.aliases)
                    .append(this.currencyCode)
                    .append(this.description)
                    .toHashCode();
    }

    @Override
    public String toString() {
        return "Instrument [symbol=" + symbol + ", aliases=" + aliases + ", currencyCode=" + currencyCode + "]";
    }
}
