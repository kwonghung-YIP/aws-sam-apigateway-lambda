Deploy/Delete the pipeline stack with aws cloudformation cli

```bash
aws cloudformation deploy \
    --profile cloudformation-execution-role \
    --template-file first-pipeline.yaml \
    --stack-name github-lambda-pipeline

aws cloudformation delete-stack \
    --profile cloudformation-execution-role \
    --stack-name github-lambda-pipeline
```

Deploy/Delete the pipeline stack with sam cli

```bash
sam deploy -t first-pipeline.yaml \
    --profile cloudformation-deployment \
    --stack-name sam-helloworld-pipeline \
    --role-arn arn:aws:iam::796973491384:role/pipeline-stack-cloudformation-execution-role \
    --capabilities=CAPABILITY_IAM

sam delete \
    --profile cloudformation-deployment \
    --stack-name sam-helloworld-pipeline
```

References
- [AWS CloudFormation Template Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-reference.html)
- [What is AWS CodePipeline](https://docs.aws.amazon.com/codepipeline/latest/userguide/welcome.html)
- [What is AWS CodeBuild](https://docs.aws.amazon.com/codebuild/latest/userguide/welcome.html)