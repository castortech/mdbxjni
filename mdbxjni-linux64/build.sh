#!/bin/bash

JAVA_HOME=/usr/lib/jvm/jre-11-openjdk
export JAVA_HOME
 
MDBX_HOME=/home/ec2-user/libmdbx
export MDBX_HOME

mvn clean compile package -Dmaven.test.skip=true -P linux64
 