package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public interface IEntityMetaData<T> {

	Class<T> getClazz();

	String getTableName();

	String[] getColumns();

	String getCreateTableSql();

}
