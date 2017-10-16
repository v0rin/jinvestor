package org.jinvestor.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Adam
 */
public class EntityUtil {

	public static String getTableName(Class<?> clazz) {
		validateAnnotations(clazz);
		return getTableAnnotationName(clazz);
	}


	public static String[] getColumns(Class<?> clazz) {
		validateAnnotations(clazz);
        Field[] fields = clazz.getDeclaredFields();
        List<String> columns = new ArrayList<>();
        for (Field field : fields) {
        	if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        		continue;
        	}
        	columns.add(getColumnAnnotationName(field));
        }
        return columns.toArray(new String[columns.size()]);
	}


//	static String getCreateTableSql() {
//	    return SQLiteQueryBuilder
//		        .create()
//		        .table(getTableName())
//		        .column(new com.alexfu.sqlitequerybuilder.api.Column(SYMBOL_COL_NAME, ColumnType.TEXT))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(DATE_TIME_COL_NAME, ColumnType.TEXT))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(OPEN_COL_NAME, ColumnType.REAL))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(HIGH_COL_NAME, ColumnType.REAL))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(LOW_COL_NAME, ColumnType.REAL))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(CLOSE_COL_NAME, ColumnType.REAL))
//				.column(new com.alexfu.sqlitequerybuilder.api.Column(VOLUME_COL_NAME, ColumnType.INTEGER))
//				.toString();
//	}


	static void validateAnnotations(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Entity.class)) {
			throw new InvalidAnnotationException("Class is not annotation as Entity");
		}
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new InvalidAnnotationException("Class is not annotation as Table");
		}
		if (StringUtils.isBlank(clazz.getAnnotation(Table.class).name())) {
			throw new InvalidAnnotationException("Table name not defined");
		}
	}


	static String getTableAnnotationName(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new InvalidAnnotationException("Class is not annotated as Table");
		}

		String name = clazz.getAnnotation(Table.class).name();
		if (StringUtils.isBlank(name)) {
			throw new InvalidAnnotationException("Annotation name empty or null for field");
		}
		return name;
	}


	static String getColumnAnnotationName(Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new InvalidAnnotationException("Field [" + field.getName() + "] is not annotated as Column");
		}

		String name = field.getAnnotation(Column.class).name();
		if (StringUtils.isBlank(name)) {
			throw new InvalidAnnotationException("Annotation name empty or null for field[" + field.getName() + "]");
		}
		return name;
	}
}
