package org.jinvestor.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jinvestor.exception.AppRuntimeException;

/**
 *
 * @author Adam
 */
public class SqlUtil {

	private SqlUtil() {
		throw new InstantiationError("This class should not be instantiated");
	}

	public static List<String> getResultSetColumns(ResultSet resultSet) {
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
