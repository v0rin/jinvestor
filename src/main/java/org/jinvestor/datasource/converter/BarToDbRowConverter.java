package org.jinvestor.datasource.converter;

import java.util.Map;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.entity.IEntityMetaData;

/**
 *
 * @author Adam
 */
public class BarToDbRowConverter extends FastRawCsvToDbRowConverter {

	private IConverter<String, String> dateTimeConverter;

	public BarToDbRowConverter(Map<String, String> inputToOutputColumnMappings,
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
