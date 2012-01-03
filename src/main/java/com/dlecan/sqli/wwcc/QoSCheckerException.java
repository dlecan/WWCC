package com.dlecan.sqli.wwcc;

public class QoSCheckerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QoSCheckerException() {
		super();
	}

	public QoSCheckerException(String message, Throwable cause) {
		super(message, cause);
	}

	public QoSCheckerException(String message) {
		super(message);
	}

	public QoSCheckerException(Throwable cause) {
		super(cause);
	}

}
