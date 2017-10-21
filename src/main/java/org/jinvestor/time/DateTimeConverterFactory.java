package org.jinvestor.time;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 *
 * @author Adam
 */
public class DateTimeConverterFactory {

	private DateTimeConverterFactory() {
		throw new InstantiationError("This class should not instantiated");
	}

	/**
	 * @return {@link DateTimeConverter} that converts {@code yyyy-MM-dd} to {@code yyyy-MM-dd 23:59:59.999}
	 */
	public static DateTimeConverter getDateToDateTimeConverter() {
		DateTimeFormatter fromDateTimeFormatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd")
		        .optionalStart()
		        .appendPattern(" HH:mm")
		        .optionalEnd()
		        .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
		        .parseDefaulting(ChronoField.MILLI_OF_SECOND, 999)
		        .toFormatter();
		DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		return new DateTimeConverter(fromDateTimeFormatter, toDateTimeFormatter);
	}
}
