package org.hung.aws.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.UUID;

import org.hung.aws.lambda.function.customer.ReadCustomerFunction;
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
import com.fasterxml.jackson.core.JsonProcessingException;

public class ReadCustomerFunctionTest extends AbstractCustomerFunctionTest {

    private ReadCustomerFunction testee;

    @BeforeEach
    public void functionSetup() {
        this.testee = new ReadCustomerFunction();
    }

    @ParameterizedTest
    @HandlerParams(
        events = @Events(folder = "events/ReadCustomerFunctionTest/request/", type = APIGatewayProxyRequestEvent.class), 
        responses = @Responses(folder = "events/ReadCustomerFunctionTest/response/", type = APIGatewayProxyResponseEvent.class))
    void handleRequestTest(APIGatewayProxyRequestEvent request, APIGatewayProxyResponseEvent expected) throws JsonProcessingException, JSONException {

        Customer entity = new Customer();
        entity.setId(UUID.fromString("c7a9d792-d518-45d5-9d8e-c13f2b2a86e2"));
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john.doe@gmail.com");

        table.putItem(entity);

        APIGatewayProxyResponseEvent actual = testee.handleRequest(request, context);

        assertThat(actual.getStatusCode(), equalTo(expected.getStatusCode()));

        JSONAssert.assertEquals(mapper.writeValueAsString(expected.getBody()),
            mapper.writeValueAsString(actual.getBody()),
            JSONCompareMode.LENIENT);
    }

}
