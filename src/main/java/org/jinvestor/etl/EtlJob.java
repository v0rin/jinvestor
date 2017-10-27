package org.jinvestor.etl;

import java.io.IOException;
import java.util.Objects;

import org.jinvestor.datasource.IReader;
import org.jinvestor.datasource.IWriter;
import org.jinvestor.datasource.converter.IConverter;

/**
 * A typical ETL (Extract-Transform-Load) Job
 *
 * @author Adam
 */
public class EtlJob<T, R> implements IEtlJob {

    private IReader<T> reader;
    private IConverter<T, R> converter;
    private IWriter<R> writer;

    public EtlJob(IReader<T> reader, IConverter<T, R> converter, IWriter<R> writer) {
        this.reader = reader;
        this.converter = converter;
        this.writer = writer;
    }

    @Override
    public void execute() throws IOException {
        try(IReader<T> tmpReaderRef = reader;
            IWriter<R> tempWriterRef = writer) {

            writer.write(reader.stream().map(converter).filter(Objects::nonNull));
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }
}
