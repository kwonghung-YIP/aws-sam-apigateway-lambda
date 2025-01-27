```bash
aws iam create-role \
    --role-name cloudformation-deployment2 \
    --description "IAM Role for deploying pipeline stack" \
    --assume-role-policy-document file://cloudformation-deployment-role-assume-doc.json \
    --output json

aws iam create-policy \
    --policy-name cloudformation-deployment2 \
    --description "IAM Policy for deploying pipeline stack" \
    --policy-document file://cloudformation-deployment-policy.json \
    --output text --query "Policy.Arn"
```

Deploy/Delete the pipeline stack with aws cloudformation cli

```bash
aws cloudformation deploy \
    --profile cloudformation-execution-role \
    --template-file codepipeline-stack.yaml \
    --stack-name sam-helloworld-pipeline \
    --role-arn arn:aws:iam::796973491384:role/pipeline-stack-cloudformation-execution-role

aws cloudformation delete-stack \
    --profile cloudformation-execution-role \
    --stack-name sam-helloworld-pipeline
```

Deploy/Delete the pipeline stack with sam cli

```bash
sam deploy -t codepipeline-stack.yaml \
    --profile cloudformation-deployment \
    --stack-name sam-helloworld-pipeline \
    --role-arn arn:aws:iam::796973491384:role/pipeline-stack-cloudformation-execution-role \
    --capabilities=CAPABILITY_IAM --debug

sam delete \
    --profile cloudformation-deployment \
    --stack-name sam-helloworld-app \
    --no-prompts

sam delete \
    --profile cloudformation-deployment \
    --stack-name aws-sam-cli-managed-default \
    --no-prompts

sam delete \
    --profile cloudformation-deployment \
    --stack-name sam-helloworld-pipeline \
    --no-prompts
```

References
- [AWS CloudFormation Template Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-reference.html)
- [What is AWS CodePipeline](https://docs.aws.amazon.com/codepipeline/latest/userguide/welcome.html)
- [What is AWS CodeBuild](https://docs.aws.amazon.com/codebuild/latest/userguide/welcome.html)