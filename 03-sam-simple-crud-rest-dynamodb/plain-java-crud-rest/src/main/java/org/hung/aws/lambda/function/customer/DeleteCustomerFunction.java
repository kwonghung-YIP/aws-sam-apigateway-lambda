package org.hung.aws.lambda.function.customer;

import java.util.UUID;

import org.hung.aws.lambda.exception.HttpErrorResponseException;
import org.hung.aws.lambda.model.Customer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

public class DeleteCustomerFunction extends AbstractCustomerFunction {

    @Override
    protected void handleAPIGatewayProxyEvent(APIGatewayProxyRequestEvent request,
            APIGatewayProxyResponseEvent response,
            Context context) {

        try {
            UUID id = extractIdParam(request);

            Customer oldEntity = findEntity(id);
            Customer deletedEntity = table.deleteItem(idToTableKey(id));
            setResponseBody(response, deletedEntity, 200);
        } catch (HttpErrorResponseException e) {
            getLog().log(e.getMessage(), LogLevel.ERROR);
            response.withStatusCode(e.getStatusCode())
                    .withBody(error(e.getTitle(), e.getMessage()));
        }
    }

}
