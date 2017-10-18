package org.jinvestor.datasource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class FastRawResultSetToPojoConverter<R> implements IConverter<ResultSet, R> {

	private boolean isFirstTimeCalled = true;
	private IEntityMetaData<R> entityMetaData;

	public FastRawResultSetToPojoConverter(Class<R> type) {
		this.entityMetaData = EntityMetaDataFactory.get(type);
	}


	@Override
	public R apply(ResultSet resultSet) {
		if (isFirstTimeCalled) {
			validateColumns(resultSet, entityMetaData);

			isFirstTimeCalled = false;
		}

		try {
			return createPojo(resultSet);
		} catch (InstantiationException |
				 IllegalAccessException |
				 InvocationTargetException |
				 SQLException e) {
			throw new AppRuntimeException(e);
		}
	}


	private R createPojo(ResultSet resultSet)
			throws IllegalAccessException, InvocationTargetException, SQLException, InstantiationException {
		R instance = entityMetaData.getClazz().newInstance();
        int i = 1;
        for (Field field : instance.getClass().getDeclaredFields()) {
        	if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        		continue;
        	}
        	BeanUtils.setProperty(instance, field.getName(), resultSet.getObject(i++));
        }
        return instance;
	}


	private void validateColumns(ResultSet resultSet, IEntityMetaData<R> entityMetaData) {
		String[] entityColumns = entityMetaData.getColumns();
		List<String> resultSetColumns = getResultSetColumns(resultSet);
		checkArgument(resultSetColumns.equals(Arrays.asList(entityColumns)),
					  "ResultSet columns and Entity columns are not equal. " +
					  "ResultSet columns=" + resultSetColumns +
					  "; entity columns=" + Arrays.asList(entityColumns) +
					  " This is not supported in this fast implementation");
	}


	private List<String> getResultSetColumns(ResultSet resultSet) {
		List<String> resultSetColumns = new ArrayList<>();
		try {
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				resultSetColumns.add(resultSet.getMetaData().getColumnName(i));
			}
		} catch (SQLException e) {
			throw new AppRuntimeException(e);
		}

		return resultSetColumns;
	}

}
