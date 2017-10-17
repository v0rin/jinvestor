package org.jinvestor.datasource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.jinvestor.model.entity.IEntityMetaData;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class FastRawAdapter implements IAdapter<String[], Object[]> {

	private boolean isFirstTimeCalled = true;

	private Map<String, String> inputToOutputColumnMappings;
	private IEntityMetaData entityMetaData;


	public FastRawAdapter(Map<String, String> inputToOutputColumnMappings, IEntityMetaData entityMetaData) {
		this.inputToOutputColumnMappings = inputToOutputColumnMappings;
		this.entityMetaData = entityMetaData;
	}


	@Override
	public Object[] apply(String[] strings) {
		if (isFirstTimeCalled) {
			String[] entityColumns = entityMetaData.getColumns();
			validateMappings(strings, inputToOutputColumnMappings, entityColumns);
			validateColumnOrder(strings, inputToOutputColumnMappings, entityColumns);
			isFirstTimeCalled = false;
			return new Object[0];
		}
		return strings;
	}


	/**
	 * Validates each column from (@code inputColumns} have a mapping in {@code inputToOutputColumnMappings}
	 * @param inputColumns
	 * @param inputToOutputColumnMappings
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateMappings(String[] inputColumns,
								  Map<String, String> inputToOutputColumnMappings,
								  String[] entityColumns) {
		List<String> inputColumnList = Arrays.asList(inputColumns);
		Collection<String> mappedInputColumnList = inputToOutputColumnMappings.keySet();
		Collection<String> mappedOutputColumnList = inputToOutputColumnMappings.values();
		List<String> entityColumnList = Arrays.asList(entityColumns);
		checkArgument(collectionsEqualIgnoresOrder(inputColumnList, mappedInputColumnList) &&
					  collectionsEqualIgnoresOrder(mappedOutputColumnList, entityColumnList),
					  "Incorrect mappings: " + getAsString(inputColumns, inputToOutputColumnMappings, entityColumns));
	}


	private void validateColumnOrder(String[] inputColumns,
									 Map<String, String> inputToOutputColumnMappings,
									 String[] entityColumns) {
		List<String> mappedInputColumns = Arrays.asList(inputColumns)
											.stream()
											.map(inputToOutputColumnMappings::get)
											.collect(Collectors.toList());
		if (!mappedInputColumns.equals(Arrays.asList(entityColumns))) {
			throw new NotImplementedException("Input columns in different order than entity columms. " +
											  getAsString(inputColumns, inputToOutputColumnMappings, entityColumns) +
											  ". This is not supported yet");
		}
	}


	private String getAsString(String[] inputColumns,
							   Map<String, String> inputToOutputColumnMappings,
							   String[] entityColumns) {
		return new StringBuilder()
				.append("input columns=").append(Arrays.asList(inputColumns))
			    .append("; output column mappings=").append(inputToOutputColumnMappings)
			    .append("; entity column=").append(Arrays.asList(entityColumns))
			    .toString();
	}


	private <T> boolean collectionsEqualIgnoresOrder(Collection<T> col1, Collection<T> col2) {
		return new HashSet<>(col1).equals(new HashSet<>(col2));
	}
}
