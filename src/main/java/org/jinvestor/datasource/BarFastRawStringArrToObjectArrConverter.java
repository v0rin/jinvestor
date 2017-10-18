package org.jinvestor.datasource;

import java.util.Map;

import org.jinvestor.model.entity.IEntityMetaData;

/**
 *
 * @author Adam
 */
public class BarFastRawStringArrToObjectArrConverter extends FastRawStringArrToObjectArrConverter {

	private IConverter<String, String> dateTimeConverter;

	public BarFastRawStringArrToObjectArrConverter(Map<String, String> inputToOutputColumnMappings,
			  									   IEntityMetaData<?> entityMetaData,
			  									   IConverter<String, String> dateTimeConverter) {
		super(inputToOutputColumnMappings, entityMetaData);
		this.dateTimeConverter = dateTimeConverter;
	}


	@Override
	public Object[] apply(String[] strings) {
		Object[] objects = super.apply(strings);
		if (objects.length > 0) {
			objects[1] = dateTimeConverter.apply((String)objects[1]);
		}
		return objects;
	}
}
