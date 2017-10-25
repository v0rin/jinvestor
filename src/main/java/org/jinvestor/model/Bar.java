package org.jinvestor.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
*
* @author Adam
*/
@Entity
@Table(name="bar")
public class Bar {

    @Column(name="symbol", columnDefinition="TEXT")
    public String symbol;

    @Column(name="timestamp", columnDefinition="TEXT")
    public Timestamp timestamp;

    @Column(name="open", columnDefinition="REAL")
    public Double open;

    @Column(name="high", columnDefinition="REAL")
    public Double high;

    @Column(name="low", columnDefinition="REAL")
    public Double low;

    @Column(name="close", columnDefinition="REAL")
    public Double close;

    @Column(name="volume", columnDefinition="INTEGER")
    public Long volume;

    @Column(name="currency", columnDefinition="TEXT")
    public Currency currency;


    public Bar() {
    }

    @SuppressWarnings({"checkstyle:parameternumber", "squid:S00107"})
    public Bar(Instrument instrument,
               Timestamp dateTime,
               Double open,
               Double high,
               Double low,
               Double close,
               Long volume,
               Currency currency) {
        this(instrument.getId(), dateTime, open, high, low, close, volume, currency.getCode());
    }

    @SuppressWarnings({"checkstyle:parameternumber", "squid:S00107"})
    public Bar(String symbol,
               Timestamp dateTime,
               Double open,
               Double high,
               Double low,
               Double close,
               Long volume,
               Currency currency) {
        this(symbol, dateTime, open, high, low, close, volume, currency.getCode());
    }


    @SuppressWarnings({"checkstyle:parameternumber", "squid:S00107"})
    public Bar(Instrument instrument,
               Timestamp dateTime,
               Double open,
               Double high,
               Double low,
               Double close,
               Long volume,
               String currency) {
        this(instrument.getId(), dateTime, open, high, low, close, volume, currency);
    }

    @SuppressWarnings({"checkstyle:parameternumber", "squid:S00107"})
    public Bar(String symbol,
               Timestamp dateTime,
               Double open,
               Double high,
               Double low,
               Double close,
               Long volume,
               String currency) {
        this.symbol = symbol;
        this.timestamp = dateTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.currency = new Currency(currency);
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Timestamp getTimestamp() {
        return new Timestamp(timestamp.getTime());
    }

    @SuppressFBWarnings(value="EI_EXPOSE_REP2")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((open == null) ? 0 : open.hashCode());
        result = prime * result + ((high == null) ? 0 : high.hashCode());
        result = prime * result + ((low == null) ? 0 : low.hashCode());
        result = prime * result + ((close == null) ? 0 : close.hashCode());
        result = prime * result + ((volume == null) ? 0 : volume.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Bar)) return false;
        Bar rhs = (Bar) obj;

        return new EqualsBuilder()
                        .append(this.symbol, rhs.symbol)
                        .append(this.timestamp, rhs.timestamp)
                        .append(this.open, rhs.open)
                        .append(this.high, rhs.high)
                        .append(this.low, rhs.low)
                        .append(this.close, rhs.close)
                        .append(this.volume, rhs.volume)
                        .append(this.currency, rhs.currency)
                        .isEquals();

    }

    @Override
    public String toString() {
        return "Bar [symbol=" + symbol + ", timestamp=" + timestamp + ", open=" + open + ", high=" + high + ", "
                + "low=" + low + ", close=" + close + ", volume=" + volume + ", currency=" + currency + "]";
    }
}
