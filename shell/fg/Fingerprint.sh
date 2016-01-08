#!/bin/bash

FILE=$1
while read line; do

curl -X POST -d "{$line}" http://10.0.3.80:8383/v1/match/fingerprint/getFPJson -w Time:%{time_total} --max-time 900

printf ",\n"
done < $FILE


