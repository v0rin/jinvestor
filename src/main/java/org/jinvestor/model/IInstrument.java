package org.jinvestor.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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

    ITimeSeriesFeed<Bar> getBarDailyFeedInBasketCurrency(String basketCurrencyName,
                                                         Map<String, Double> basketComposition);

    ITimeSeriesFeed<Bar> getBarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                                    String basketCurrencyName,
                                                    Map<String, Double> basketComposition);

    ITimeSeriesFeed<Bar> getBarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                                    String basketCurrencyName,
                                                    Map<String, Double> basketComposition,
                                                    String proxyCurrency);

    ITimeSeriesFeed<Bar> getBarDailyFeedInBasketCurrencyByProxy(String basketCurrencyName,
                                                                Map<String, Double> basketComposition,
                                                                String proxyCurrency);

    ITimeSeriesFeed<Bar> getBarFeedInBasketCurrencyByProxy(TimeSeriesFreq frequency,
                                                           String basketCurrencyName,
                                                           Map<String, Double> basketComposition,
                                                           String proxyCurrency);

    String getDailyBarsInJson(Instant from, Instant to);

    String getDailyBarsInJsonReduced(Instant from, Instant to);

    String getDailyBarsInBasketCurrencyInJsonReduced(Instant from, Instant to, String basketCurrencyName,
            Map<String, Double> basketComposition);

    String getDailyBarsInDefaultBasketCurrencyInJsonReduced(Instant from, Instant to);
}
