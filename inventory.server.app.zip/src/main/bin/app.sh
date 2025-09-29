#!/bin/bash

# expected working directory is the one containing the bin, conf and lib directories

# set JAVA_BIN here unless defined as an environment variable
if [ -z $JAVA_BIN ]; then
  JAVA_BIN="java"
fi
if [ -z $APP_TMP ]; then
  APP_TMP="temp"
fi

syntax () {
  echo "$0 [--port port][--debug-port debug_port]"
  exit 1
}

port=8080
debug_port=0
conf=conf

while [ $# -gt 0 ]; do
  case "$1" in
    --java)
      JAVA_BIN="$2"
      shift 2
      ;;
    --port)
      port="$2"
      shift 2
      ;;
    --debug-port)
      debug_port="$2"
      shift 2
      ;;
    --conf)
      conf="$2"
      shift 2
      ;;
    -h|--h|--help|--aide)
      syntax
      exit 1
      ;;
    *)
      if [ -n "$chemin" ]; then
        echo "Paramètre inconnu : $1"
        err=1
        shift
      else
        chemin="$1"
        shift
      fi
      ;;
  esac
done

JVM_DEBUG=
if [ "$debug_port" != "0" ]; then
  JVM_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$debug_port"
fi

JVM_ARGS="
-Xms64m -Xmx512m
-XX:-OmitStackTraceInFastThrow
-Djava.io.tmpdir=$APP_TMP
-Dscout.app.port=$port
$JVM_DEBUG
"
CLASSPATH="$conf/:lib/*"

mkdir -p logs
mkdir -p $APP_TMP

$JAVA_BIN $JVM_ARGS -classpath $CLASSPATH org.eclipse.scout.rt.app.Application >> logs/app.out 2>&1 &
echo $! > bin/app.pid
