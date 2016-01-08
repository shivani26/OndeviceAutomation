#!/bin/bash
FILE=$1
while read line; do

curl -X POST -d '['"$line"']' http://54.251.164.80:8080/music/v1/metamatch?u=G%2Fu2F8KyLzoT0K4Jym6pXzjjWXpHK3pcidUx7cgJpV8%3D
printf ",\n"
done < $FILE
