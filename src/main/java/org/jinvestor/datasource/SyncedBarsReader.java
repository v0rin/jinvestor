package org.jinvestor.datasource;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.time.TimestampStream;

import static com.google.common.base.Preconditions.checkState;

/**
 * Retrieves bars for multiple instruments and combines them into a stream of bar lists,
 * where bars places on continues timestamp stream
 * <br><br>
 *
 * @author Adam
 */
public class SyncedBarsReader implements IReader<List<Bar>> {

    private static final Logger LOG = LogManager.getLogger();

    private List<IInstrument> instruments;
    private Instant from;
    private Instant to;


    public SyncedBarsReader(List<IInstrument> instruments, Instant from, Instant to) {
        this.instruments = instruments;
        this.from = from;
        this.to = to;
    }


    @Override
    public Stream<List<Bar>> stream() throws IOException {
        List<Iterator<Bar>> iterators = getStreamIteratorsForInstruments();

        Function<Timestamp, List<Bar>> barMapper = getTimestampToBarsMapper(iterators);

        return TimestampStream.rangeClosed(from, to).map(barMapper);
    }


    private Function<Timestamp, List<Bar>> getTimestampToBarsMapper(List<Iterator<Bar>> iterators) {
        Map<Iterator<Bar>, Bar> lastRetrievedBars = new HashMap<>();
        return timestamp -> {
            List<Bar> bars = new ArrayList<>();
            iterators.forEach(barIter -> {
                Bar bar = lastRetrievedBars.get(barIter);
                while ((bar == null || bar.getTimestamp().before(timestamp)) && barIter.hasNext()) {
                    // bar needs to catch up to the timestamp
                    bar = barIter.next();
                    // TODO (AF) can be removed in the future, after the algorithm is fully tested
                    checkBarTimestampIsEod(bar);
                }
                if (bar == null) {
                    // no more bars for the instrument
                    return;
                }

                lastRetrievedBars.put(barIter, bar);
                if (bar.getTimestamp().equals(timestamp)) {
                    bars.add(bar);
                    lastRetrievedBars.put(barIter, bar);
                    return;
                }
            });
            return bars;
        };
    }


    private List<Iterator<Bar>> getStreamIteratorsForInstruments() {
        List<Iterator<Bar>> iterators = new ArrayList<>();
        instruments.stream().forEach(instrument -> {
            try {
                Iterator<Bar> barIterator = instrument.getBarDailyFeed().stream(from, to).iterator();
                iterators.add(barIterator);
            }
            catch (IOException e) {
                throw new AppRuntimeException(e);
            }
        });

        return iterators;
    }


    private void checkBarTimestampIsEod(Bar bar) {
        if (bar == null) return;

        final String expectedTime = "23:59:59.999";
        checkState(bar.getTimestamp().toString().contains(expectedTime),
                   "Bar timestamp should be End-Of-Day [{}]: " + bar.getTimestamp(), expectedTime);
    }


    @Override
    public void close() throws Exception {
        for(IInstrument instrument : instruments) {
            instrument.getBarDailyFeed().close();
        }
    }
}
