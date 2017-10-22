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


    public Bar() {
    }

    @SuppressFBWarnings(value="EI_EXPOSE_REP2")
    public Bar(String symbol,
               Timestamp dateTime,
               Double open,
               Double high,
               Double low,
               Double close,
               Long volume) {
        this.symbol = symbol;
        this.timestamp = dateTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
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
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        Bar rhs = (Bar) obj;

        return new EqualsBuilder()
                       .append(symbol, rhs)
                       .append(timestamp, rhs)
                       .append(open, rhs)
                       .append(high, rhs)
                       .append(low, rhs)
                       .append(close, rhs)
                       .append(volume, rhs)
                       .isEquals();
    }

    @Override
    public String toString() {
        return "Bar [symbol=" + symbol + ", timestamp=" + timestamp + ", open=" + open + ", high=" + high + ", "
                + "low=" + low + ", close=" + close + ", volume=" + volume + "]";
    }


}
