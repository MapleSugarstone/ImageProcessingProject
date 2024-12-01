#!/bin/bash

output=`aws lambda invoke --invocation-type RequestResponse --cli-binary-format raw-in-base64-out --function-name ${1} --region us-east-1 --payload "{"\"bucketname\"":\"tcss462-au2024-group8\"","\"filename\"":\"image-${2}.png\""}" /dev/stdout | head -n 1 | head -c -2 ; echo` 
echo $output | jq '.runtime' >> "times/${1}-${2}-runtimes"
