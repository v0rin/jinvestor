package org.jinvestor.datasource;

import java.io.IOException;
import java.util.Objects;

/**
 * A typical ETL (Extract-Transform-Load) Job
 *
 * @author Adam
 */
public class EtlJob<T, R> implements IEtlJob {

	private IReader<T> reader;
	private IAdapter<T, R> adapter;
	private IWriter<R> writer;

	public EtlJob(IReader<T> reader, IAdapter<T, R> adapter, IWriter<R> writer) {
		this.reader = reader;
		this.adapter = adapter;
		this.writer = writer;
	}

	@Override
	public void execute() throws IOException {
		try(IReader<T> tmpReaderRef = reader;
			IWriter<R> tempWriterRef = writer) {

			writer.write(reader.stream().map(adapter).filter(Objects::nonNull));
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
}
