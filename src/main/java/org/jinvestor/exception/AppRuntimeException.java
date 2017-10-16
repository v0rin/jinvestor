package org.jinvestor.exception;

/**
 *
 * @author Adam
 */
public class AppRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4699125731633946997L;

	public AppRuntimeException() {
		super();
	}

	public AppRuntimeException(String message) {
		super(message);
	}

	public AppRuntimeException(Throwable t) {
		super(t);
	}

	public AppRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
