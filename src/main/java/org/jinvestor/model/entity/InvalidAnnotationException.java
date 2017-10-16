package org.jinvestor.model.entity;

/**
 *
 * @author Adam
 */
public class InvalidAnnotationException extends RuntimeException {

	private static final long serialVersionUID = 4130101135745518041L;

	public InvalidAnnotationException() {
		super();
	}

	public InvalidAnnotationException(String message) {
		super(message);
	}

	public InvalidAnnotationException(Throwable t) {
		super(t);
	}

	public InvalidAnnotationException(String message, Throwable t) {
		super(message, t);
	}

}
