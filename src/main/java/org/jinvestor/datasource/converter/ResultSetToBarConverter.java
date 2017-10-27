package org.jinvestor.datasource.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.jinvestor.datasource.SqlUtil;
import org.jinvestor.exception.AppRuntimeException;
import org.jinvestor.model.Bar;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class ResultSetToBarConverter implements IConverter<ResultSet, Bar> {

    private boolean isFirstTimeCalled = true;
    private IEntityMetaData<Bar> entityMetaData;

    public ResultSetToBarConverter() {
        this.entityMetaData = EntityMetaDataFactory.get(Bar.class);
    }

    @Override
    public Bar apply(ResultSet resultSet) {
        if (isFirstTimeCalled) {
            validateColumns(resultSet, entityMetaData);
            isFirstTimeCalled = false;
        }

        try {
            int i = 1;
            return new Bar(resultSet.getString(i++),
                           resultSet.getTimestamp(i++),
                           resultSet.getDouble(i++),
                           resultSet.getDouble(i++),
                           resultSet.getDouble(i++),
                           resultSet.getDouble(i++),
                           resultSet.getLong(i++),
                           resultSet.getString(i));
        }
        catch (SQLException e) {
            throw new AppRuntimeException(e);
        }
    }

    private void validateColumns(ResultSet resultSet, IEntityMetaData<Bar> entityMetaData) {
        List<String> entityColumns = Arrays.asList(entityMetaData.getColumns());
        List<String> resultSetColumns = SqlUtil.getResultSetColumns(resultSet);
        checkArgument(resultSetColumns.equals(entityColumns),
                "ResultSet columns and Entity columns need to be equal to use this converter. " +
                "ResultSet columns=" + resultSetColumns + "; entity columns=" + entityColumns);
    }
}