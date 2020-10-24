#!/bin/sh

target=../target
dest=${target}/jpackage

rm -rf ${dest}

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home

jpackage=/Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home/bin/jpackage

${jpackage} \
  --add-modules java.base,java.logging,java.xml,java.sql,java.prefs,java.desktop,java.management,java.naming \
  --type pkg \
  --dest ${dest} \
  --verbose \
  -i ${target} \
  --name jackfruit \
  --main-jar jackfruit-0.0.1-SNAPSHOT.jar
