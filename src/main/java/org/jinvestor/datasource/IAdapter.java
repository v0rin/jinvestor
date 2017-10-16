package org.jinvestor.datasource;

import java.util.function.Function;

/**
 *
 * @author Adam
 */
@FunctionalInterface
public interface IAdapter<T, R> extends Function<T, R> {
}
