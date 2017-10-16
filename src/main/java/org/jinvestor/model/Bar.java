package org.jinvestor.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
*
* @author Adam
*/
@Entity
@Table(name="bars")
public class Bar {

	public static final String TABLE_NAME = "bars";
	public static final String SYMBOL_COL_NAME = "symbol";
	public static final String DATE_TIME_COL_NAME = "datetime";
	public static final String OPEN_COL_NAME = "open";
	public static final String HIGH_COL_NAME = "high";
	public static final String LOW_COL_NAME = "low";
	public static final String CLOSE_COL_NAME = "close";
	public static final String VOLUME_COL_NAME = "volume";

	@Column(name="symbol", columnDefinition="TEXT")
	public String symbol;
	@Column(name="datetime", columnDefinition="TEXT")
	public Instant dateTime;
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

	public Bar(String symbol,
			   Instant dateTime,
			   Double open,
			   Double high,
			   Double low,
			   Double close,
			   Long volume) {
		this.symbol = symbol;
		this.dateTime = dateTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public static String getCreateTableSql() {
	    return SQLiteQueryBuilder
		        .create()
		        .table(TABLE_NAME)
		        .column(new com.alexfu.sqlitequerybuilder.api.Column(SYMBOL_COL_NAME, ColumnType.TEXT))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(DATE_TIME_COL_NAME, ColumnType.TEXT))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(OPEN_COL_NAME, ColumnType.REAL))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(HIGH_COL_NAME, ColumnType.REAL))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(LOW_COL_NAME, ColumnType.REAL))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(CLOSE_COL_NAME, ColumnType.REAL))
				.column(new com.alexfu.sqlitequerybuilder.api.Column(VOLUME_COL_NAME, ColumnType.INTEGER))
				.toString();
	}

	public String getSymbol() {
		return symbol;
	}
	public Instant getDateTime() {
		return dateTime;
	}
	public Double getOpen() {
		return open;
	}
	public Double getHigh() {
		return high;
	}
	public Double getLow() {
		return low;
	}
	public Double getClose() {
		return close;
	}
	public Long getVolume() {
		return volume;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + ((close == null) ? 0 : close.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		Bar rhs = (Bar) obj;

		return new EqualsBuilder()
				       .append(symbol, rhs)
				       .append(dateTime, rhs)
				       .append(open, rhs)
				       .append(high, rhs)
				       .append(low, rhs)
				       .append(close, rhs)
				       .append(volume, rhs)
				       .isEquals();
	}

	@Override
	public String toString() {
		return "Bar [symbol=" + symbol + ", dateTime=" + dateTime + ", open=" + open + ", high=" + high + ", low=" + low
				+ ", close=" + close + ", volume=" + volume + "]";
	}


}
