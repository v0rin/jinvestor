package org.jinvestor.datasource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Adam
 */
public class DateTimeConverter implements IConverter<String, String> {

	private DateTimeFormatter fromDateTimeFormatter;
	private DateTimeFormatter toDateTimeFormatter;

	public DateTimeConverter(DateTimeFormatter fromDateTimeFormatter, DateTimeFormatter toDateTimeFormatter) {
		this.fromDateTimeFormatter = fromDateTimeFormatter;
		this.toDateTimeFormatter = toDateTimeFormatter;
	}

	@Override
	public String apply(String t) {
		LocalDateTime ld = LocalDateTime.parse(t, fromDateTimeFormatter);
		return ld.format(toDateTimeFormatter);
	}
}
