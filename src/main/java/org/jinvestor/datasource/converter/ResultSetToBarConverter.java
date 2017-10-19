package org.jinvestor.datasource.converter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jinvestor.model.Bar;

/**
 *
 * @author Adam
 */
public class ResultSetToBarConverter extends FastRawResultSetToPojoConverter<Bar> {

	public ResultSetToBarConverter(Class<Bar> type) {
		super(type);
	}

	@Override
	protected Bar createPojo(ResultSet resultSet) throws SQLException {
		return new Bar(resultSet.getString(1),
					   resultSet.getTimestamp(2),
					   resultSet.getDouble(3),
					   resultSet.getDouble(4),
					   resultSet.getDouble(5),
					   resultSet.getDouble(6),
					   resultSet.getLong(7));
	}
}