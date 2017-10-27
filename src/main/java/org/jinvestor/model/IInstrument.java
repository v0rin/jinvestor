package org.jinvestor.model;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.jinvestor.timeseriesfeed.TimeSeriesFreq;

/**
 *
 * @author Adam
 */
public interface IInstrument {

    Stream<Bar> streamDaily(Instant from, Instant to) throws IOException;

    Stream<Bar> stream(Instant from, Instant to, TimeSeriesFreq frequency) throws IOException;

    String getSymbol();

    List<String> getAliases();

    String getCurrencyCode();

    String getDescription();

}
