package org.jinvestor.timeseriesfeed;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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


    public BarFeedInBasketCurrency(TimeSeriesFreq frequency,
                                 IInstrument instrument,
                                 String basketCurrencyName,
                                 Map<String, Double> basketComposition) {
        this.basketComposition = basketComposition;
        this.refCurrency = instrument.getCurrencyCode();
        this.basketConverter = new InstrumentsToBasketConverter(basketCurrencyName, refCurrency, basketComposition);
        this.barFeed = new BarFeed(frequency, instrument);

    }


    @Override
    public Stream<Bar> stream(Instant from, Instant to) throws IOException {
        List<IInstrument> instruments = getInstruments();

        syncedBarsReader = new SyncedBarsReader(instruments, from, to);

        Stream<Bar> basketCurrencyStream = syncedBarsReader.stream().map(basketConverter);

        IConverter<Bar, Bar> instrumentCurrencyConverter = new InstrumentCurrencyConverter(basketCurrencyStream);

        return barFeed.stream(from, to).map(instrumentCurrencyConverter);
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
    }
}
