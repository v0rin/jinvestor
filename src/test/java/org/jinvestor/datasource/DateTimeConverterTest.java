package org.jinvestor.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.junit.Test;

public class DateTimeConverterTest {

	private static final String INPUT_TIME = "1993-01-01";
	private static final String EXPECTED_OUTPUT_TIME = "1993-01-01 00:00:00.000";

	@Test
	public void testSimpleConversion() {
		// given
		DateTimeFormatter fromDateTimeFormatter = new DateTimeFormatterBuilder()
											        .appendPattern("yyyy-MM-dd")
											        .optionalStart()
											        .appendPattern(" HH:mm")
											        .optionalEnd()
											        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
											        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
											        .toFormatter();
		DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		IConverter<String, String> converter = new DateTimeConverter(fromDateTimeFormatter, toDateTimeFormatter);

		// when
		String actual = converter.apply(INPUT_TIME);

		// then
		assertThat(actual, is(EXPECTED_OUTPUT_TIME));
	}
}
