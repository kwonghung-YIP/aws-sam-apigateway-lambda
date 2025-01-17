package org.hung.aws.lambda.function;

import java.util.Set;

import org.hung.aws.lambda.config.DynamoDBLambdaHandler;
import org.hung.aws.lambda.exception.BadRequestException;
import org.hung.aws.lambda.exception.EntityNotFoundException;
import org.hung.aws.lambda.exception.InternalServerErrorException;
import org.hung.aws.lambda.model.Customer;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.utils.StringUtils;

public abstract class AbstractLambdaCrudFunction<T, K> implements DynamoDBLambdaHandler<T> {

    final protected DynamoDbTable<T> table;

    final protected ObjectMapper objectMapper;

    final protected Validator validator;

    private LambdaLogger log;

    abstract protected String getTableName();

    abstract protected Class<T> getItemClass();

    abstract protected K stringToId(String id);

    abstract protected String idToString(K id);

    abstract protected Key idToTableKey(K id);

    protected LambdaLogger getLog() {
        return this.log;
    }
    protected void setLog(LambdaLogger log) {
        this.log = log;
    }

    protected AbstractLambdaCrudFunction() {
        this.table = getDynamoDBTable(getTableName(), getItemClass());
        this.objectMapper = new ObjectMapper();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public K extractIdParam(APIGatewayProxyRequestEvent request) throws BadRequestException {
        String id = request.getPathParameters().get("id");
        log.log("extract {id} from rquest path:" + id, LogLevel.INFO);
        if (StringUtils.isEmpty(id)) {
            throw new BadRequestException("Cannot find {id} parameter in path parameters");
        }
        try {
            return stringToId(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public T extractEntity(APIGatewayProxyRequestEvent request) throws BadRequestException {
        try {
            return objectMapper.readValue(request.getBody(), getItemClass());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Fail to parse JSON request body", e);
        }
    }

    public void validateEntity(Customer entity) throws BadRequestException {
        Set<ConstraintViolation<Customer>> errors = validator.validate(entity);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Request body validation error");
        }
    }

    public T findEntity(K id) throws EntityNotFoundException {
        T existing = table.getItem(idToTableKey(id));
        if (existing == null) {
            throw new EntityNotFoundException(id);
        }
        return existing;
    }

    public void setResponseBody(APIGatewayProxyResponseEvent response, T entity,
            int expectedStatusCode) throws InternalServerErrorException {
        try {
            response.withStatusCode(expectedStatusCode)
                    .withBody(objectMapper.writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Failed to write entity as JSON", e);
        }
    }

    public String error(String error, String message) {
        return String.format("""
                {"error":"%s","message":"%s"}
                """, error, message);
    }

}
