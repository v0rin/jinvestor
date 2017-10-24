package org.jinvestor.model;

/**
 * TODO (AF) in the future instruments should be stored and read from db, the table would have multiple aliases,
 * but the Bar table would actually have unique ids, maybe e.g. RIC or other the most popular symbol
 * and suplemented with some hash for uniqness
 *
 * @author Adam
 */
public class InstrumentFactory {

    private InstrumentFactory() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public Instrument get(String code) {
        return new Instrument(code);
    }

    public Instrument get(Instrument.Code code) {
        return new Instrument(code);
    }
}
