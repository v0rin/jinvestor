package org.jinvestor.model;

import java.util.ArrayList;
import java.util.List;

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
}
