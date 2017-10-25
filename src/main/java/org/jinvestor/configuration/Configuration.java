package org.jinvestor.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
*
* @author Adam
*/
public enum Configuration implements IConfiguration {

    INSTANCE;

    private IConfiguration conf;

    public void initialize(IConfiguration configuration) {
        this.conf = configuration;
    }

    @Override
    public <T extends Enum<T> & IConfigurationKey> String getString(T key) {
        checkIfInitialized();
        return conf.getString(key);
    }

    @Override
    public <T extends Enum<T> & IConfigurationKey> Integer getInt(T key) {
        checkIfInitialized();
        return conf.getInt(key);
    }

    private void checkIfInitialized() {
        checkNotNull(conf, "Configuration not set. Call ConfigurationFactory.INSTANCE.set() method first");
    }
}
