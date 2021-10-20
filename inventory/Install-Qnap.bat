SET TOMCAT=\\QNAP251\home\Tomcat8\tomcat8
SET WEBAPPS=%TOMCAT%\webapps

SET ROOTDIR=%~dp0..\

XCOPY/Y %ROOTDIR%inventory.server.app.war\dist\inventory-server.war "%WEBAPPS%"
XCOPY/Y %ROOTDIR%inventory.ui.html.app.war\dist\inventory.war "%WEBAPPS%"
PAUSE
