

## Features cover in this example

## Problem and solution found during built thie example


```bash
sam local generate-event apigateway aws-proxy --debug \
    --method "POST" --path "customer/" \
    --env-vars events/env.json \
    --body "{}"
```

```bash
echo "{}"|sam local invoke CreateCustomerFunction --profile localstack -e - --env-vars events/env.json
sam local invoke ReadCustomerFunction --profile localstack
sam local invoke UpdateCustomerFunction --profile localstack
sam local invoke DeleteCustomerFunction --profile localstack

aws dynamodb create-table \
    --profile localstack \
    --table-name customer \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --billing-mode PAY_PER_REQUEST

sam local invoke CreateCustomerFunction --profile localstack \
    --docker-network localstack_default \
    --event events/create-customer-function/ok.json \
    --env-vars events/env.json
```
## Testing with local api-gateway and curl

```bash
sam local start-api --profile localstack \
    --docker-network localstack_default \
	--env-vars events/env.json --debug

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
sam local start-lambda --profile localstack \
    --docker-network localstack_default
```


```bash
curl -s -v -X POST --user admin:passwd \
    -d "{"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}" \
    --output customer.json \
    --write-out %{http_code} \
    https://780fhbov3i.execute-api.eu-north-1.amazonaws.com/dev/customer/  
```


```Json
{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Sid": "cloudformation",
			"Effect": "Allow",
			"Action": [
				"cloudformation:CreateChangeSet",
				"cloudformation:DescribeChangeSet",
				"cloudformation:ExecuteChangeSet",
				"cloudformation:GetTemplate",
				"cloudformation:DeleteStack",
				"cloudformation:DescribeStackEvents",
				"cloudformation:GetTemplateSummary"
			],
			"Resource": [
				"*"
			]
		},
		{
			"Sid": "s3bucket",
			"Effect": "Allow",
			"Action": [
				"s3:CreateBucket",
				"s3:DeleteBucket",
				"s3:PutBucketTagging",
				"s3:PutEncryptionConfiguration",
				"s3:PutBucketVersioning",
				"s3:PutBucketPublicAccessBlock",
				"s3:PutBucketPolicy",
				"s3:DeleteBucketPolicy",
				"s3:PutObject",
				"s3:GetObject",
				"s3:DeleteObject",
				"sts:AssumeRole"
			],
			"Resource": [
				"*"
			]
		},
		{
			"Sid": "iamrole",
			"Effect": "Allow",
			"Action": [
				"iam:CreateRole",
				"iam:TagRole",
				"iam:AttachRolePolicy",
				"iam:DeleteRole",
				"iam:DetachRolePolicy",
				"iam:GetRole",
				"iam:PassRole",
				"iam:PutRolePolicy",
				"iam:GetRolePolicy",
				"iam:DeleteRolePolicy"
			],
			"Resource": [
				"*"
			]
		},
		{
			"Sid": "lambda",
			"Effect": "Allow",
			"Action": [
				"lambda:CreateFunction",
				"lambda:DeleteFunction",
				"lambda:TagResource",
				"lambda:GetFunction",
				"lambda:AddPermission",
				"lambda:RemovePermission",
				"lambda:UpdateFunctionConfiguration",
				"lambda:UpdateFunctionCode"
			],
			"Resource": [
				"*"
			]
		},
		{
			"Sid": "apigateway",
			"Effect": "Allow",
			"Action": [
				"apigateway:POST",
				"apigateway:PUT",
				"apigateway:DELETE",
				"apigateway:PATCH",
				"apigateway:GET"
			],
			"Resource": [
				"arn:aws:apigateway:*::/restapis/*",
				"arn:aws:apigateway:*::/restapis"
			]
		},
		{
			"Sid": "dynamodbtable",
			"Effect": "Allow",
			"Action": [
				"dynamodb:CreateTable",
				"dynamodb:DeleteTable"
			],
			"Resource": [
				"arn:aws:dynamodb:*:*:table/simple-crud-rest-dynamodb*"
			]
		}
	]
}
```

## Reference

- [GitHub - java-crud-lambda-example](https://github.com/aws-samples/java-crud-microservice-template)
- [Use API Gateway Lambda Authorizers](https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-use-lambda-authorizer.html)
- [SAM - Control API access with your AWS SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-controlling-access-to-apis.html)
- [SAM - AWS SAM Policy template list](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-policy-template-list.html)
- [Building Lambda with Java](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html)
- [Run local AWS SAM - Environment Variable File](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-using-start-api.html)