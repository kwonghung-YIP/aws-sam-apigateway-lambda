package org.hung.aws.lambda;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.net.URI;

import org.hung.aws.lambda.function.customer.ReadCustomerFunction;
import org.hung.aws.lambda.model.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ExtendWith(MockitoExtension.class)
@Testcontainers
public abstract class AbstractCustomerFunctionTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.0.3"));

    @Mock
    protected Context context;

    @Mock
    private LambdaLogger logger;

    protected ObjectMapper mapper;

    protected DynamoDbTable<Customer> table;

    @BeforeAll
    static public void beforeAll() throws UnsupportedOperationException, IOException, InterruptedException {
        System.setProperty("aws.region","us-east-1");
        System.setProperty("aws.profile","localstack");
        System.setProperty("aws.accessKeyId",localstack.getAccessKey());
        System.setProperty("aws.secretAccessKey",localstack.getSecretKey());
        System.setProperty("customer.table", "customer");
        //System.out.println("endpoint-override:"+localstack.getEndpointOverride(Service.DYNAMODB).toString());
        System.setProperty("aws.local.endpoint",localstack.getEndpointOverride(Service.DYNAMODB).toString());

        ExecResult result = localstack.execInContainer("awslocal","dynamodb",
            "create-table","--table-name","customer",
            "--key-schema","AttributeName=id,KeyType=HASH",
            "--attribute-definitions","AttributeName=id,AttributeType=S",
            "--billing-mode","PAY_PER_REQUEST");

        //System.out.println(result.getStdout());
        //System.out.println(result.getStderr());
    }

    @BeforeEach
    public void commonSetup() {
        String localEndpontUri = System.getProperty("aws.local.endpoint");
        String tableName = System.getProperty("customer.table");
        this.table = createDynamoDBTable(localEndpontUri,tableName,Customer.class);

        this.mapper = new ObjectMapper();

        BDDMockito.given(context.getLogger()).willReturn(logger);
        BDDMockito.doAnswer((invocation) -> {
            String msg = invocation.getArgument(0);
            System.out.println(msg);
            return null;
        }).when(logger).log(anyString(),any());
    }

    static private <T> DynamoDbTable<T> createDynamoDBTable(String localEndpointUri, String tableName, Class<T> entityClass) {
        DynamoDbClient standard = DynamoDbClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClient(UrlConnectionHttpClient.create())
            .endpointOverride(URI.create(localEndpointUri))
            .build();

        DynamoDbEnhancedClient enhanced = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(standard)
            .build();

        return enhanced.table(tableName, TableSchema.fromClass(entityClass));
    }
}
