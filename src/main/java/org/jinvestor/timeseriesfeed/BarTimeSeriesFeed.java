package org.jinvestor.timeseriesfeed;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.jinvestor.ConfKeys;
import org.jinvestor.configuration.Configuration;
import org.jinvestor.datasource.IReader;
import org.jinvestor.datasource.converter.ResultSetToBarConverter;
import org.jinvestor.datasource.db.DbReader;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Instrument;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.time.DateTimeFormatterFactory;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 *
 * @author Adam
 */
public class BarTimeSeriesFeed implements ITimeSeriesFeed<Bar>, AutoCloseable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatterFactory.standardTimestamp();

    private TimeSeriesFreq frequency;
    private Instrument instrument;
    private Currency currency;

    private IReader<Bar> dbReader;


    protected BarTimeSeriesFeed(TimeSeriesFreq frequency, Instrument instrument, Currency currency) {
        this.frequency = frequency;
        this.instrument = instrument;
        this.currency = currency;

    }


    @Override
    public Stream<Bar> get(Instant from, Instant to) throws IOException {
        String selectQuery = SQLiteQueryBuilder
                .select("*")
                .from(EntityMetaDataFactory.get(Bar.class).getTableName())
                .where("symbol = '" + instrument.getId() + "'")
                .and("currency = '" + currency.getCode() + "'")
                .and("timestamp >= '" + getUtcTimeAsString(from) + "'")
                .and("timestamp <= '" + getUtcTimeAsString(to) + "'")
                .build();

        dbReader = new DbReader<>(getConnectionString(frequency),
                                   selectQuery,
                                   new ResultSetToBarConverter());

        return dbReader.stream();
    }


    private String getConnectionString(TimeSeriesFreq barFrequency) {
        if (barFrequency == TimeSeriesFreq.DAILY) {
            return Configuration.INSTANCE.getString(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING);
        }
        else {
            throw new UnsupportedOperationException("TimeSeriesFreq." + barFrequency.name() + "is not supported");
        }
    }


    private String getUtcTimeAsString(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }


    @Override
    public void close() throws Exception {
        dbReader.close();
    }
}
