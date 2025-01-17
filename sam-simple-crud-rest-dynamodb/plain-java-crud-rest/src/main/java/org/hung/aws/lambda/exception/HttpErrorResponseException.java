package org.hung.aws.lambda.exception;

public abstract class HttpErrorResponseException extends Exception {

    private final int statusCode;
    private final String title;

    public HttpErrorResponseException(int statusCode, String title, String message) {
        super(message);
        this.statusCode = statusCode;
        this.title = title;
    }

    public HttpErrorResponseException(int statusCode, String title, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.title = title;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }
}
