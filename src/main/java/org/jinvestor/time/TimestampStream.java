package org.jinvestor.time;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *
 * @author Adam
 */
public class TimestampStream {

    private TimestampStream() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static Stream<Timestamp> rangeClosed(Instant from, Instant to) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        return LongStream.rangeClosed(0, daysBetween).boxed().map(i -> {
            Instant nextInstant = ChronoUnit.DAYS.addTo(from, i);
            return Timestamp.valueOf(LocalDateTime.ofInstant(nextInstant, ZoneOffset.UTC));
        });
    }
}
