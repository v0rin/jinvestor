package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public interface IEntityMetaData<T> {

	String getTableName();

	String[] getColumns();

	String getCreateTableSql();

}
