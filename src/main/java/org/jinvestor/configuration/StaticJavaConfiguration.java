package org.jinvestor.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jinvestor.exception.AppRuntimeException;

/**
*
* @author Adam
*/
public class StaticJavaConfiguration<T extends Enum<T> & IConfigurationKey> implements IConfiguration<T> {

    protected static final String BAR_DAILY_DB_CONNECTION_STRING = "jdbc:sqlite:datasource/sqlite/bar_daily.sqlite";


    // ##################################################################################################### //

    private Map<T, String> values;
    private Class<T> type;

    public StaticJavaConfiguration(Class<T> type) {
        this.type = type;
        this.values = new HashMap<>();
        initializeValues();
    }

    private void initializeValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && !field.getName().equals("$jacocoData")) {
                try {
                    T key = T.valueOf(type, field.getName());
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

    public void setValue(T key, String value) {
        values.put(key, value);
    }

    public void setValues(Map<T, String> newValues) {
        values.putAll(newValues);
    }

    @Override
    public String getString(T key) {
        return values.get(key);
    }

    @Override
    public Integer getInt(T key) {
        return Integer.parseInt(values.get(key));
    }

}
