package org.jinvestor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.jinvestor.model.Instruments.SPY;
import static org.jinvestor.model.Instruments.USD;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InstrumentTest {

    @Test
    public void instrumentsShouldBeEqual() {
        // given
        Instrument instrument1 = new Instrument(SPY, USD);
        Instrument instrument2 = new Instrument(SPY, USD);

        // when
        instrument1.getBarDailyFeed();
        instrument2.getBarDailyFeed();

        // then
        assertThat(instrument1, is(instrument2));
    }
}
