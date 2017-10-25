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
public class EntityMetaData<T> implements IEntityMetaData<T> {

    private Class<T> entityClass;

    EntityMetaData(Class<T> entityClass) {
        this.entityClass = entityClass;
    }


    @Override
    public String getTableName() {
        validateAnnotations();
        return getTableAnnotationName();
    }


    @Override
    public String[] getColumns() {
        validateAnnotations();
        Field[] fields = entityClass.getDeclaredFields();
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
        validateAnnotations();
        CreateTableSegmentBuilder builder = SQLiteQueryBuilder
                .create()
                .table(getTableName());

        Field[] fields = entityClass.getDeclaredFields();
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
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new InvalidAnnotationException(entityClass.getName() + " is not annotated as Entity");
        }
        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new InvalidAnnotationException(entityClass.getName() + " is not annotated as Table");
        }
        if (StringUtils.isBlank(entityClass.getAnnotation(Table.class).name())) {
            throw new InvalidAnnotationException("Table name not defined");
        }
    }


    private String getTableAnnotationName() {
        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new InvalidAnnotationException(entityClass.getName() + " is not annotated as Table");
        }

        String name = entityClass.getAnnotation(Table.class).name();
        if (StringUtils.isBlank(name)) {
            throw new InvalidAnnotationException("Table Annotation name empty or null for field");
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


    @Override
    public Class<T> getClazz() {
        return entityClass;
    }
}
