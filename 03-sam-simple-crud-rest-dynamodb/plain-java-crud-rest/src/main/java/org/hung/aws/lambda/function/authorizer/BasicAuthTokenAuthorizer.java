package org.hung.aws.lambda.function.authorizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hung.aws.lambda.function.authorizer.model.AuthorizerResponse;
import org.hung.aws.lambda.function.authorizer.model.PolicyDocument;
import org.hung.aws.lambda.function.authorizer.model.Statement;
import org.hung.aws.lambda.function.authorizer.model.TokenAuthorizerRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.utils.StringUtils;

// In order to use the customized JSON serilaization config (for capitalized property name in PolicyDocument), 
// here we implements RequestStreamHander instead of RequestHandler

public class BasicAuthTokenAuthorizer implements RequestStreamHandler {

    private final ObjectMapper mapper;
    private final String awsAccountId;
    private final String awsRegion;
    private final String awsApiGwId;
    private final String awsStage;

    public BasicAuthTokenAuthorizer() {
        awsAccountId = System.getenv("AWS_ACCOUNTID");
        awsRegion = System.getenv("AWS_REGION");
        awsApiGwId = System.getenv("APIGW_ID");
        awsStage = System.getenv("APIGW_STAGE");
        mapper = new ObjectMapper();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        LambdaLogger log = context.getLogger();

        log.log("AWS_ACCOUNTID:"+awsAccountId,LogLevel.DEBUG);
        log.log("AWS_REGION:"+awsRegion,LogLevel.DEBUG);
        log.log("APIGW_ID:"+awsApiGwId,LogLevel.DEBUG);
        log.log("APIGW_STAGE:"+awsStage,LogLevel.DEBUG);

        TokenAuthorizerRequest request = mapper.readValue(input, TokenAuthorizerRequest.class);

        log.log("authorizationToken:" + request.getAuthorizationToken(), LogLevel.INFO);
        String[] credentials = parseBasicAuthHeader(request.getAuthorizationToken());
        String login = credentials[0];

        log.log("Basic Auth login:" + login, LogLevel.INFO);

        AuthorizerResponse response = new AuthorizerResponse();
        if (StringUtils.isEmpty(login) || "passwd".compareTo(credentials[1]) != 0) {
            response.setPrincipalId("unauthorized");
            response.setPolicyDocument(buildDenyPolicyDocument(request.getMethodArn()));
            mapper.writeValue(output, response);
            return;
        }

        response.setPrincipalId(login);
        response.setPolicyDocument(buildPolicyDocument(login));
        // response.setContext(Map.of("key1","abcd1234"));

        mapper.writeValue(output, response);
    }

    private String[] parseBasicAuthHeader(String token) {
        String base64 = token.substring("Basic ".length()).trim();
        String credential = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        return credential.split(":", 2);
    }

    private PolicyDocument buildDenyPolicyDocument(String methodArn) {
        Statement statement = new Statement();
        statement.setAction("execute-api:Invoke");
        statement.setEffect("Deny");
        statement.setResource(List.of("*"));

        PolicyDocument policy = new PolicyDocument();
        policy.setVersion("2012-10-17");
        policy.setStatement(List.of(statement));

        return policy;
    }

    private PolicyDocument buildPolicyDocument(String login) {
        // "methodArn":"arn:aws:execute-api:us-east-1:123456789012:1234567890/Dev/POST/customer/"
        List<String> userAllowResources = getResourceArn("GET");
        List<String> adminAllowResources = getResourceArn("GET", "POST", "PUT", "DELETE");

        Statement statement = new Statement();
        statement.setAction("execute-api:Invoke");
        statement.setEffect("Allow");
        statement.setResource("admin".equalsIgnoreCase(login) ? adminAllowResources : userAllowResources);

        PolicyDocument policy = new PolicyDocument();
        policy.setVersion("2012-10-17");
        policy.setStatement(List.of(statement));

        return policy;
    }

    private List<String> getResourceArn(String... methods) {
        return Stream.of(methods)
                .map(method -> String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/customer/*",
                        nvl(awsRegion, "*"),
                        nvl(awsAccountId, "*"),
                        nvl(awsApiGwId, "*"),
                        nvl(awsStage, "*"), method))
                .collect(Collectors.toList());
    }

    private String nvl(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

}
