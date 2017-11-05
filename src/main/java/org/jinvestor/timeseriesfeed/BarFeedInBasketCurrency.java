package org.jinvestor.timeseriesfeed;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.datasource.IReader;
import org.jinvestor.datasource.SyncedBarsReader;
import org.jinvestor.datasource.converter.IConverter;
import org.jinvestor.datasource.converter.InstrumentCurrencyConverter;
import org.jinvestor.datasource.converter.InstrumentsToBasketConverter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.Instrument;

/**
 * Returns bar stream of a given instrument in a basket currency computed during runtime
 *
 * @author Adam
 */
public class BarFeedInBasketCurrency implements ITimeSeriesFeed<Bar> {

    private Map<String, Double> basketComposition;
    private String refCurrency;

    private IConverter<List<Bar>, Bar> basketConverter;
    private IReader<List<Bar>> syncedBarsReader;
    private ITimeSeriesFeed<Bar> barFeed;
    private ITimeSeriesFeed<Bar> proxyCurrencyBarFeed;

    public BarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                   IInstrument instrument,
                                   String basketCurrencyName,
                                   Map<String, Double> basketComposition,
                                   String proxyCurrency) {
        this.basketComposition = basketComposition;
        this.barFeed = new BarFeed(frequency, instrument);
        if (proxyCurrency == null) {
            this.refCurrency = instrument.getCurrencyCode();
        }
        else {
            this.refCurrency = proxyCurrency;
            IInstrument proxyInstrument = new Instrument(instrument.getCurrencyCode(), proxyCurrency);
            this.proxyCurrencyBarFeed = new BarFeed(frequency, proxyInstrument);
        }
        this.basketConverter = new InstrumentsToBasketConverter(basketCurrencyName, refCurrency, basketComposition);
    }


    public BarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                 IInstrument instrument,
                                 String basketCurrencyName,
                                 Map<String, Double> basketComposition) {
        this(frequency, instrument, basketCurrencyName, basketComposition, null);
    }


    @Override
    public Stream<Bar> stream(Instant from, Instant to) throws IOException {
        List<IInstrument> instruments = getInstruments();

        syncedBarsReader = new SyncedBarsReader(instruments, from, to);

        Stream<Bar> basketCurrencyStream = syncedBarsReader.stream().map(basketConverter);

        IConverter<Bar, Bar> instrumentCurrencyConverter = new InstrumentCurrencyConverter(basketCurrencyStream);

        if (proxyCurrencyBarFeed == null) {
            // spy/usd * usd/bc1 -> spy/bc1
            return barFeed.stream(from, to).map(instrumentCurrencyConverter).filter(Objects::nonNull);
        }
        else {
            // wig/pln * pln/usd -> wig/usd * usd/bc1 -> wig/bc1
            Stream<Bar> proxyCurrencyStream = proxyCurrencyBarFeed.stream(from, to);
            IConverter<Bar, Bar> proxyCurrencyConverter = new InstrumentCurrencyConverter(proxyCurrencyStream);
            return barFeed.stream(from, to).map(proxyCurrencyConverter)
                                           .map(instrumentCurrencyConverter)
                                           .filter(Objects::nonNull);
        }
    }


    private List<IInstrument> getInstruments() {
        return basketComposition.keySet().stream()
                .filter(symbol -> !refCurrency.equals(symbol))
                .map(symbol -> new Instrument(symbol, refCurrency))
                .collect(Collectors.toList());
    }


    @Override
    public void close() throws Exception {
        syncedBarsReader.close();
        barFeed.close();
        if (proxyCurrencyBarFeed != null) proxyCurrencyBarFeed.close();
    }
}
