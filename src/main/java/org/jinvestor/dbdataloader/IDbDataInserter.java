package org.jinvestor.dbdataloader;

import java.io.IOException;

/**
*
* @author Adam
*/
public interface IDbDataInserter<T> extends AutoCloseable {

	void insert(T from) throws IOException;

}
