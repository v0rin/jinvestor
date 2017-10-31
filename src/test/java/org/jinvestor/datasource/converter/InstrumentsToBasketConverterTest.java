package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.GBP;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.datasource.IReader;
import org.jinvestor.datasource.SyncedBarsReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.time.TimestampUtil;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.common.collect.ImmutableMap;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * @author Adam
 */
@RunWith(JUnitParamsRunner.class)
public class InstrumentsToBasketConverterTest {

    private static final Instant FROM = Instant.parse("2017-01-01T23:59:59.999Z");
    private static final Instant TO = Instant.parse("2017-01-04T23:59:59.999Z");

    private static final String BASKET_CURRENCY = BC1;
    private static final String REF_CURRENCY = USD;
    private static final double REF_CURRENCY_WEIGHT = 0.5;
    private static final String SYM1 = EUR;
    private static final double SYMBOL1_WEIGHT = 0.4;
    private static final String SYM2 = GBP;
    private static final double SYMBOL2_WEIGHT = 0.1;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ITimeSeriesFeed<Bar> testBarFeed1;

    @Mock
    private ITimeSeriesFeed<Bar> testBarFeed2;

    private List<IInstrument> instruments;
    private Map<String, Double> basketComposition;
    private IReader<List<Bar>> syncedBarsReader;


    @Before
    public void setUp() throws IOException {
        instruments = new ArrayList<>();
        instruments.add(new TestInstrument(SYM1, REF_CURRENCY, testBarFeed1));
        instruments.add(new TestInstrument(SYM2, REF_CURRENCY, testBarFeed2));
        basketComposition = ImmutableMap.of(REF_CURRENCY, REF_CURRENCY_WEIGHT,
                                            SYM1, SYMBOL1_WEIGHT,
                                            SYM2, SYMBOL2_WEIGHT);
    }


    @After
    public void tearDown() throws Exception {
        if (syncedBarsReader != null) {
            syncedBarsReader.close();
        }
    }


    @SuppressWarnings("checkstyle:magicnumber")
    protected Object[] correctInputBarStreams() {
        return new Object[] {
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d}),
                barArray(REF_CURRENCY, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d})
            },
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 2}, new Double[]{1d, 1d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d}),
                barArray(REF_CURRENCY, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d})
            },
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 1, 2, 3}, new Double[]{3d, 4d, 1d, 6d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2, 3}, new Double[]{8d, 1d, 6d, 3d}),
                barArray(REF_CURRENCY, new Integer[]{0, 1, 2, 3}, new Double[]{2d/5, 5d/11, 2d/3, 5d/16})
            },
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 1, 2, 3}, new Double[]{3d, 4d, 1d, 6d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2}, new Double[]{8d, 1d, 6d}),
                barArray(REF_CURRENCY, new Integer[]{0, 1, 2, 3}, new Double[]{2d/5, 5d/11, 2d/3, 2d/7})
            },
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 2}, new Double[]{2d, 8d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 3}, new Double[]{8d, 1d, 3d}),
                barArray(REF_CURRENCY, new Integer[]{0, 1, 2, 3}, new Double[]{10d/21, 5d/7, 5d/19, 1d/4})
            },
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{1, 2, 3}, new Double[]{3d, 1d, 6d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 2, 3}, new Double[]{8d, 6d, 3d}),
                barArray(REF_CURRENCY, new Integer[]{null, 1, 2, 3}, new Double[]{null, 2d/5, 2d/3, 5d/16})
            }
        };
    }


    @Test
    @Parameters(method="correctInputBarStreams")
    public void shouldCreateCorrectBasketBars(Stream<Bar> inputBarStream1,
                                              Stream<Bar> inputBarStream2,
                                              List<Bar> expected) throws Exception {
        // given
        BDDMockito.given(testBarFeed1.stream(any(), any())).willReturn(inputBarStream1);
        BDDMockito.given(testBarFeed2.stream(any(), any())).willReturn(inputBarStream2);
        syncedBarsReader = new SyncedBarsReader(instruments, FROM, TO);
        IConverter<List<Bar>, Bar> converter = new InstrumentsToBasketConverter(
                                                    BASKET_CURRENCY, REF_CURRENCY, basketComposition);
        // when
        List<Bar> basketBars = syncedBarsReader.stream().map(converter).collect(Collectors.toList());
        List<TestBar> basketTestBars = basketBars.stream()
                                                 .map(bar -> {
                                                     if (bar == null) return null;
                                                     else return new TestBar(bar);
                                                 })
                                                 .collect(Collectors.toList());
        // then
        assertThat(basketTestBars, is(expected));
    }


    @SuppressWarnings("checkstyle:magicnumber")
    protected Object[] incorrectInputBarStreams() {
        return new Object[] {
            // should report bar not present for more than given number of days
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{0, 1}, new Double[]{1d, 1d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d}),
                IllegalArgumentException.class
            },
            // throw new IllegalStateException("There wasnt't all bars present in the first N bars");
            new Object[] {
                barStreamWithDayOffsets(SYM1, new Integer[]{2}, new Double[]{1d}),
                barStreamWithDayOffsets(SYM2, new Integer[]{0, 1, 2, 3}, new Double[]{1d, 1d, 1d, 1d}),
                IllegalStateException.class
            }
        };
    }


    @Test
    @Parameters(method="incorrectInputBarStreams")
    public void shouldReportIncorrectSituation(Stream<Bar> inputBarStream1,
                                               Stream<Bar> inputBarStream2,
                                               Class<? extends Throwable> expectedExceptionClass) throws Exception {
        // given
        thrown.expect(expectedExceptionClass);
        BDDMockito.given(testBarFeed1.stream(any(), any())).willReturn(inputBarStream1);
        BDDMockito.given(testBarFeed2.stream(any(), any())).willReturn(inputBarStream2);
        syncedBarsReader = new SyncedBarsReader(instruments, FROM, TO);
        InstrumentsToBasketConverter converter = new InstrumentsToBasketConverter(
                                                    BASKET_CURRENCY, REF_CURRENCY, basketComposition);
        converter.setBarNotPresentForNDaysWarningThreshold(2);

        // when
        syncedBarsReader.stream().map(converter).collect(Collectors.toList());

        // then throws an expected exception
    }


    protected Object[] incorrectBasketComposition() {
        return new Object[] {
            // reference currency not in the basket
            new Object[] {
                ImmutableMap.of(SYM1, SYMBOL1_WEIGHT,
                                SYM2, SYMBOL2_WEIGHT),
                IllegalArgumentException.class
            },
            // only one symbol in the basket
            new Object[] {
                ImmutableMap.of(REF_CURRENCY, REF_CURRENCY_WEIGHT),
                IllegalArgumentException.class
            },
            // total sum of weights > 1
            new Object[] {
                ImmutableMap.of(REF_CURRENCY, REF_CURRENCY_WEIGHT + 1,
                                SYM1, SYMBOL1_WEIGHT,
                                SYM2, SYMBOL2_WEIGHT),
                IllegalArgumentException.class
            }
        };
    }


    @Test
    @Parameters(method="incorrectBasketComposition")
    public void shouldThrowExceptionWhenIncorrectBasketComposition(Map<String, Double> basketComposition,
                                                                   Class<? extends Throwable> expectedException)
                                                                   throws Exception {
        // given
        thrown.expect(expectedException);

        // when
        new InstrumentsToBasketConverter(BASKET_CURRENCY, REF_CURRENCY, basketComposition);

        // then throws an expected exception
    }


    private List<TestBar> barArray(String symbol, Integer[] dayOffsets, Double[] prices) {
        List<TestBar> bars = new ArrayList<>();
        for (int i = 0; i < dayOffsets.length; i++) {
            if (dayOffsets[i] == null) bars.add(null);
            else bars.add(new TestBar(symbol, dayOffsets[i], prices[i], BASKET_CURRENCY));
        }
        return bars;
    }


    private Stream<TestBar> barStreamWithDayOffsets(String symbol, Integer[] dayOffsets, Double[] prices) {
        List<TestBar> bars = new ArrayList<>();
        for (int i = 0; i < dayOffsets.length; i++) {
            if (dayOffsets[i] == null) bars.add(null);
            else bars.add(new TestBar(symbol, dayOffsets[i], prices[i], REF_CURRENCY));
        }
        return bars.stream();
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


    private static class TestInstrument extends Instrument {

        private ITimeSeriesFeed<Bar> testBarFeed;

        TestInstrument(String symbol, String currencyCode, ITimeSeriesFeed<Bar> testBarFeed) {
            super(symbol, currencyCode);
            this.testBarFeed = testBarFeed;
        }

        @Override
        public ITimeSeriesFeed<Bar> getBarDailyFeed() {
            return testBarFeed;
        }
    }
}