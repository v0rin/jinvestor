package org.jinvestor.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * {@code BC1} Basket currency composed of main currencies in a given ratio
 *
 * @author Adam
 */
public class Currency {

    private String code;
    private String description;

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
        this.description = code;
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

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(236488691, 316571933)
                    .append(this.code)
                    .append(this.description)
                    .toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency rhs = (Currency) object;
        return new EqualsBuilder()
                    .append(this.code, rhs.code)
                    .append(this.description, rhs.description)
                    .isEquals();
    }

    @Override
    public String toString() {
        return "Currency [code=" + code + "]";
    }
}