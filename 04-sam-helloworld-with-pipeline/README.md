```bash
# IAM Identity Center instance arn
instance-arn="arn:aws:sso:::instance/ssoins-6508049c7c8773e1"

policy-name="cloudformation-deployment2"

aws iam create-policy \
    --policy-name "${policy-name}" \
    --description "IAM Policy for deploying pipeline stack" \
    --policy-document file://cloudformation-deployment-policy.json

aws sso-admin create-permission-set \
    --name awscli-cloudformation-deploy2 \
    --description "Permission Set for deploying cloudformation stack in AWS CLI" \
    --instance-arn "${instance-arn}" \
    --output text --query "PermissionSet.PermissionSetArn"

aws sso-admin attach-customer-managed-policy-reference-to-permission-set \
    --customer-managed-policy-reference "Name=${policy-name}" \
    --permission-set-arn "arn:aws:sso:::permissionSet/ssoins-6508049c7c8773e1/ps-1d4a6279e3e94378" \
    --instance-arn "${instance-arn}"

aws iam create-role \
    --role-name cloudformation-deployment2 \
    --description "IAM Role for deploying pipeline stack" \
    --assume-role-policy-document file://cloudformation-deployment-role-assume-doc.json \
    --output json
```

```bash
aws sso-admin list-account-assignment-creation-status \
    --instance-arn "arn:aws:sso:::instance/ssoins-6508049c7c8773e1" \
    --filter "Status=IN_PROGRESS"

aws sso-admin describe-account-assignment-creation-status \
    --account-assignment-creation-request-id "5d138c3a-ba88-490b-9032-eb28d06508fd" \
    --instance-arn "arn:aws:sso:::instance/ssoins-6508049c7c8773e1"

aws sso-admin list-account-assignment-deletion-status \
    --instance-arn "arn:aws:sso:::instance/ssoins-6508049c7c8773e1" \
    --filter "Status=IN_PROGRESS"

aws sso-admin describe-account-assignment-deletion-status \
    --account-assignment-creation-request-id "5d138c3a-ba88-490b-9032-eb28d06508fd" \
    --instance-arn "arn:aws:sso:::instance/ssoins-6508049c7c8773e1"

""
sed -e "s#%CLOUDFORMATION_DEPLOYMENT_POLICY_ARN%#arn:aws:sso:::permissionSet/ssoins-6508049c7c8773e1/ps-ad0128377cdcf3fd#g" role-setup-rollback.sh
```

Deploy/Delete the pipeline stack with aws cloudformation cli

```bash
aws cloudformation deploy \
    --profile cloudformation-deployment \
    --template-file codepipeline-stack.yaml \
    --stack-name sam-helloworld-pipeline \
    --role-arn arn:aws:iam::796973491384:role/pipeline-stack-cloudformation-execution-role

aws cloudformation delete-stack \
    --profile cloudformation-deployment \
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