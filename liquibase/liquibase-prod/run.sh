#!/bin/bash

echo "current path = $(pwd)"

#parent path
PARENT_PATH=$(dirname $(pwd))
echo "parent path = $PARENT_PATH"

#cd excute path
cd $PARENT_PATH
echo "excute path = $(pwd)"

#run mvn
mvn resources:resources liquibase:update -P prod

echo "excute successfully"


