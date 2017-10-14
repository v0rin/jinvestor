package org.vorin.trading.dbdataloader;

import java.io.IOException;

public interface IDbDataLoader<T> extends AutoCloseable {

	void load(T from) throws IOException;

}
