package org.vorin.trading.configuration;

/**
*
* @author vorin
*/
public interface IConfiguration {

	<T extends Enum<T> & IConfigurationKey> String getString(T key);

	<T extends Enum<T> & IConfigurationKey> Integer getInt(T key);

}
