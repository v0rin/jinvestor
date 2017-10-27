package org.jinvestor.model;

/**
 *
 * @author Adam
 */
public class Instruments {

    public static final String SPY = "SPY";
    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final String JPY = "JPY";
    public static final String CNY = "CNY";
    public static final String GBP = "GBP";
    public static final String PLN = "PLN";
    public static final String BC1 = "BC1";



    private Instruments() {
        throw new InstantiationError("This class should not be instantiated!");
    }
}
