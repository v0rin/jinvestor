package org.jinvestor.time;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Adam
 */
public class TimestampUtil {

    private TimestampUtil() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public static Timestamp fromInstantInUTC(Instant instant) {
        return Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    public static Timestamp addTo(Timestamp timestamp, int offset, ChronoUnit unit) {
        LocalDateTime ldt = LocalDateTime
                                .ofInstant(timestamp.toInstant(), ZoneOffset.systemDefault())
                                .plus(offset, unit);
        return fromInstantInUTC(ldt.toInstant(ZoneOffset.UTC));
    }
}
