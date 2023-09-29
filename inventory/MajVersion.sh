#!/bin/sh
#
# Mise à jour de la version dans tous les modules
version="$1"

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )


for mod in . inventory ; do
  dir="$SCRIPT_DIR/../$mod"
  echo $dir
  cd "$dir"
  mvn versions:set -DnewVersion="$version"
done

for mod in inventory.ui.html.app.dev inventory.server.app.dev; do
  dir="$SCRIPT_DIR/../$mod"
  echo $dir
  cd "$dir"
  mvn versions:update-parent -DskipResolution=true -DparentVersion="$version"
done
