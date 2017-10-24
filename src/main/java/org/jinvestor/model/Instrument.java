package org.jinvestor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Adam
 */
public class Instrument {

    public enum Code {
        /**
         * Standard & Poor's 500
         */
        SPY;
    }

    private String id;
    private List<String> codes;
    private String description;

    public Instrument(Code code) {
        this(code.name());
    }

    public Instrument(String code) {
        id = code;
        codes = new ArrayList<>();
        codes.add(code);
        description = code;
    }

    public static Instrument of(String code) {
        return new Instrument(code);
    }

    public static Instrument of(Instrument.Code code) {
        return of(code.name());
    }

    public String getId() {
        return id;
    }

    public List<String> getCodes() {
        return codes;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Instrument)) {
            return false;
        }
        Instrument rhs = (Instrument) object;
        return new EqualsBuilder().append(this.codes, rhs.codes)
                                  .append(this.description, rhs.description)
                                  .append(this.id, rhs.id)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(-2103055395, 1354241493)
                    .append(this.codes)
                    .append(this.description)
                    .append(this.id)
                    .toHashCode();
    }

    @Override
    public String toString() {
        return "Instrument [id=" + id + ", codes=" + codes + "]";
    }
}
