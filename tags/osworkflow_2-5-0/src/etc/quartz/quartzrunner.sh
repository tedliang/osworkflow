#!/bin/sh

# The following files must be in this directory:
# your JDBC driver
# commons-collections.jar
# commons-dbcp.jar
# commons-pool.jar
# log4j.jar
# quartz.jar
# jdbc.jar
# GLUE-STD.jar
# servlet.jar
# log4j.properties
# quartz.properties
# and of course, osworkflow.jar

# Change this to your JDK installation root
#
#JAVA_HOME=/usr/local/jdk1.3

JRE=$JAVA_HOME/jre
JAVA=$JRE/bin/java

QRTZ_CP=commons-collections.jar:commons-dbcp.jar:commons-pool.jar:log4j.jar:quartz.jar:jdbc.jar:osworkflow.jar:GLUE-STD.jar:servlet.jar:.
JDBC_CP=classes12.zip

# change this to the URL you've bound GLUE to
SOAP_URL=http://localhost/example/glue/oswf.wsdl

$JAVA "-Dosworkflow.soap.url=$SOAP_URL" -classpath "$QRTZ_CP:JDBC_CP" com.opensymphony.workflow.timer.QuartzRunner
