package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.time.Instant;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.datasource.Yahoo;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.time.DateTimeConverterFactory;
import org.junit.Before;
import org.junit.Test;

public class CsvBarToDbRowConverterTest {

    private static final String INSTRUMENT_COL = "Symbol";
    private static final String DATE_COL = "Date";
    private static final String OPEN_COL = "Open";
    private static final String HIGH_COL = "High";
    private static final String LOW_COL = "Low";
    private static final String CLOSE_COL = "Close";
    private static final String VOLUME_COL = "Volume";

    private static final IInstrument INSTRUMENT = new Instrument(SPY, USD);
    private static final String CURRENCY_CODE = EUR;

    private static final String DATE = "2017-01-01";
    private static final Object[] EXPECTED = new Object[] {
        INSTRUMENT.getSymbol(),
        Timestamp.from(Instant.parse(DATE + "T23:59:59.999Z")).toString(),
        "1", "2", "3", "4", "100",
        CURRENCY_CODE};

    private CsvBarToDbRowConverter.Builder converterBuilder;


    @Before
    public void setUp() {
        converterBuilder = new CsvBarToDbRowConverter.Builder(
                Yahoo.getStocksCsvToDbColumnsMappings(),
                DateTimeConverterFactory.getDateToDateTimeEodConverter(),
                CURRENCY_CODE);
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldConvertStandardRowToBar() {
        // given
        String[] csvColumns = new String[] {
            INSTRUMENT_COL, DATE_COL, OPEN_COL, HIGH_COL, LOW_COL, CLOSE_COL, VOLUME_COL};
        int id = 2;
        String[] csvRow = new String[] {INSTRUMENT.getSymbol(),
                                        DATE,
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id]};

        IConverter<String[], Object[]> converter = converterBuilder.build();

        // when
        converter.apply(csvColumns);
        Object[] actual = converter.apply(csvRow);

        // then
        assertThat(actual, is(EXPECTED));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldConvertRowWithoutInstrumentToBar() {
        // given
        String[] csvColumns = new String[] {DATE_COL, OPEN_COL, HIGH_COL, LOW_COL, CLOSE_COL, VOLUME_COL};
        int id = 2;
        String[] csvRow = new String[] {DATE,
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id]};

        IConverter<String[], Object[]> converter = converterBuilder
                .instrument(INSTRUMENT)
                .build();

        // when
        converter.apply(csvColumns);
        Object[] actual = converter.apply(csvRow);

        // then
        assertThat(actual, is(EXPECTED));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldConvertRowWithoutColumnToBar() {
        // given
        String[] csvColumns = new String[] {
            INSTRUMENT_COL, DATE_COL, OPEN_COL, HIGH_COL, LOW_COL, CLOSE_COL};
        int id = 2;
        String[] csvRow = new String[] {INSTRUMENT.getSymbol(),
                                        DATE,
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++]};

        IConverter<String[], Object[]> converter = converterBuilder
                .volume(100L)
                .build();

        // when
        converter.apply(csvColumns);
        Object[] actual = converter.apply(csvRow);

        // then
        assertThat(actual, is(EXPECTED));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldConvertRowWithoutInstrumentAndVolumeToBar() {
        // given
        String[] csvColumns = new String[] {
            DATE_COL, OPEN_COL, HIGH_COL, LOW_COL, CLOSE_COL};
        int id = 2;
        String[] csvRow = new String[] {DATE,
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++],
                                        (String)EXPECTED[id++]};

        IConverter<String[], Object[]> converter = converterBuilder
                .volume(100L)
                .instrument(INSTRUMENT)
                .build();

        // when
        converter.apply(csvColumns);
        Object[] actual = converter.apply(csvRow);

        // then
        assertThat(actual, is(EXPECTED));
    }

}
