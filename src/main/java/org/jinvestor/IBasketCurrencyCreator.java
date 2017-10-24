package org.jinvestor;

import java.time.Instant;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;

/**
 * @author Adam
 */
public interface IBasketCurrencyCreator {

    Stream<Bar> create(Instant from, Instant to);

}
