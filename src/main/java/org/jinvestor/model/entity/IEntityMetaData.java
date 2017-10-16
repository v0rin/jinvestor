package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public interface IEntityMetaData {

	String getTableName();

	String[] getColumns();

	String getCreateTableSql();

}
