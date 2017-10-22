package org.jinvestor.time;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 *
 * @author Adam
 */
public class DateTimeFormatterFactory {

    private DateTimeFormatterFactory() {
        throw new InstantiationError("This class should not be instantiated");
    }


    public static DateTimeFormatter standardTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public static DateTimeFormatter dateToDateTimeEod() {
        return new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 999)
                .toFormatter()
                .withZone(ZoneOffset.UTC);
    }
}
