package org.jinvestor.datasource.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class FastRawCsvToDbRowConverter implements IConverter<String[], Object[]> {

    private boolean isFirstTimeCalled = true;

    private Map<String, String> inputToOutputColumnMappings;
    private IEntityMetaData<?> entityMetaData;


    public FastRawCsvToDbRowConverter(Map<String,
                                      String> inputToOutputColumnMappings,
                                      Class<?> entityClass) {
        this.inputToOutputColumnMappings = inputToOutputColumnMappings;
        this.entityMetaData = EntityMetaDataFactory.get(entityClass);
    }


    @Override
    public Object[] apply(String[] strings) {
        if (isFirstTimeCalled) {
            validate(strings, inputToOutputColumnMappings, entityMetaData);
            isFirstTimeCalled = false;
            return new Object[0];
        }
        return strings;
    }


    private void validate(String[] strings,
                          Map<String, String> inputToOutputColumnMappings,
                          IEntityMetaData<?> entityMetaData) {
        String[] entityColumns = entityMetaData.getColumns();
        validateMappings(strings, inputToOutputColumnMappings, entityColumns);
        validateColumnOrder(strings, inputToOutputColumnMappings, entityColumns);
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
            throw new UnsupportedOperationException("Input columns in different order than entity columms. " +
                                              getAsString(inputColumns, inputToOutputColumnMappings, entityColumns) +
                                              ". This is not supported in this fast implementation");
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
