

@REM ______ TESTER DANS TOMCAT _______

@echo off

@rem TOMCAT conf _______
set "TOMCAT_PATH=C:\Program Files\Apache Software Foundation\Tomcat 10.1"
set "CATALINA_HOME=%TOMCAT_PATH%"
set "PORT=8080"


@REM DEPLOIEMENT _______
netstat -ano | findstr /c:":%PORT%" > nul
if errorlevel 1 (
    call "%TOMCAT_PATH%\bin\startup.bat"
    timeout /t 5 /nobreak
)
@REM @REM rem METTOYAGE webapps [tomcat]
if exist "%TOMCAT_PATH%\webapps\%frame_work%.war" del "%TOMCAT_PATH%\webapps\%frame_work%.war"
if exist "%TOMCAT_PATH%\webapps\%frame_work%" rmdir /S /Q "%TOMCAT_PATH%\webapps\%frame_work%"

cd test

jar -cvf "%TOMCAT_PATH%\webapps\%frame_work%.war" *

echo url web SITE: 
echo http://localhost:8080/%frame_work%
