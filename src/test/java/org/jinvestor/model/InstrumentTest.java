package org.jinvestor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.junit.Test;

public class InstrumentTest {

    @Test
    public void shouldReturnTheSameInstanceOfBarFeed() {
        // given
        Instrument instrument = new Instrument(SPY, USD);
        ITimeSeriesFeed<Bar> expectedBarFeedInstance = instrument.getBarDailyFeed();

        // when
        ITimeSeriesFeed<Bar> actualBarFeedInstanceFeed = instrument.getBarDailyFeed();

        // then
        assertThat(actualBarFeedInstanceFeed, is(expectedBarFeedInstance));
    }
}
