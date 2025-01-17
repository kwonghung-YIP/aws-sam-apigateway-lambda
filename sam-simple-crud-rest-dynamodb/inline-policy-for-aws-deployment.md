## Required permissions for AWS deployment

ViewOnlyAccess (AWS managed policy) + the inline policy below are granted to the account that used for local AWS CLI login
```json
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
        },
        {
            "Sid": "logs",
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogGroup",
                "logs:DeleteLogGroup",
                "logs:PutRetentionPolicy"
            ],
            "Resource": [
                "arn:aws:logs:eu-north-1:796973491384:log-group:/aws/lambda/*"
            ]
        }
    ]
}
```