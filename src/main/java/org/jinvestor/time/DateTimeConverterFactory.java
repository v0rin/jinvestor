package org.jinvestor.time;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author Adam
 */
public class DateTimeConverterFactory {

    private DateTimeConverterFactory() {
        throw new InstantiationError("This class should not instantiated");
    }

    /**
     * @return {@link DateTimeStringConverter} that converts {@code yyyy-MM-dd} to {@code yyyy-MM-dd 23:59:59.999}
     */
    public static IDateTimeConverter<String, String> getDateToDateTimeEodConverter() {
        DateTimeFormatter fromDateTimeFormatter = DateTimeFormatterFactory.dateToDateTimeEod();
        DateTimeFormatter toDateTimeFormatter = DateTimeFormatterFactory.standardTimestamp();

        return new DateTimeStringConverter(fromDateTimeFormatter, toDateTimeFormatter);
    }
}
