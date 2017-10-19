package org.jinvestor.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jinvestor.datasource.IConverter;

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
