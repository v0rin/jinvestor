package org.jinvestor.datasource;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
*
* @author https://www.codeproject.com/Tips/372152/Mapping-JDBC-ResultSet-to-Object-using-Annotations
*/
public class PojoMapper<T> {

    public static class ResultSetMapperException extends Exception {
        private static final long serialVersionUID = 7298043674519428186L;

		ResultSetMapperException(String msg) {
            super(msg);
        }

        ResultSetMapperException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> mapToList(ResultSet rs, Class outputClass) throws ResultSetMapperException {
        List<T> outputList = new ArrayList<>();
        if (rs == null) {
            return outputList;
        }
        if (!outputClass.isAnnotationPresent(Entity.class)) {
            throw new ResultSetMapperException("Class missing Entity annotation: " + outputClass.getSimpleName());
        }
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            Field[] fields = outputClass.getDeclaredFields();
            while (rs.next()) {
                T bean = (T) outputClass.newInstance();
                // ResultSet indexes are 1-based.
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String rsColumnName = rsmd.getColumnName(i);
                    Object rsColumnValue = rs.getObject(i);
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Column.class)) {
                            Column column = field.getAnnotation(Column.class);
                            String beanColumnName = field.getName();
                            if (!StringUtils.isBlank(column.name())) {
                                beanColumnName = column.name();
                            }
                            if (beanColumnName.equals(rsColumnName)) {
                                BeanUtils.setProperty(bean, field.getName(), rsColumnValue);
                                break;
                            }
                        }
                    }
                }
                outputList.add(bean);
            }
        }
        catch (Exception e) {
            throw new ResultSetMapperException(e.getMessage(), e);
        }
        return outputList;
    }


    public T mapToSingle(ResultSet rs, Class<?> outputClass) throws ResultSetMapperException {
        List<T> outputList = mapToList(rs, outputClass);
        if (outputList.size() > 1) {
            throw new ResultSetMapperException("Expected single row, but got " + outputList.size());
        }
        else if (outputList.isEmpty()) {
            return null;
        }
        return outputList.get(0);
    }
}