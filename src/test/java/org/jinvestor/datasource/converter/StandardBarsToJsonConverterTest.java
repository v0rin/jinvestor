package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;
import org.jinvestor.time.TimestampUtil;
import org.junit.Test;

public class StandardBarsToJsonConverterTest {

    private static final Instant INSTANT = Instant.parse("2017-11-04T00:00:00.000Z");
    private static final Bar TEST_BAR1 =
            new Bar("SPY", TimestampUtil.fromInstantInUTC(INSTANT), 1d, 2d, 3d, 4d, 1L, "USD");

    private IConverter<Stream<Bar>, String> converter = new StandardBarsToJsonConverter();


    @Test
    public void shouldCorrectlyConvertToJson() {
        // given
        Stream<Bar> barStream = Stream.of(TEST_BAR1);
        String expectedJson = "[{\"timestamp\":\"2017-11-04\",\"value\":4.0}]";

        // when
        String actualJson = converter.apply(barStream);

        // then
        assertThat(actualJson, is(expectedJson));
    }
}
