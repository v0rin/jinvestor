package org.jinvestor.configuration;

import org.jinvestor.ConfKeys;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
*
* @author Adam
*/
public class Configuration {

    private static IConfiguration<ConfKeys> conf;

    private Configuration() {
        // exists only to
    }

    @SuppressFBWarnings(value="ME_ENUM_FIELD_SETTER")
    public static synchronized void initialize(IConfiguration<ConfKeys> configuration) {
        conf = configuration;
    }

    public static String getString(ConfKeys key) {
        checkIfInitialized();
        return conf.getString(key);
    }

    public static Integer getInt(ConfKeys key) {
        checkIfInitialized();
        return conf.getInt(key);
    }

    private static void checkIfInitialized() {
        checkNotNull(conf, "Configuration not set. Call ConfigurationFactory.INSTANCE.set() method first");
    }
}
