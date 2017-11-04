package org.jinvestor.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jinvestor.datasource.converter.BarsToJsonReducingConverter;
import org.jinvestor.datasource.converter.IConverter;
import org.jinvestor.datasource.converter.StandardBarsToJsonConverter;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.timeseriesfeed.BarFeed;
import org.jinvestor.timeseriesfeed.BarFeedInBasketCurrency;
import org.jinvestor.timeseriesfeed.ITimeSeriesFeed;
import org.jinvestor.timeseriesfeed.TimeSeriesFreq;

/**
 * TODO (AF) dependency injection would be nice here
 * @author Adam
 */
public class Instrument implements IInstrument {

    private String symbol;
    private List<String> aliases;
    private String currencyCode;
    private String description;

    private Map<TimeSeriesFreq, ITimeSeriesFeed<Bar>> barFeeds;
    private Map<TimeSeriesFreq, ITimeSeriesFeed<Bar>> basketCurrencyBarFeeds;

    private IConverter<Stream<Bar>, String> barsToJsonConverter = new StandardBarsToJsonConverter();
    private IConverter<Stream<Bar>, String> barsToJsonReducingConverter = new BarsToJsonReducingConverter();

    public Instrument(String symbol, String currencyCode) {
        this.symbol = symbol;
        this.aliases = new ArrayList<>();
        this.aliases.add(symbol);
        this.currencyCode = currencyCode;
        this.description = symbol;
        this.barFeeds = new EnumMap<>(TimeSeriesFreq.class);
        this.basketCurrencyBarFeeds = new EnumMap<>(TimeSeriesFreq.class);
    }

    @Override
    public String getDailyBarsInJson(Instant from, Instant to) {
        try (ITimeSeriesFeed<Bar> barFeed = new BarFeed(TimeSeriesFreq.DAILY, this)) {
            return barsToJsonConverter.apply(barFeed.stream(from, to));
        }
        catch (Exception e) {
            throw new AppRuntimeException(e);
        }
    }

    @Override
    public String getDailyBarsInJsonReduced(Instant from, Instant to) {
        try (ITimeSeriesFeed<Bar> barFeed = new BarFeed(TimeSeriesFreq.DAILY, this)) {
            return barsToJsonReducingConverter.apply(barFeed.stream(from, to));
        }
        catch (Exception e) {
            throw new AppRuntimeException(e);
        }
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarDailyFeed() {
        return getBarFeed(TimeSeriesFreq.DAILY);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarFeed(TimeSeriesFreq frequency) {
        return barFeeds.computeIfAbsent(frequency, freq -> new BarFeed(freq, this));
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarDailyFeedInBasketCurrency(String basketCurrencyName,
                                                              Map<String, Double> basketComposition) {
        return getBarFeedInBasketCurrency(TimeSeriesFreq.DAILY, basketCurrencyName, basketComposition);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                                           String basketCurrencyName,
                                                           Map<String, Double> basketComposition,
                                                           String proxyCurrency) {
        return getBarFeedInBasketCurrencyByProxy(TimeSeriesFreq.DAILY, basketCurrencyName, basketComposition, null);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                                         String basketCurrencyName,
                                                         Map<String, Double> basketComposition) {
        return getBarFeedInBasketCurrencyByProxy(frequency, basketCurrencyName, basketComposition, null);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarDailyFeedInBasketCurrencyByProxy(String basketCurrencyName,
                                                                       Map<String, Double> basketComposition,
                                                                       String proxyCurrency) {
        return getBarFeedInBasketCurrencyByProxy(TimeSeriesFreq.DAILY,
                                                 basketCurrencyName,
                                                 basketComposition,
                                                 proxyCurrency);
    }

    @Override
    public ITimeSeriesFeed<Bar> getBarFeedInBasketCurrencyByProxy(TimeSeriesFreq frequency,
                                                                  String basketCurrencyName,
                                                                  Map<String, Double> basketComposition,
                                                                  String proxyCurrency) {
        return basketCurrencyBarFeeds.compute(frequency,
            (freq, v) -> new BarFeedInBasketCurrency(freq, this, basketCurrencyName, basketComposition, proxyCurrency));
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Instrument)) {
            return false;
        }
        Instrument rhs = (Instrument) object;
        return new EqualsBuilder().append(this.symbol, rhs.symbol)
                                  .append(this.aliases, rhs.aliases)
                                  .append(this.currencyCode, rhs.currencyCode)
                                  .append(this.description, rhs.description)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(-2103055395, 1354241493)
                    .append(this.symbol)
                    .append(this.aliases)
                    .append(this.currencyCode)
                    .append(this.description)
                    .toHashCode();
    }

    @Override
    public String toString() {
        return "Instrument [symbol=" + symbol + ", aliases=" + aliases + ", currencyCode=" + currencyCode + "]";
    }
}
