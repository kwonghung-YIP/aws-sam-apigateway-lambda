package org.hung.aws.lambda.config;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

public interface DynamoDBLambdaHandler<T> extends DefaultAWSConfigure {

    default DynamoDbTable<T> getDynamoDBTable(String tableName, Class<T> modelClass) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClient(UrlConnectionHttpClient.create());

        System.out.println("isSamLocal:"+isSamLocal());
        if (isSamLocal()) {
            builder.endpointOverride(URI.create("http://localstack-main:4566"));
        } else {
            builder.region(getDefaultRegion());
        }

        DynamoDbClient client = builder.build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();

        return enhancedClient.table(tableName, TableSchema.fromBean(modelClass));
    }
}
