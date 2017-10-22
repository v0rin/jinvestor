package org.jinvestor.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Adam
 */
public class DateTimeStringConverter implements IDateTimeConverter<String, String> {

    private DateTimeFormatter fromDateTimeFormatter;
    private DateTimeFormatter toDateTimeFormatter;

    public DateTimeStringConverter(DateTimeFormatter fromDateTimeFormatter, DateTimeFormatter toDateTimeFormatter) {
        this.fromDateTimeFormatter = fromDateTimeFormatter;
        this.toDateTimeFormatter = toDateTimeFormatter;
    }

    @Override
    public String apply(String t) {
        ZonedDateTime zdt = ZonedDateTime.parse(t, fromDateTimeFormatter);
        return zdt.format(toDateTimeFormatter);
    }
}
