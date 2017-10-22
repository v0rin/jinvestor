package org.jinvestor.configuration;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

import org.jinvestor.ConfKeys;
import org.jinvestor.exception.AppRuntimeException;

/**
*
* @author Adam
*/
public class StaticJavaConfiguration implements IConfiguration {

    protected static final String BAR_DAILY_DB_CONNECTION_STRING = "jdbc:sqlite:datasource/sqlite/bar.daily.sqlite";

    private Map<ConfKeys, String> values;

    public StaticJavaConfiguration() {
        values = new EnumMap<>(ConfKeys.class);
        initializeValues();
    }

    private void initializeValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    ConfKeys key = ConfKeys.valueOf(field.getName());
                    values.put(key, (String)field.get(null));
                }
                catch (IllegalArgumentException e) {
                    throw new AppRuntimeException("It seems the field " + field.getName() +
                                                  " is not defined in ConfKeys", e);
                }
                catch (IllegalAccessException e) {
                    throw new AppRuntimeException("Could not get a value of the field " + field.getName(), e);
                }
            }
        }
    }

    @Override
    public <T extends Enum<T> & IConfigurationKey> String getString(T key) {
        return values.get(key);
    }

    @Override
    public <T extends Enum<T> & IConfigurationKey> Integer getInt(T key) {
        return Integer.parseInt(values.get(key));
    }

}
