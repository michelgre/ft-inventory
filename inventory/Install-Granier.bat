SET TOMCAT=C:\Program Files\Apache Software Foundation\Tomcat 8.5
SET WEBAPPS=%TOMCAT%\webapps
SET CONF=%TOMCAT%\conf

SET ROOTDIR=%~dp0..\

XCOPY/Y %ROOTDIR%inventory.server.app.war\dist\inventory-server.war "%WEBAPPS%"
XCOPY/Y %ROOTDIR%inventory.ui.html.app.war\dist\inventory.war "%WEBAPPS%"
PAUSE
