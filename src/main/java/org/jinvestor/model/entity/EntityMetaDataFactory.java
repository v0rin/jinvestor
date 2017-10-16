package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public class EntityMetaDataFactory {

	public static <T> IEntityMetaData<T> get(Class<T> clazz) {
		return new EntityMetaData<T>(clazz);
	}
}
