## Features cover in this example
- Develop a simple CRUD REST with AWS Gateway, Lambda, DynamoDB, and JDK 21
- Develop a Lambda Token Authorizer to protect the API with Basic Authentication
- Package the entire application in a AWS SAM template, an extension of AWS Cloudformation
- Test locally with sam local start-api and LocalStack docker compose stack
- Deploy to AWS as a Cloudformation stack with AWS CLI and SAM CLI
- JUnit test with TestContainer and LocalStack

## Problem and solution found during built thie example
- When customized conversion is necessary for the lambda function request and response, it should implement the RequestStreamHandler 
instead of the RequestHandler interface, the former one pass request and response as InputStream and OutputStream.
When implemented the BasicAuthTokenAuthorizer, it was expected to return an IAM inline policy in a explicit JSON format (case sensitive),
the properties in PolicyDocument such as "Action" and "Statement", their initial letter is expected in uppercase which is different form
standard JSON conversion in Java. First I applied the @JsonProperty annotation to adjust the property name, however I got the 502 error
from API Gateway which complained invalid response from authorizer until I replaced the interface.
- There is no way to return a 401 from Lambda Token Authorizer, as long as the token has been pass into the authorizer, the implementation
has to return a valid IAM Policy. The policy could only deny all accesses even the login has not been registered, and a denied access always
lead to 403 response.

## Prepare the local environment
- install jdk, maven, curl, and jq
- install aws cli
- install aws sam
- setup the aws cli profile - default(your aws account), localstack

## Test locally

### Running localstack in docker compose, and create customer dynamodb table
```bash
cd localstack

docker compose up -d

aws dynamodb create-table \
    --profile localstack \
    --table-name customer \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --billing-mode PAY_PER_REQUEST
```
### Start API Gateway with SAM
```bash
cd sam-simple-crud-rest-dynamodb

sam build

sam validate

# the localstack_default network is created after started the localstack
sam local start-api --profile localstack \
    --docker-network localstack_default \
	--env-vars events/env.json --debug
```

### Run the test script
```bash
cd sam-simple-crud-rest-dynamodb

./test-api-gateway.sh
```

## Deploy and Test in AWS

### Grant required permissions to the account in IAM Identity center which for aws cli login
- ViewOnlyAccess + [this inline IAM policy](inline-policy-for-aws-deployment.md)

### Deploy the cloudformation stack to AWS
```bash
cd sam-simple-crud-rest-dynamodb

sam build
sam validate

sam deploy --guided
# adjust the region for your own account
# continue the deployment after review the ChangeSet
```

### Public URL for the API Gateway

template => https://<api-gateway-id>.execute-api.<region>.amazonzws.com/<stage>/customer/

api-gateway-id is available in the cloudformation deployment output, also in the API Gateway Console

e.g. https://780fhbov3i.execute-api.eu-north-1.amazonaws.com/dev/customer/ 

### Run the test script
```bash
# update the region setting in the testscript
./test-api-gateway.sh <<api-gateway-id>>
```

### Clear up
```bash
sam delete
# empty the SAM managed S3 bucket
# delete the SAM managed cloudformation stack
aws sso logout
# disable the account
```

## curl command for local API Gateway testing

```bash
curl -s -v -X POST --user admin:passwd \
    -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}" \
    --output customer.json \
    --write-out %{http_code} \
    http://localhost:3000/customer/

cat customer.json| jq "."
export customer_uuid=`jq -r ".id" < customer.json`

curl -X GET --user admin:passwd \
    "http://localhost:3000/customer/${customer_uuid}"

curl -X PUT --user admin:passwd \
    -d "{\"id\":\"${customer_uuid}\",\"firstName\":\"Peter\",\"lastName\":\"Pan\",\"email\":\"peter.pan@somewhere.com\"}" \
    "http://localhost:3000/customer/${customer_uuid}"

curl -X DELETE --user admin:passwd \
    "http://localhost:3000/customer/${customer_uuid}"

rm customer.json
```


```bash
echo "{}"|sam local invoke CreateCustomerFunction --profile localstack -e - --env-vars events/env.json
sam local invoke ReadCustomerFunction --profile localstack
sam local invoke UpdateCustomerFunction --profile localstack
sam local invoke DeleteCustomerFunction --profile localstack

sam local invoke CreateCustomerFunction --profile localstack \
    --docker-network localstack_default \
    --event events/create-customer-function/ok.json \
    --env-vars events/env.json
```

## Reference

- [GitHub - java-crud-lambda-example](https://github.com/aws-samples/java-crud-microservice-template)
- [Use API Gateway Lambda Authorizers](https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-use-lambda-authorizer.html)
- [SAM - Control API access with your AWS SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-controlling-access-to-apis.html)
- [SAM - AWS SAM Policy template list](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-policy-template-list.html)
- [Building Lambda with Java](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html)
- [Run local AWS SAM - Environment Variable File](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-using-start-api.html)
- [GitHub - aws-java-lambda-test project](https://github.com/aws/aws-lambda-java-libs/tree/main/aws-lambda-java-tests)
- [AWS Java SDK 2.0:Environment variables and System properties](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-env-and-system-props.html)