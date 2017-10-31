package org.jinvestor.configuration;

/**
*
* @author Adam
*/
public interface IConfiguration<T extends Enum<T> & IConfigurationKey> {

    String getString(T key);

    Integer getInt(T key);

}
