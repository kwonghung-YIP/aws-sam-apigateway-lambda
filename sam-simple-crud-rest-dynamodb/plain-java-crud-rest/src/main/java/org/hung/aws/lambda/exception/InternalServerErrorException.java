package org.hung.aws.lambda.exception;

public class InternalServerErrorException extends HttpErrorResponseException {

    public InternalServerErrorException(String msg, Throwable cause) {
        super(500, "Internal Server Error", msg, cause);
    }
}
