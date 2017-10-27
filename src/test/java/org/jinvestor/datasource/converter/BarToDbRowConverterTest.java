package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.time.Instant;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Instruments;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.junit.Test;

public class BarToDbRowConverterTest {

    @Test
    public void shouldReturnCorrectObjects() {
        // given
        Timestamp timestamp = Timestamp.from(Instant.now());
        Object[] expected = new Object[] {Instruments.SPY, timestamp, 1d, 1d, 1d, 1d, 1L, Instruments.USD};
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
