package org.jinvestor.datasource.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.jinvestor.datasource.IWriter;
import org.jinvestor.exception.AppRuntimeException;

/**
 *
 * @author Adam
 */
public class SimpleFileWriter implements IWriter<String> {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private String filePath;

	public SimpleFileWriter(String filePath, boolean append) throws IOException {
		this.filePath = filePath;
		if (!new File(filePath).createNewFile() && !append) {
			throw new IOException("File [" + filePath + "] already exists");
		}
	}

	@Override
	public void close() throws Exception {
		// method intentionally empty
	}

	@Override
	public void write(Stream<String> incomingStream) throws IOException {
		incomingStream.forEach(line -> {
			try {
				line += System.getProperty("line.separator");
				Files.write(Paths.get(filePath), line.getBytes(CHARSET), StandardOpenOption.APPEND);
			} catch (IOException e) {
				throw new AppRuntimeException(e);
			}
		});
	}
}
