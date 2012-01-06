package com.dlecan.sqli.wwcc;

/**
 * Exceptions du programme WWCC.
 * 
 * @author dlecan
 */
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
