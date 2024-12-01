#!/bin/bash

function run_image() {
	for ((i = 0; i < ${3}; i++))
	do
		/usr/bin/time -f '%e' -o "times/${1}-${2}-times" -a ./callfunction.sh ${1} ${2}
	done
	
}

run_image ${1} "small" ${2}
run_image ${1} "medium" ${2}
run_image ${1} "large" ${2}

echo "Done with ${1}"
