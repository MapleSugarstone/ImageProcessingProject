#!/bin/bash

# Argument is amount of iterations

# ./timefunction.sh "Process-Image" ${1} # &
# ./timefunction.sh "Python-Process-Image" ${1}

RED="\033[31m"
GREEN="\033[32m"
YELLOW="\033[33m"
BLUE="\033[34m"
UNCOLORED="\033[0m"

echo -e "${RED}Process-Image (Java) stats:${UNCOLORED}"
echo -e "${GREEN}  SMALL${UNCOLORED}"
echo -e "${BLUE}    Runtime${UNCOLORED}"
./calculations.sh times/Process-Image-small-runtimes -d
echo -e "${BLUE}    Client Time${UNCOLORED}"
./calculations.sh times/Process-Image-small-times
echo -e "${GREEN}  MEDIUM${UNCOLORED}"
echo -e "${BLUE}    Runtime${UNCOLORED}"
./calculations.sh times/Process-Image-medium-runtimes -d
echo -e "${BLUE}    Client Time${UNCOLORED}"
./calculations.sh times/Process-Image-medium-times
echo -e "${GREEN}  LARGE${UNCOLORED}"
echo -e "${BLUE}    Runtime${UNCOLORED}"
./calculations.sh times/Process-Image-large-runtimes -d
echo -e "${BLUE}    Client Time${UNCOLORED}"
./calculations.sh times/Process-Image-large-times
