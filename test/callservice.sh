#!/bin/bash
# JSON object to pass to Lambda Function
json={"\"bucketname\"":\"tcss462-au2024-group8\"","\"filename\"":\"image-small.png\""}
echo "Invoking Lambda function using API Gateway"
time output=`curl -s -H "Content-Type: application/json" -X POST -d $json https://7p9kklgi4g.execute-api.us-east-1.amazonaws.com/process`
echo ""
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""
