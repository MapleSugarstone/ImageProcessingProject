#!/bin/bash
# JSON object to pass to Lambda Function
json={"\"bucketname\"":\"tcss462-au2024-group8\"","\"filename\"":\"${1}\""}
# echo "Invoking Lambda function using API Gateway"
# time output=`curl -s -H "Content-Type: application/json" -X POST -d $json https://7p9kklgi4g.execute-api.us-east-1.amazonaws.com/process`
# echo ""
# echo ""
# echo "JSON RESULT:"
# echo $output | jq
# echo ""

echo "Invoking Lambda function using AWS CLI (Boto3)"
time output=`aws lambda invoke --invocation-type RequestResponse --cli-binary-format raw-in-base64-out --function-name Process-Image --region us-east-1 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""
