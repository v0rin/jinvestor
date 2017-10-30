package org.jinvestor.time;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TimestampUtilTest {

    public Object[] timesFromInstant() {
        return new Object[] {
            new Object[] {Instant.parse("2000-01-01T23:59:59.999Z"), Timestamp.valueOf("2000-01-01 23:59:59.999")},
            new Object[] {Instant.parse("2000-08-01T23:59:59.999Z"), Timestamp.valueOf("2000-08-01 23:59:59.999")}
        };
    }

    @Test
    @Parameters(method="timesFromInstant")
    public void shouldGetTimestampFromInstantInUTC(Instant instant, Timestamp expected) {
        // given
        // when
        Timestamp actual = TimestampUtil.fromInstantInUTC(instant);

        // then
        assertThat(actual, is(expected));
    }

    public Object[] timesAddTo() {
        return new Object[] {
            new Object[] {Timestamp.valueOf("2000-01-01 23:59:59.999"), Timestamp.valueOf("2000-01-03 23:59:59.999")},
            new Object[] {Timestamp.valueOf("2000-08-01 00:00:00.000"), Timestamp.valueOf("2000-08-03 00:00:00.000")}
        };
    }

    @Test
    @Parameters(method="timesAddTo")
    public void shouldAddToTimestamp(Timestamp timestamp, Timestamp expected) {
        // given
        // when
        Timestamp actual = TimestampUtil.addTo(timestamp, 2, ChronoUnit.DAYS);

        // then
        assertThat(actual, is(expected));
    }
}
