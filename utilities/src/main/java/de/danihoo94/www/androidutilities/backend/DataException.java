package de.danihoo94.www.androidutilities.backend;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class DataException extends Exception {
    // client error status
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_AUTH_FAILURE = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_TIMEOUT = 408;

    // server error status
    public static final int STATUS_SERVER_ERROR = 500;
    public static final int STATUS_SERVICE_UNAVAILABLE = 503;

    private final int htmlStatus;

    public DataException(String message) {
        super(message);
        this.htmlStatus = STATUS_SERVICE_UNAVAILABLE;
    }

    public DataException(String message, int status) {
        super(message);
        this.htmlStatus = status;
    }

    public int getHtmlStatus() {
        return htmlStatus;
    }
}
