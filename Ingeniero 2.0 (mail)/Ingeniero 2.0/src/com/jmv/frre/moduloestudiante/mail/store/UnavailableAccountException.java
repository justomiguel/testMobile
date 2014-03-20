package com.jmv.frre.moduloestudiante.mail.store;

import com.jmv.frre.moduloestudiante.Account;

/**
 * An {@link Account} is not
 * {@link Account#isAvailable(android.content.Context)}.<br/>
 * The operation may be retried later.
 */
public class UnavailableAccountException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1827283277120501465L;

    public UnavailableAccountException() {
        super("please try again later");
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public UnavailableAccountException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * @param detailMessage
     */
    public UnavailableAccountException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable
     */
    public UnavailableAccountException(Throwable throwable) {
        super(throwable);
    }
}
