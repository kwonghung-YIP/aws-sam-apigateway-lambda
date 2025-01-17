package org.hung.aws.lambda.exception;

public class BadRequestException extends HttpErrorResponseException {

    public BadRequestException(String message) {
        super(400, "Bad Request", message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(400, "Bad Request", message, cause);
    }
}
