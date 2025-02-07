#!/bin/bash

INSTANCE_ARN="arn:aws:sso:::instance/ssoins-6508049c7c8773e1"

POLICY_NAME="cloudformation-deployment2"

AWS_ACCOUNT_ID="796973491384"

SSO_USER_PRINCIPAL_ID="a07c599c-c041-700d-0209-f965861b8678"

POLICY_ARN_1=`aws iam create-policy \
    --policy-name "${POLICY_NAME}" \
    --description "IAM Policy for deploying pipeline stack" \
    --policy-document file://cloudformation-deployment-policy.json \
    --output text --query "Policy.Arn" \
    --no-cli-pager`

PERMISSION_SET_ARN=`aws sso-admin create-permission-set \
    --name awscli-cloudformation-deploy2 \
    --description "Permission Set for deploying cloudformation stack in AWS CLI" \
    --instance-arn "${INSTANCE_ARN}" \
    --output text --query "PermissionSet.PermissionSetArn" \
    --no-cli-pager`

aws sso-admin attach-customer-managed-policy-reference-to-permission-set \
    --customer-managed-policy-reference "Name=${POLICY_NAME}" \
    --permission-set-arn "${PERMISSION_SET_ARN}" \
    --instance-arn "${INSTANCE_ARN}"

aws sso-admin describe-permission-set \
    --permission-set-arn "${PERMISSION_SET_ARN}" \
    --instance-arn "${INSTANCE_ARN}" \
    --no-cli-pager

REQUEST_ID=`aws sso-admin create-account-assignment \
    --instance-arn "${INSTANCE_ARN}" \
    --permission-set-arn "${PERMISSION_SET_ARN}" \
    --principal-id "${SSO_USER_PRINCIPAL_ID}" \
    --principal-type "USER" \
    --target-id "${AWS_ACCOUNT_ID}" \
    --target-type "AWS_ACCOUNT" \
    --output text --query "AccountAssignmentCreationStatus.RequestId" \
    --no-cli-pager`

#aws sso-admin list-account-assignment-creation-status \
#    --instance-arn "${INSTANCE_ARN}" \
#    --filter "Status=IN_PROGRESS" \
#    --no-cli-pager

STATUS="IN_PROGRESS"

while [ "${STATUS}" = "IN_PROGRESS" ]
do
    sleep 2s

    STATUS=`aws sso-admin describe-account-assignment-creation-status \
        --account-assignment-creation-request-id "${REQUEST_ID}" \
        --instance-arn "${INSTANCE_ARN}" \
        --output text --query="AccountAssignmentCreationStatus.Status" \
        --no-cli-pager`
done

aws sso-admin list-account-assignments-for-principal \
    --instance-arn "${INSTANCE_ARN}" \
    --principal-id "${SSO_USER_PRINCIPAL_ID}" \
    --principal-type "USER" \
    --no-cli-pager

ROLE_NAME="pipeline-stack-cloudformation-execution-role2"
POLICY_NAME="pipeline-stack-cloudformation-execution-policy2"

POLICY_ARN_2=`aws iam create-policy \
    --policy-name "${POLICY_NAME}" \
    --description "IAM Policy for running Pipeline Stack changeset" \
    --policy-document file://pipeline-stack-cloudformation-execution-policy.json \
    --output text --query "Policy.Arn" \
    --no-cli-pager`

ROLE_ARN=`aws iam create-role \
    --role-name ${ROLE_NAME} \
    --description "IAM Role for running Pipeline Stack changeset" \
    --assume-role-policy-document file://pipeline-stack-cloudformation-execution-role.json \
    --output text --query "Role.Arn" \
    --no-cli-pager`

aws iam attach-role-policy \
    --role-name ${ROLE_NAME} \
    --policy-arn ${POLICY_ARN_2}

sed -e "s#%CLOUDFORMATION_DEPLOYMENT_POLICY_ARN%#${POLICY_ARN_1}#g" \
    -e "s#%CLOUDFORMATION_DEPLOYMENT_PERMISSION_SET_ARN%#${PERMISSION_SET_ARN}#g" \
    -e "s#%PIPELINE_STACK_CLOUDFORMATION_EXECUTION_POLICY_ARN%#${POLICY_ARN_2}#g" < role-setup-rollback.sh.tmpl > role-setup-rollback.sh