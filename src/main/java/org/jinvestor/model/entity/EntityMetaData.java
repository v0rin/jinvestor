package org.jinvestor.model.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.alexfu.sqlitequerybuilder.builder.CreateTableSegmentBuilder;

/**
 *
 * @author Adam
 */
public class EntityMetaData<T> implements IEntityMetaData {

	private Class<T> clazz;

	EntityMetaData(Class<T> clazz) {
		this.clazz = clazz;
	}


	@Override
	public String getTableName() {
		validateAnnotations();
		return getTableAnnotationName();
	}


	@Override
	public String[] getColumns() {
		validateAnnotations();
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


	@Override
	public String getCreateTableSql() {
		CreateTableSegmentBuilder builder = SQLiteQueryBuilder
		        .create()
		        .table(getTableName());

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
        	if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        		continue;
        	}
        	builder.column(
        			new com.alexfu.sqlitequerybuilder.api.Column(
        					getColumnAnnotationName(field),
        					ColumnType.valueOf(getColumnAnnotationDefinition(field))));

        }

        return builder.toString();
	}


	private void validateAnnotations() {
		if (!clazz.isAnnotationPresent(Entity.class)) {
			throw new InvalidAnnotationException("Class[" + clazz.getName() + "] is not annotation as Entity");
		}
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new InvalidAnnotationException("Class[" + clazz.getName() + "] is not annotation as Table");
		}
		if (StringUtils.isBlank(clazz.getAnnotation(Table.class).name())) {
			throw new InvalidAnnotationException("Table name not defined");
		}
	}


	private String getTableAnnotationName() {
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new InvalidAnnotationException("Class[" + clazz.getName() + "] is not annotated as Table");
		}

		String name = clazz.getAnnotation(Table.class).name();
		if (StringUtils.isBlank(name)) {
			throw new InvalidAnnotationException("Annotation name empty or null for field");
		}
		return name;
	}


	private String getColumnAnnotationName(Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new InvalidAnnotationException("Field [" + field.getName() + "] is not annotated as Column");
		}

		String name = field.getAnnotation(Column.class).name();
		if (StringUtils.isBlank(name)) {
			throw new InvalidAnnotationException("Annotation name empty or null for field[" + field.getName() + "]");
		}
		return name;
	}


	private String getColumnAnnotationDefinition(Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new InvalidAnnotationException("Field [" + field.getName() + "] is not annotated as Column");
		}

		String columnDefinition = field.getAnnotation(Column.class).columnDefinition();
		if (StringUtils.isBlank(columnDefinition)) {
			throw new InvalidAnnotationException(
					"Annotation columnDefinition empty or null for field[" + field.getName() + "]");
		}
		return columnDefinition;
	}
}
