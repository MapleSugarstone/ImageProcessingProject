#!/bin/bash

Mean=$(awk '{ total += $1; count++ } END { print total/count }' ${1})
SD=$(awk '{ total += $1; count++; array[NR] = $1 } END {mean = total / count; for(i=1; i<=count; i++){sumsq += (array[i] - mean)^2;}variance = sumsq / count;print sqrt(variance);}' ${1})
Min=$(sort -n ${1} | head -1)
Max=$(sort -n ${1} | tail -1)

echo "Mean: ${Mean}"
echo "Min: ${Min}"
echo "Max: ${Max}"
echo "Range: $((Max - Min))"
echo "Standard Deviation: ${SD}"
