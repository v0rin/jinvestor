package org.jinvestor.datasource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.jinvestor.datasource.file.CsvReader;
import org.jinvestor.datasource.file.SimpleFileWriter;
import org.junit.After;
import org.junit.Test;

public class EtlJobTest {

    private static final String ROOT_PATH = "src/test/resources/org/jinvestor/datasource/etl-job-test/";
    private static final String INPUT_FILE_PATH = ROOT_PATH + "input.csv";
    private static final String EXPECTED_OUTPUT_FILE_PATH = ROOT_PATH + "expected-output.csv";
    private static final String OUTPUT_FILE_PATH = "output.csv";
    private static final char SEPARATOR = ',';

    private static final String APPENDED_TEST_STRING = ",test";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @After
    public void tearDown() {
        new File(OUTPUT_FILE_PATH).delete();
    }

    @Test
    public void testSimpleConversion() throws Exception {
        // given
        IReader<String[]> reader = spy(new CsvReader(INPUT_FILE_PATH, SEPARATOR));
        IWriter<String> writer = spy(new SimpleFileWriter(OUTPUT_FILE_PATH, false));
        IEtlJob etlJob = new EtlJob<String[], String>(reader, new TestConverter(), writer);

        // when
        etlJob.execute();

        // then
        verify(reader).close();
        verify(writer).close();
        assertEquals(FileUtils.readLines(new File(EXPECTED_OUTPUT_FILE_PATH), CHARSET),
                     FileUtils.readLines(new File(OUTPUT_FILE_PATH), CHARSET));
    }

    private static class TestConverter implements IConverter<String[], String> {

        @Override
        public String apply(String[] strings) {
            StringBuilder sb = new StringBuilder();
            Arrays.asList(strings).forEach(sb::append);
            return sb.toString() + APPENDED_TEST_STRING;
        }
    }
}
