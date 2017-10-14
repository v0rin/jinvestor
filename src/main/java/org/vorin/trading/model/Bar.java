package org.vorin.trading.model;

import java.time.Instant;

/**
*
* @author vorin
*/
public class Bar {

	public static String DATE_TIME_COL_NAME = "datetime";
	public static String OPEN_COL_NAME = "open";
	public static String HIGH_COL_NAME = "high";
	public static String LOW_COL_NAME = "low";
	public static String CLOSE_COL_NAME = "close";
	public static String VOLUME_COL_NAME = "volume";

	public Instant dateTime;

	public Double open;
	public Double high;
	public Double low;
	public Double close;


	public Long volume;	public Bar(Instant dateTime, Double open, Double high, Double low, Double close, Long volume) {
		this.dateTime = dateTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
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
		result = prime * result + ((close == null) ? 0 : close.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bar other = (Bar) obj;
		if (close == null) {
			if (other.close != null)
				return false;
		} else if (!close.equals(other.close))
			return false;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		if (volume == null) {
			if (other.volume != null)
				return false;
		} else if (!volume.equals(other.volume))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bar [" +
				  "dateTime=" + dateTime +
				  ", open=" + open +
				  ", high=" + high +
				  ", low=" + low +
				  ", close=" + close +
				  ", volume=" + volume +
				  "]";
	}
}
