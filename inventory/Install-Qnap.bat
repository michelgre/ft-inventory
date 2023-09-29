@ECHO OFF
SET TOMCAT=\\QNAP2\Tomcat
SET WEBAPPS=%TOMCAT%\webapps
SET APP=inventory

SET ROOTDIR=%~dp0..\

IF "%1" == "-app" (
  SET APP=%2
  SHIFT
  SHIFT
)
ECHO APP=%APP%
PAUSE

REM Derniere Version
FOR /f "delims=" %%i in ('dir "%ROOTDIR%inventory.server.app.war\dist\*.war" /b /a-d /o-d') DO SET "SERVER_WAR=%%i" & GOTO :end1
:end1

FOR /f "delims=" %%i in ('dir "%ROOTDIR%inventory.ui.html.app.war\dist\*.war" /b /a-d /o-d') DO SET "CLIENT_WAR=%%i" & GOTO :end2
:end2

ECHO Serveur: %SERVER_WAR%
ECHO Client: %CLIENT_WAR%
@ECHO ON
XCOPY/Y /-I "%ROOTDIR%inventory.server.app.war\dist\%SERVER_WAR%" "%WEBAPPS%\%APP%-server.war"
XCOPY/Y /-I "%ROOTDIR%inventory.ui.html.app.war\dist\%CLIENT_WAR%" "%WEBAPPS%\%APP%.war"
PAUSE
