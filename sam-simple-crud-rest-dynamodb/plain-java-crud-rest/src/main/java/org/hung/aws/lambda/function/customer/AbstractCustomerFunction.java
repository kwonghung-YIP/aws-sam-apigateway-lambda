package org.hung.aws.lambda.function.customer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hung.aws.lambda.function.AbstractLambdaCrudFunction;
import org.hung.aws.lambda.model.Customer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import software.amazon.awssdk.enhanced.dynamodb.Key;

public abstract class AbstractCustomerFunction extends AbstractLambdaCrudFunction<Customer, UUID>
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected abstract void handleAPIGatewayProxyEvent(final APIGatewayProxyRequestEvent request,
            final APIGatewayProxyResponseEvent response, final Context context);

    @Override
    protected Class<Customer> getItemClass() {
        return Customer.class;
    }

    @Override
    protected String getTableName() {
        String tableName = System.getenv("CUSTOMER_TABLE");
        if (tableName==null) {
            tableName = System.getProperty("customer.table");
        }
        System.out.println("Tablename:"+tableName);
        return tableName;
    }

    @Override
    protected UUID stringToId(String id) {
        return UUID.fromString(id);
    }

    @Override
    protected String idToString(UUID id) {
        return id.toString();
    }

    @Override
    protected Key idToTableKey(UUID id) {
        return Key.builder().partitionValue(id.toString()).build();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request,
            final Context context) {
        setLog(context.getLogger());

        getLog().log("request.body:"+request.getBody(),LogLevel.DEBUG);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        handleAPIGatewayProxyEvent(request, response, context);

        return response;
    }

}
