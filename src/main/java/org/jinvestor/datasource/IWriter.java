package org.jinvestor.datasource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author Adam
 */
public interface IWriter<T> extends AutoCloseable {
    void write(Stream<T> incomingStream) throws IOException;
}
