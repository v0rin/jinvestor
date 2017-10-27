package org.jinvestor.model;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jinvestor.timeseriesfeed.BarFeed;
import org.jinvestor.timeseriesfeed.TimeSeriesFreq;

/**
 * @author Adam
 */
public class Instrument implements IInstrument {

    private String symbol;
    private List<String> aliases;
    private String currencyCode;
    private String description;

    public Instrument(String symbol, String currencyCode) {
        this.symbol = symbol;
        this.aliases = new ArrayList<>();
        this.aliases.add(symbol);
        this.currencyCode = currencyCode;
        this.description = symbol;
    }

    @Override
    public Stream<Bar> streamDaily(Instant from, Instant to) throws IOException {
        return stream(from, to, TimeSeriesFreq.DAILY);
    }

    @Override
    public Stream<Bar> stream(Instant from, Instant to, TimeSeriesFreq frequency) throws IOException {
        return new BarFeed(frequency, this).get(from, to);
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
