package org.jinvestor.time;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DateTimeConverterTest {

    private static final String DATE_TIME_1 = "2017-10-25 23:59:59.999";
    private static final String DATE_TIME_2 = "2017-10-25 00:00:00.000";

    protected Object[] dateTimesToConvert() {
        return new Object[] {
            new Object[]{DATE_TIME_1, DATE_TIME_1},
            new Object[]{DATE_TIME_2, DATE_TIME_2}
        };
    }

    @Test
    @Parameters(method="dateTimesToConvert")
    public void shouldCorrectlyConvertDateTime(String dateTimeStringToConvert, String expected) {
        // given
        DateTimeFormatter timestampFormatter = DateTimeFormatterFactory.standardTimestamp();
        IDateTimeConverter<String, String> converter =
                new DateTimeStringConverter(timestampFormatter, timestampFormatter);

        // when
        String actual = converter.apply(dateTimeStringToConvert);

        // then
        assertThat(actual, is(expected));
    }
}
