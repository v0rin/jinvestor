package org.jinvestor.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TimestampStreamTest {
    private static final Instant FROM = Instant.parse("2000-01-01T23:59:59.999Z");


    @Test
    public void shouldCreateCorrectStream() {
        // given
        Instant to = Instant.parse("2000-01-02T23:59:59.999Z");
        List<Timestamp> expected = Arrays.asList(Timestamp.valueOf("2000-01-01 23:59:59.999"),
                                                 Timestamp.valueOf("2000-01-02 23:59:59.999"));

        // when
        List<Timestamp> actual = TimestampStream.rangeClosed(FROM, to).collect(Collectors.toList());

        // then
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldCreateEmptyStream() {
        // given
        Instant to = Instant.parse("1999-12-31T23:59:59.999Z");

        // when
        List<Timestamp> actual = TimestampStream.rangeClosed(FROM, to).collect(Collectors.toList());

        // then
        assertThat(actual, is(empty()));
    }
}
