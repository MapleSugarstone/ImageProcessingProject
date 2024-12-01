#!/bin/bash

Mean=$(awk '{ total += $1; count++ } END { print total/count }' ${1})
SD=$(awk '{ total += $1; count++; array[NR] = $1 } END {mean = total / count; for(i=1; i<=count; i++){sumsq += (array[i] - mean)^2;}variance = sumsq / count;print sqrt(variance);}' ${1})
Min=$(sort -n ${1} | head -1)
Max=$(sort -n ${1} | tail -1)

YELLOW="\033[33m"
UNCOLORED="\033[0m"

if [ "${2}" = "-d" ]
then
	Mean=$(bc <<< "scale=3; $Mean / 1000.0" | awk '{printf "%.3f", $0}')
	SD=$(bc <<< "scale=3; $SD / 1000.0" | awk '{printf "%.3f", $0}')	
	Min=$(bc <<< "scale=3; $Min / 1000.0" | awk '{printf "%.3f", $0}')
	Max=$(bc <<< "scale=3; $Max / 1000.0" | awk '{printf "%.3f", $0}')
fi
echo -e "      ${YELLOW}Mean:${UNCOLORED} ${Mean}"
echo -e "      ${YELLOW}Min:${UNCOLORED} ${Min}"
echo -e "      ${YELLOW}Max:${UNCOLORED} ${Max}"
echo -e "      ${YELLOW}Range:${UNCOLORED} "$(bc <<< "$Max - $Min")
echo -e "      ${YELLOW}Standard Deviation:${UNCOLORED} ${SD}"
echo -e "      ${YELLOW}Coefficient of Variation:${UNCOLORED} "$(bc <<< "scale=5; $SD / $Mean * 100" | awk '{printf "%.3f", $0}')"%"
