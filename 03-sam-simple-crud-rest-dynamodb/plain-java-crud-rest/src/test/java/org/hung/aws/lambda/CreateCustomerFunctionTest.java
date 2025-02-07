package org.hung.aws.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.hung.aws.lambda.function.customer.CreateCustomerFunction;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Events;
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Responses;

public class CreateCustomerFunctionTest extends AbstractCustomerFunctionTest {

    private CreateCustomerFunction testee;

    @BeforeEach
    public void functionSetup() {
        this.testee = new CreateCustomerFunction();
    }

    @ParameterizedTest
    @HandlerParams(
        events = @Events(folder = "events/CreateCustomerFunctionTest/request/", type = APIGatewayProxyRequestEvent.class), 
        responses = @Responses(folder = "events/CreateCustomerFunctionTest/response/", type = APIGatewayProxyResponseEvent.class))
    void handleRequestTest(APIGatewayProxyRequestEvent request, APIGatewayProxyResponseEvent expected) throws JSONException {

        APIGatewayProxyResponseEvent actual = testee.handleRequest(request, context);

        assertThat(actual.getStatusCode(), equalTo(expected.getStatusCode()));

        JSONAssert.assertEquals(expected.getBody(),actual.getBody(),
            new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("id",(e,a) -> true)));
    }
}
