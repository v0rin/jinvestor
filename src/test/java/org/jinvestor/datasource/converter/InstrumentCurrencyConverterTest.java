package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;
import org.jinvestor.time.TimestampUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;


@RunWith(JUnitParamsRunner.class)
public class InstrumentCurrencyConverterTest {

    private static final Instant FROM = Instant.parse("2017-01-01T23:59:59.999Z");

    private static final String SYMBOL = SPY;
    private static final String CURRENCY = USD;
    private static final String CURRENCY_TO_CONVERT_TO = BC1;


    @SuppressWarnings("checkstyle:magicnumber")
    protected Object[] input() {
        return new Object[] {
            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{1, 2, 3}, new double[]{1d, 4d, 9d})},

            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{1, 2   }, new double[]{1d, 2d    }),
                         expectedBarArray(   new int[]{1, 2, 3}, new double[]{1d, 4d, 6d})},

            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{1,    3}, new double[]{1d,     3d}),
                         expectedBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 9d})},

            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{1      }, new double[]{1d        }),
                         expectedBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d})},

            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{   2, 3}, new double[]{    2d, 3d}),
                         expectedBarArray(   new int[]{   2, 3}, new double[]{    4d, 9d})},

            new Object[]{straightBarArray(   new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         conversionBarStream(new int[]{      3}, new double[]{        3d}),
                         expectedBarArray(   new int[]{      3}, new double[]{        9d})},

            new Object[]{straightBarArray(   new int[]{   2, 3}, new double[]{    2d, 3d}),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{   2, 3}, new double[]{    4d, 9d})},

            new Object[]{straightBarArray(   new int[]{1,    3}, new double[]{1d,     3d}),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{1,    3}, new double[]{1d,     9d})},

            new Object[]{straightBarArray(   new int[]{1, 2   }, new double[]{1d, 2d    }),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{1, 2   }, new double[]{1d, 4d    })},

            new Object[]{straightBarArray(   new int[]{      3}, new double[]{        3d}),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{      3}, new double[]{        9d})},

            new Object[]{straightBarArray(   new int[]{   2   }, new double[]{    2d    }),
                         conversionBarStream(new int[]{1, 2, 3}, new double[]{1d, 2d, 3d}),
                         expectedBarArray(   new int[]{   2   }, new double[]{    4d    })},
        };
    }


    @Test
    @Parameters(method="input")
    public void shouldCorrectlyConvert(List<Bar> straightBars, Stream<Bar> conversionBars, List<Bar> expected) {
        // given
        IConverter<Bar, Bar> converter = new InstrumentCurrencyConverter(conversionBars);

        // when
        List<Bar> actual = new ArrayList<>();
        for (Bar bar : straightBars) {
            Bar convertedBar = converter.apply(bar);
            if (convertedBar != null) {
                actual.add(new TestBar(convertedBar));
            }
        }

        // then
        assertThat(actual, is(expected));
    }


    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldCorrectlyResolveAllBarValues() {
        // given
        Timestamp timestamp = Timestamp.from(FROM);
        Bar straightBar = new Bar(SYMBOL, timestamp, 1d, 2d, 3d, 4d, 1L, CURRENCY);
        Stream<Bar> conversionBar = Stream.of(new Bar(CURRENCY, timestamp,
                                                      3d, 5d, 7d, 9d, 2L, CURRENCY_TO_CONVERT_TO));
        Bar expected = new Bar(SYMBOL, timestamp, 3d, 10d, 21d, 36d, 1L, CURRENCY_TO_CONVERT_TO);

        IConverter<Bar, Bar> converter = new InstrumentCurrencyConverter(conversionBar);

        // when
        Bar actual = converter.apply(straightBar);

        // then
        assertThat(actual, is(expected));
    }


    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldThrowExceptionWhenIncompatiblePairs() {
        // given
        Timestamp timestamp = Timestamp.from(FROM);
        Bar straightBar = new Bar(SYMBOL, timestamp, 1d, 2d, 3d, 4d, 1L, CURRENCY);
        Stream<Bar> conversionBar = Stream.of(new Bar("WRONG_CURRENCY", timestamp,
                                              3d, 5d, 7d, 9d, 2L, CURRENCY_TO_CONVERT_TO));

        IConverter<Bar, Bar> converter = new InstrumentCurrencyConverter(conversionBar);

        // when
        converter.apply(straightBar);

        // then throws exception
    }


    private Stream<Bar> conversionBarStream(int[] dayOffsets, double[] prices) {
        return barArray(CURRENCY, dayOffsets, prices, CURRENCY_TO_CONVERT_TO).stream();
    }


    private List<Bar> straightBarArray(int[] dayOffsets, double[] prices) {
        return barArray(SYMBOL, dayOffsets, prices, CURRENCY);
    }


    private List<Bar> expectedBarArray(int[] dayOffsets, double[] prices) {
        return barArray(SYMBOL, dayOffsets, prices, CURRENCY_TO_CONVERT_TO);
    }


    private List<Bar> barArray(String symbol, int[] dayOffsets, double[] prices, String currency) {
        List<Bar> bars = new ArrayList<>();
        for (int i = 0; i < dayOffsets.length; i++) {
            bars.add((Bar)new TestBar(symbol, dayOffsets[i], prices[i], currency));
        }
        return bars;
    }


    private static class TestBar extends Bar {
        private int dayOffset;

        TestBar(String symbol, int dayOffset, double price, String currency) {
            super(symbol, getAdjustedTimestamp(dayOffset), price, price, price, price, Long.MAX_VALUE, currency);
            this.dayOffset = dayOffset;
        }

        TestBar(Bar bar) {
            super(bar.getSymbol(), bar.getTimestamp(),
                  bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose(),
                  bar.getVolume(), bar.getCurrencyCode());
            this.dayOffset = (int) ChronoUnit.DAYS.between(FROM, bar.getTimestamp().toInstant());
        }

        private static Timestamp getAdjustedTimestamp(int dayOffset) {
            Instant fromShifted = FROM.plus(dayOffset, ChronoUnit.DAYS);
            Timestamp timestamp = TimestampUtil.fromInstantInUTC(fromShifted);
            return timestamp;
        }

        @Override
        public String toString() {
            return "[day=" + dayOffset + ", price=" + open + "]";
        }
    }
}
