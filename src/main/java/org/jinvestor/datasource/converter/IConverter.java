package org.jinvestor.datasource.converter;

import java.util.function.Function;

/**
 *
 * @author Adam
 */
@FunctionalInterface
public interface IConverter<T, R> extends Function<T, R> {
}
