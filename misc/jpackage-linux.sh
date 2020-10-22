#!/bin/sh

target=../target

dest=${target}/jpackage

rm -rf ${dest}

export JAVA_HOME=/usr/lib/jvm/java-14
jpackage=${JAVA_HOME}/bin/jpackage

${jpackage} \
  --add-modules java.base,java.logging,java.xml,java.sql,java.prefs,java.desktop,java.management,java.naming \
  --dest ${dest} \
  --verbose \
  -i ${target} \
  --name jackfruit \
  --main-jar jackfruit-0.0.1-SNAPSHOT.jar
