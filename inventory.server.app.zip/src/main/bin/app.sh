#!/bin/bash

# expected working directory is the one containing the bin, conf and lib directories

# set JAVA_BIN here unless defined as an environment variable
if [ -z $JAVA_BIN ]; then
  JAVA_BIN="java"
fi
if [ -z $APP_TMP ]; then
  APP_TMP="temp"
fi

JVM_ARGS="
-Xms64m -Xmx512m
-XX:-OmitStackTraceInFastThrow
-Djava.io.tmpdir=$APP_TMP
-Dscout.app.port=8080
"
CLASSPATH="conf/:lib/*"

mkdir -p logs
mkdir -p $APP_TMP

$JAVA_BIN $JVM_ARGS -classpath $CLASSPATH org.eclipse.scout.rt.app.Application >> logs/app.out 2>&1 &
echo $! > bin/app.pid
