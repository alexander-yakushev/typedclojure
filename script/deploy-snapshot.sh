#!/bin/bash

set -e

TYPED_VERSION=$(mvn -q \
  -Dexec.executable="echo" \
  -Dexec.args='${project.version}' \
  --non-recursive \
  org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)

if [[ "$TYPED_VERSION" == *-SNAPSHOT ]]; then
  echo "Deploying snapshot"
  mvn deploy
else
  echo "Not deploying snapshot, version does not end with '-SNAPSHOT': ${TYPED_VERSION}"
  exit 1
fi
