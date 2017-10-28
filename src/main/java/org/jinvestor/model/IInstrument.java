package org.jinvestor.model;

import java.util.List;

import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.jinvestor.timeseriesfeed.TimeSeriesFreq;

/**
 *
 * @author Adam
 */
public interface IInstrument {

    String getSymbol();

    List<String> getAliases();

    String getCurrencyCode();

    String getDescription();

    ITimeSeriesFeed<Bar> getBarDailyFeed();

    ITimeSeriesFeed<Bar> getBarFeed(TimeSeriesFreq frequency);

}
