package org.jinvestor.timeseriesfeed;

import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Instrument;

/**
 *
 * @author Adam
 */
public class TimeSeriesFeedFactory {

    private TimeSeriesFeedFactory() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public static ITimeSeriesFeed<Bar> getDailyBarFeed(Instrument instrument, Currency currency) {
        return new BarTimeSeriesFeed(TimeSeriesFreq.DAILY, instrument, currency);
    }

}
