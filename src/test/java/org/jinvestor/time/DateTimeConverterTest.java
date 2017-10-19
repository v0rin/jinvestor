package org.jinvestor.time;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jinvestor.datasource.IConverter;
import org.junit.Test;

public class DateTimeConverterTest {

	private static final String INPUT_TIME = "1993-01-01";
	private static final String EXPECTED_OUTPUT_TIME = "1993-01-01 23:59:59.999999";

	@Test
	public void testSimpleConversion() {
		// given
		DateTimeFormatter fromDateTimeFormatter = new DateTimeFormatterBuilder()
											        .appendPattern("yyyy-MM-dd")
											        .optionalStart()
											        .appendPattern("HH:mm")
											        .optionalEnd()
											        .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
											        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
											        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
											        .parseDefaulting(ChronoField.MICRO_OF_SECOND, 999999)
											        .toFormatter();
		DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

		IConverter<String, String> converter = new DateTimeConverter(fromDateTimeFormatter, toDateTimeFormatter);

		// when
		String actual = converter.apply(INPUT_TIME);

		// then
		assertThat(actual, is(EXPECTED_OUTPUT_TIME));
	}
}
