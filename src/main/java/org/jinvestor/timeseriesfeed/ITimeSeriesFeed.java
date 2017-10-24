package org.jinvestor.timeseriesfeed;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

/**
 *
 * @author Adam
 */
public interface ITimeSeriesFeed<T> {

    Stream<T> get(Instant from, Instant to) throws IOException;

}
