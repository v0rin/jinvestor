package org.jinvestor.model;

/**
 * {@code BC1} Basket currency composed of main currencies in a given ratio
 *
 * @author Adam
 */
public class Currency {

    private String code;

    public enum Code {
        USD,
        EUR,
        CNY,
        JPY,
        GBP,
        BC1
        ;
    }

    public Currency(String code) {
        this.code = code;
    }

    public Currency(Currency.Code code) {
        this(code.name());
    }

    public static Currency of(String code) {
        return new Currency(code);
    }

    public static Currency of(Currency.Code code) {
        return of(code.name());
    }

    public String getCode() {
        return code;
    }
}