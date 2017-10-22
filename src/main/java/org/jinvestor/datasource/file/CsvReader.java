package org.jinvestor.datasource.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.jinvestor.datasource.IReader;
import org.simpleflatmapper.csv.CsvParser;

/**
 *
 * @author Adam
 */
public class CsvReader implements IReader<String[]> {

    private String csvPath;
    private char separator;

    private java.io.Reader csvFileReader;
    private Stream<String[]> outgoingStream;

    public CsvReader(String csvPath, char separator) {
        this.csvPath = csvPath;
        this.separator = separator;
    }

    @Override
    public Stream<String[]> stream() throws IOException {
        csvFileReader = new InputStreamReader(new FileInputStream(csvPath), StandardCharsets.UTF_8);
        outgoingStream = CsvParser.separator(separator).stream(csvFileReader);
        return outgoingStream;
    }

    @Override
    public void close() throws Exception {
        if (outgoingStream != null) {
            outgoingStream.close();
        }
        if (csvFileReader != null) {
            csvFileReader.close();
        }
    }
}
