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
    --stack-name sam-helloworld-pipeline
```

References
- [AWS CloudFormation Template Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-reference.html)
- [What is AWS CodePipeline](https://docs.aws.amazon.com/codepipeline/latest/userguide/welcome.html)
- [What is AWS CodeBuild](https://docs.aws.amazon.com/codebuild/latest/userguide/welcome.html)