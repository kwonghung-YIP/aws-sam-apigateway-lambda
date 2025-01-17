## Features cover in this repo

## Set up LocalStack with docker-compose
```bash
cd localstack
docker compose up
```

## Connect LocalStack with AWS CLI
```bash
cd localstack
docker run --name awscli --rm -it \
    --network localstack_default \
    --link localstack-main \
    --entrypoint /bin/bash \
    -v "./.aws:/root/.aws" \
    -v "./cloudformation:/root/cloudformation" \
    -v "/home/hung/projects/aws-lambda-spike/plain-java-helloworld/target:/root/target" \
    -w "/root" \
    -e AWS_PROFILE=localstack \
    amazon/aws-cli:2.22.12
```

### create lambda function from local zip file (option 1)
```bash
aws lambda create-function \
    --function-name my-lambda-function \
    --runtime java21 \
    --handler org.hung.aws.MyLambdaHandler \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --zip-file fileb:///root/target/plain-java-helloworld-1.0-SNAPSHOT.jar

# invoke the function for testing
aws lambda invoke \
    --cli-binary-format raw-in-base64-out \
    --function-name simple-helloworld \
    --payload "{"name":"John"}" output.txt
```

### create lambda function from s3 bucket (option 2)
```bash
aws s3 mb s3://lambda-artifacts

aws s3 cp \
    ./target/plain-java-helloworld-1.0-SNAPSHOT.jar \
    s3://lambda-artifacts/plain-java-helloworld-1.0-SNAPSHOT.jar

aws s3 ls s3://lambda-artifacts

# to list the object version in s3 bucket
aws s3api list-object-versions --bucket lambda-artifacts

aws lambda create-function \
    --function-name simple-helloworld \
    --runtime java21 \
    --handler org.hung.aws.MyLambdaHandler \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --code S3Bucket=lambda-artifacts,S3Key=plain-java-helloworld-1.0-SNAPSHOT.jar

# invoke the function for testing
aws lambda invoke \
    --cli-binary-format raw-in-base64-out \
    --function-name simple-helloworld \
    --payload "{"name":"John"}" output.txt

aws lambda list-functions

aws lambda get-function \
    --function-name simple-helloworld
```
    
# create lambda function with AWS ASM (cloudformation extension) (option 3)
```bash
aws s3 mb s3://lambda-artifacts

aws cloudformation package \
    --template-file cloudformation/template.yml \
    --s3-bucket lambda-artifacts \
    --output-template-file cloudformation/out.yml

aws cloudformation deploy \
    --template-file cloudformation/out.yml \
    --stack-name my-lambda-stack \
    --capabilities CAPABILITY_NAMED_IAM

aws cloudformation describe-stacks \
    --debug --stack-name my-lambda-stack

aws cloudformation delete-stack \
    --stack-name my-lambda-stack

# check the function name with the stack
aws lambda list-functions

# invoke the function for testing
aws lambda invoke \
    --cli-binary-format raw-in-base64-out \
    --function-name my-lambda-stack-function-3f245d71 \
    --payload "{"name":"John"}" output.txt

aws lambda get-function \
    --function-name simple-helloworld

```

## Install AWS CLI and AWS SAM CLI
- [AWS CLI install and update instructions - Linux](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- [AWS CLI - Configuration and credential settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html)
- [Installing AWS SAM CLI - Linux](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)
```bash
sudo snap install aws-cli --classic
aws --version

wget https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
unzip aws-sam-cli-linux-x86_64.zip -d sam-installation
sudo ./sam-installation/install
sam --version
```

## Reference
- [Baeldung A Basic AWS Lambda Example With Java](https://www.baeldung.com/java-aws-lambda)
- [Building Lambda functions with Java](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html)
- [AWS CLI 2.x s3 command reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3/index.html)
- [AWS CLI 2.x s3api command reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3api/index.html)
- [Youtube - Using the AWS Serverless Application Model (AWS SAM)...](https://www.youtube.com/watch?v=xQHgRgKquqQ)
- [AWS Serverless Application Model](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html)
