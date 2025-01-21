package org.hung.aws.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.UUID;

import org.hung.aws.lambda.function.customer.UpdateCustomerFunction;
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

public class UpdateCustomerFunctionTest extends AbstractCustomerFunctionTest {

    private UpdateCustomerFunction testee;

    @BeforeEach
    public void functionSetup() {
        this.testee = new UpdateCustomerFunction();
    }

    @ParameterizedTest
    @HandlerParams(
        events = @Events(folder = "events/UpdateCustomerFunctionTest/request/", type = APIGatewayProxyRequestEvent.class), 
        responses = @Responses(folder = "events/UpdateCustomerFunctionTest/response/", type = APIGatewayProxyResponseEvent.class))
    void handleRequestTest(APIGatewayProxyRequestEvent request, APIGatewayProxyResponseEvent expected) throws JSONException {
        
        Customer entity = new Customer();
        entity.setId(UUID.fromString("da78c07c-290b-4842-a1e0-a40fa692f043"));
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john.doe@gmail.com");

        table.putItem(entity);

        APIGatewayProxyResponseEvent actual = testee.handleRequest(request, context);

        assertThat(actual.getStatusCode(), equalTo(expected.getStatusCode()));

        JSONAssert.assertEquals(expected.getBody(),actual.getBody(),JSONCompareMode.LENIENT);
    }
}
