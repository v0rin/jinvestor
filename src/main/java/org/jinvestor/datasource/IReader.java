package org.jinvestor.datasource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author Adam
 */
public interface IReader<T> extends AutoCloseable {
	Stream<T> stream() throws IOException;
}
