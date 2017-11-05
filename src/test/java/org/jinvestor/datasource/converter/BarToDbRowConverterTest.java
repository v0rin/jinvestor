package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;

import org.jinvestor.model.Bar;
import org.jinvestor.model.Instruments;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.junit.Test;

public class BarToDbRowConverterTest {

    private static final String TIMESTAMP = "2017-11-06 07:00:24.355";

    @Test
    public void shouldReturnCorrectObjects() {
        // given
        Timestamp timestamp = Timestamp.valueOf(TIMESTAMP);
        Object[] expected = new Object[] {Instruments.SPY, TIMESTAMP, 1d, 1d, 1d, 1d, 1L, Instruments.USD};
        Bar bar = new Bar(Instruments.SPY, timestamp, 1d, 1d, 1d, 1d, 1L, Instruments.USD);
        IConverter<Bar, Object[]> converter = new BarToDbRowConverter();

        // when
        Object[] actual = converter.apply(bar);

        // then
        assertThat(actual, is(expected));
        int columnCount = EntityMetaDataFactory.get(Bar.class).getColumns().length;
        assertThat(columnCount, is(expected.length));
    }
}
