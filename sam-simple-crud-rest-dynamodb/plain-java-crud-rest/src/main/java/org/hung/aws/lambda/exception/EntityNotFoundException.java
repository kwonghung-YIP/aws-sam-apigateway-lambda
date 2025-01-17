package org.hung.aws.lambda.exception;

public class EntityNotFoundException extends HttpErrorResponseException {

    public EntityNotFoundException(Object id) {
        super(404, "Not Found", "Cannot find entity by Id:" + id);
    }
}
