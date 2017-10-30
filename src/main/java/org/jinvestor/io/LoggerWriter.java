package org.jinvestor.io;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.model.Bar;

/**
 *
 * @author Adam
 */
public class LoggerWriter implements IWriter<Bar> {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void write(Stream<Bar> incomingStream) throws IOException {
        incomingStream.forEach(LOG::info);
    }

    @Override
    public void close() throws Exception {
        // intentionally left empty
    }
}