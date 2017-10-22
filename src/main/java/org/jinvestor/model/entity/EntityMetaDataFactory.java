package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public class EntityMetaDataFactory {

    private EntityMetaDataFactory() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static <T> IEntityMetaData<T> get(Class<T> entityClass) {
        return new EntityMetaData<>(entityClass);
    }
}
