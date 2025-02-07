## Features covered in the Repo

## Development Tool Setup
- install aws cli
- install sam cli
- install docker
- enable IAM identity center in AWS
- create IAM user account, grant the permissionSet and inline policy
- aws cli sso login
- install JDK
- install Maven

## Required AWS Inline Policy to deploy SAM Helloworld example to AWS cloud (+predefined ViewOnlyAccess PermissionSet)
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
				"cloudformation:DescribeStackEvents"
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
				"iam:PassRole"
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
				"lambda:RemovePermission"
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
		}
	]
}
```

## Create the SAM helloworld 
- sam init
- Helloworld template, runtime: java21, maven

## deploy helloworld and test with the API Gateway URL
- sam deploy --guided
- allow the helloworld example without authorization

## Clear up
- sam delete
- empty the s3 bucket
- delete the sam managed stack

## Reference