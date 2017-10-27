package org.jinvestor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class InstrumentsTest {

    @Test
    public void checkAllStaticFieldsHaveValuesEqualNames() throws IllegalArgumentException, IllegalAccessException {
        // given
        Field[] fields = Instruments.class.getDeclaredFields();

        // when and then
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                assertThat((String)field.get(null), is(field.getName()));
            }
        }
    }
}
