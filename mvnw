#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# This script is based on the mvn script from Apache Maven
# It allows running Maven without global installation

if [ -z "$JAVA_HOME" ] ; then
  JAVA_CMD=java
else
  if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    exit 1
  fi
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done

MAVEN_HOME=`dirname "$PRG"`
MAVEN_HOME=`dirname "$MAVEN_HOME"`

exec "$JAVA_CMD" \
  -classpath "$MAVEN_HOME/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_HOME" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
