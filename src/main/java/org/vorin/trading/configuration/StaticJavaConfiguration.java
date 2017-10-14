package org.vorin.trading.configuration;

import java.util.HashMap;
import java.util.Map;

/**
*
* @author vorin
*/
public class StaticJavaConfiguration implements IConfiguration {

	private Map<String, String> values;

	public StaticJavaConfiguration() {
		values = new HashMap<>();

		initializeValues();
	}

	private void initializeValues() {
//		values.put()
	}

	@Override
	public <T extends Enum<T> & IConfigurationKey> String getString(T key) {
		return values.get(key.name());
	}

	@Override
	public <T extends Enum<T> & IConfigurationKey> Integer getInt(T key) {
		return Integer.parseInt(values.get(key.name()));
	}

}
