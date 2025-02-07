package org.hung.aws.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.UUID;

import org.hung.aws.lambda.function.customer.DeleteCustomerFunction;
import org.hung.aws.lambda.model.Customer;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Events;
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Responses;

public class DeleteCustomerFunctionTest extends AbstractCustomerFunctionTest {

    private DeleteCustomerFunction testee;

    @BeforeEach
    public void functionSetup() {
        this.testee = new DeleteCustomerFunction();
    }

    @ParameterizedTest
    @HandlerParams(
        events = @Events(folder = "events/DeleteCustomerFunctionTest/request/", type = APIGatewayProxyRequestEvent.class), 
        responses = @Responses(folder = "events/DeleteCustomerFunctionTest/response/", type = APIGatewayProxyResponseEvent.class))
    void handleRequestTest(APIGatewayProxyRequestEvent request, APIGatewayProxyResponseEvent expected) throws JSONException {

        Customer entity = new Customer();
        entity.setId(UUID.fromString("9910f7b8-b557-4611-bdcd-b99f33e5e3c7"));
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john.doe@gmail.com");

        table.putItem(entity);
        
        APIGatewayProxyResponseEvent actual = testee.handleRequest(request, context);

        assertThat(actual.getStatusCode(), equalTo(expected.getStatusCode()));

        JSONAssert.assertEquals(expected.getBody(),actual.getBody(),JSONCompareMode.LENIENT);
    }
}
