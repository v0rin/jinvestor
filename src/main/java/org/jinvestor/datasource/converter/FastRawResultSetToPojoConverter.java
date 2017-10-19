package org.jinvestor.datasource.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.jinvestor.datasource.IConverter;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;

import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @deprecated use {@link #new()} instead.
 *
 * @author Adam
 */
@Deprecated
public class FastRawResultSetToPojoConverter<R> implements IConverter<ResultSet, R> {

	private boolean isFirstTimeCalled = true;
	private IEntityMetaData<R> entityMetaData;

	private Map<String, Integer> columnMappings;

	public FastRawResultSetToPojoConverter(Class<R> type) {
		this.entityMetaData = EntityMetaDataFactory.get(type);
	}


	@Override
	public R apply(ResultSet resultSet) {
		if (isFirstTimeCalled) {
			validateColumns(resultSet, entityMetaData);
			columnMappings = createMappings(resultSet, entityMetaData);

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


	protected R createPojo(ResultSet resultSet)
			throws IllegalAccessException, InvocationTargetException, SQLException, InstantiationException {
		R instance = entityMetaData.getClazz().newInstance();
        int i = 1;
        for (Field field : instance.getClass().getDeclaredFields()) {
        	if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        		continue;
        	}
        	BeanUtils.setProperty(instance,
        						  field.getName(),
        						  resultSet.getObject(columnMappings.get(field.getName())));
        }
        return instance;
	}


	private Map<String, Integer> createMappings(ResultSet resultSet, IEntityMetaData<R> entityMetaData) {
		Map<String, Integer> columnMappings2 = new HashMap<>();
		List<String> resultSetColumns = getResultSetColumns(resultSet);
		List<String> entityColumns = Arrays.asList(entityMetaData.getColumns());

		entityColumns.forEach(entityColumn -> {
			columnMappings2.put(entityColumn, resultSetColumns.indexOf(entityColumn) + 1);
		});

		return columnMappings2;
	}


	private void validateColumns(ResultSet resultSet, IEntityMetaData<R> entityMetaData) {
		Set<String> entityColumns = Sets.newHashSet(entityMetaData.getColumns());
		Set<String> resultSetColumns = Sets.newConcurrentHashSet(getResultSetColumns(resultSet));
		checkArgument(resultSetColumns.equals(entityColumns),
				"ResultSet columns and Entity columns are not equal. " +
						"ResultSet columns=" + resultSetColumns +
						"; entity columns=" + entityColumns +
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
