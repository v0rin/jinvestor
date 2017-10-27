package org.jinvestor.etl;

import java.io.IOException;

/**
 *
 * @author Adam
 */
public interface IEtlJob {

    void execute() throws IOException;

}
