rem @echo off

rem The following files must be in this directory:
rem your JDBC driver
rem commons-collections.jar
rem commons-dbcp.jar
rem commons-pool.jar
rem log4j.jar
rem quartz.jar
rem jdbc.jar
rem GLUE-STD.jar
rem servlet.jar
rem log4j.properties
rem quartz.properties
rem and of course, osworkflow.jar

rem !!!!!!! Please read important information. !!!!!!
rem If "java" is not in your path, please set the path
rem for Java 2 Runtime Environment in the path variable below
rem for example :
rem   SET PATH=D:\jdk1.3.1;%PATH%
rem

SET QRTZ_CP=commons-collections.jar;commons-dbcp.jar;commons-pool.jar;log4j.jar;quartz.jar;jdbc.jar;osworkflow.jar;GLUE-STD.jar;servlet.jar;.

rem Put the path to your JDBC driver(s) in this variable
rem for example :
rem   SET JDBC_CP=d:\repository\java\lib\oracle-1.2.jar
SET JDBC_CP=pgjdbc2.jar

rem change this to the URL you've bound GLUE to
SET SOAP_URL=http://localhost/example/glue/oswf.wsdl

"java" "-Dosworkflow.soap.url=%SOAP_URL%" -cp "%QRTZ_CP%;%JDBC_CP%" com.opensymphony.workflow.timer.QuartzRunner
