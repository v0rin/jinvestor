package org.jinvestor.datasource.converter;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author Adam
 */
@SuppressFBWarnings
class DataPointWithTimestamp {

    @SuppressWarnings("unused")
    public String timestamp;

    @SuppressWarnings("unused")
    public Double value;

    DataPointWithTimestamp(Timestamp date, Double value) {
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date.toLocalDateTime());
        this.value = value;
    }
}
