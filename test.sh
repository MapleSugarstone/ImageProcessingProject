#!/bin/bash

# Argument is amount of iterations

./timefunction.sh "Process-Image" ${1} &
./timefunction.sh "Python-Process-Image" ${1}

