@set PATH=%SYSTEMROOT%\System32\;%PATH%
@set INSTALL_PATH=$INSTALL_PATH
@set JAVA_HOME=$JAVA_HOME
@set CAFILE=startssl.pem
@cd /D %INSTALL_PATH%
@if exist .isInstalled goto end

@if exist offline goto end
@echo Downloading bcprov-jdk15on-149.jar
@java -jar bin\sha1test.jar bcprov-jdk15on-149.jar . %CAFILE% > NUL
:end
