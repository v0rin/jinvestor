package org.jinvestor.datasource.db;

import java.io.IOException;
import java.util.stream.Stream;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.datasource.converter.BarToDbRowConverter;
import org.jinvestor.model.Bar;

/**
 *
 * @author Adam
 */
public class BarDbWriter implements IWriter<Bar> {

    IWriter<Object[]> rawDbWriter;
    IConverter<Bar, Object[]> converter;

    public BarDbWriter(String dbConnectionString) {
        rawDbWriter = new FastRawDbWriter(dbConnectionString, Bar.class);
    }

    @Override
    public void write(Stream<Bar> incomingStream) throws IOException {
        rawDbWriter.write(incomingStream.map(new BarToDbRowConverter()));
    }

    @Override
    public void close() throws Exception {
        rawDbWriter.close();
    }
}
