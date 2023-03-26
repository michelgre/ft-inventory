@ECHO OFF
SET TOMCAT=\\QNAP2\home\Tomcat9
SET WEBAPPS=%TOMCAT%\webapps

SET ROOTDIR=%~dp0..\

REM Derniere Version
FOR /f "delims=" %%i in ('dir "%ROOTDIR%inventory.server.app.war\dist\*.war" /b /a-d /o-d') DO SET "SERVER_WAR=%%i" & GOTO :end1
:end1

FOR /f "delims=" %%i in ('dir "%ROOTDIR%inventory.ui.html.app.war\dist\*.war" /b /a-d /o-d') DO SET "CLIENT_WAR=%%i" & GOTO :end2
:end2

ECHO Serveur: %SERVER_WAR%
ECHO Client: %CLIENT_WAR%
@ECHO ON
XCOPY/Y "%ROOTDIR%inventory.server.app.war\dist\%SERVER_WAR%" "%WEBAPPS%\inventory-server.war"
XCOPY/Y "%ROOTDIR%inventory.ui.html.app.war\dist\%CLIENT_WAR%" "%WEBAPPS%\inventory.war"
PAUSE
