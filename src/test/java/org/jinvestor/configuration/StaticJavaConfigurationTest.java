package org.jinvestor.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jinvestor.ConfKeys;
import org.junit.Test;

public class StaticJavaConfigurationTest {

    @Test
    public void shouldInitializeCorrectly() {
        // given
        IConfiguration conf = new TestStaticJavaConfiguration();
        String expected = TestStaticJavaConfiguration.BAR_DAILY_DB_CONNECTION_STRING;

        // when
        String actual = conf.getString(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING);

        // then
        assertThat(actual, is(expected));
    }


    private static class TestStaticJavaConfiguration extends StaticJavaConfiguration {
        protected static final String BAR_DAILY_DB_CONNECTION_STRING = "test-value";
    }
}