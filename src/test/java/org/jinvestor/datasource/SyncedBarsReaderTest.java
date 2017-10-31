package org.jinvestor.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.configuration.StaticJavaConfiguration;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;
import org.jinvestor.time.TimestampUtil;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class SyncedBarsReaderTest {

    private static final Instant FROM = Instant.parse("2000-01-01T23:59:59.999Z");
    private static final Instant TO = Instant.parse("2000-01-06T23:59:59.999Z");

    private static final String SYMBOL = EUR;
    private static final String REF_CURRENCY = USD;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ITimeSeriesFeed<Bar> testBarFeed;

    private List<IInstrument> instruments;


    @Before
    public void setUp() throws IOException {
        Configuration.initialize(new StaticJavaConfiguration<>(ConfKeys.class));

        instruments = new ArrayList<>();
        instruments.add(new TestInstrument(SYMBOL, REF_CURRENCY, testBarFeed));
    }

    @SuppressWarnings("checkstyle:magicnumber")
    protected Object[] inputBarStream() {
        return new Object[] {
            new Object[]{barStreamWithDayOffsets(0, 1, 2, 3, 4, 5), barArray(0, 1, 2, 3, 4, 5)},
            new Object[]{barStreamWithDayOffsets(0, 1, 2, 3), barArray(0, 1, 2, 3, null, null)},
            new Object[]{barStreamWithDayOffsets(3, 4, 5), barArray(null, null, null, 3, 4, 5)},
            new Object[]{barStreamWithDayOffsets(0, 2, 4), barArray(0, null, 2, null, 4, null)},
            new Object[]{barStreamWithDayOffsets(1, 3, 5), barArray(null, 1, null, 3, null, 5)},
            new Object[]{barStreamWithDayOffsets(0, 5), barArray(0, null, null, null, null, 5)},
            new Object[]{barStreamWithDayOffsets(2, 5), barArray(null, null, 2, null, null, 5)},
            new Object[]{barStreamWithDayOffsets(0, 3), barArray(0, null, null, 3, null, null)},
            new Object[]{barStreamWithDayOffsets(0), barArray(0, null, null, null, null, null)},
            new Object[]{barStreamWithDayOffsets(5), barArray(null, null, null, null, null, 5)},
            new Object[]{barStreamWithDayOffsets(2), barArray(null, null, 2, null, null, null)},
            new Object[]{barStreamWithDayOffsets(2, 3), barArray(null, null, 2, 3, null, null)}
        };
    }

    @Test
    @Parameters(method="inputBarStream")
    public void shouldCreateCorrectBarList(Stream<Bar> inputBarStream, List<List<Bar>> expected) throws Exception {
        // given
        BDDMockito.given(testBarFeed.stream(any(), any())).willReturn(inputBarStream);

        // when
        List<List<Bar>> actual = null;
        try (IReader<List<Bar>> syncedBarsReader = new SyncedBarsReader(instruments, FROM, TO)) {
            actual = syncedBarsReader.stream().collect(Collectors.toList());
        }

        // then
        assertThat(actual, is(expected));
    }

    private List<List<TestBar>> barArray(Integer... dayOffsets) {
        return Arrays.stream(dayOffsets).map(dayOffset -> {
            if (dayOffset == null) return new ArrayList<TestBar>();
            else return Arrays.asList(new TestBar(dayOffset));
        }).collect(Collectors.toList());
    }

    private Stream<Bar> barStreamWithDayOffsets(int... dayOffsets) {
        return Arrays.stream(dayOffsets).boxed().map(TestBar::new);
    }

    private static class TestBar extends Bar {
        private int dayOffset;

        TestBar(int dayOffset) {
            super(SYMBOL, getAdjustedTimestamp(dayOffset), null, null, null, null, null, REF_CURRENCY);
            this.dayOffset = dayOffset;
        }

        private static Timestamp getAdjustedTimestamp(int dayOffset) {
            Instant fromShifted = FROM.plus(dayOffset, ChronoUnit.DAYS);
            Timestamp timestamp = TimestampUtil.fromInstantInUTC(fromShifted);
            return timestamp;
        }

        @Override
        public String toString() {
            return Integer.toString(dayOffset);
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
