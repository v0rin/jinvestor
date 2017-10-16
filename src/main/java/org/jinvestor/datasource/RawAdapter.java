package org.jinvestor.datasource;

/**
 *
 * @author Adam
 */
public class RawAdapter implements IAdapter<String[], Object[]> {

	private boolean isFirstTimeCalled = true;

	@Override
	public Object[] apply(String[] strings) {
		if (isFirstTimeCalled) {
			isFirstTimeCalled = false;
			return null;
		}
		return strings;
	}
}
