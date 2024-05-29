
@REM CONPILATION _______

@echo off 
@REM VARIABLE _______
set "frame_work=sprint"
set "test=test"
set "temp_src=temp_src"
set "bin=bin"
set "lib=lib"
set "package=mg\itu"
set "src=src\%package%"
set "xml=web.xml"

set "main=test"
set "assets=%main%\assets"
set "web-inf=%main%\WEB-INF"
set "classes=%web-inf%\classes"

@REM config ENVIRONEMENT _______
call config/build_test.bat
mkdir %frame_work%\%temp_src%
mkdir %frame_work%\%bin%

@REM COMPILATION _______
echo CONFIGURATION en cours
    rem copy *.java --to temp_src
    for /r "%frame_work%\%src%" %%i in (*.java) do (
        copy "%%i" "%frame_work%\%temp_src%"
    )
    rem _______ compilaton _______
    cd %frame_work%
    if not exist %lib% mkdir %lib%
    javac -cp lib/* -d bin temp_src/*.java
echo CONPILATION terminer

@REM CONVERTIR JAR _______
if exist "%lib%\%frame_work%.jar" del "%lib%\%frame_work%.jar"
jar cvf "%lib%/%frame_work%.jar" -C %bin% .
cd ..

@REM COPY *.JAR
xcopy "%frame_work%\%lib%\*" "%web-inf%\%lib%\" /s /i /y

@REM COPY *.CLASS
xcopy "%frame_work%\%bin%" "%classes%\" /s /i /y
setlocal
for /d %%D in ("%classes%\%package%\*") do (
    for %%N in ("%%~nxD") do (  REM Extraire le nom du dossier
        if not "%%~N" == "Controllers" (
            rd /s /q "%%D"
            echo Dossier "%%D" ont eters supprimee ...
        )
    )
)
endlocal


@REM COPY web.xml _______
if exist %web-inf%/%xml% rmdir /s /q %web-inf%/%xml%
copy "%frame_work%/src" %web-inf% 

@REM NETTOYAGE _______
rmdir /s /q %frame_work%\%temp_src%
rmdir /s /q %frame_work%\%bin%

@REM TESTER MAINTENANT
call %frame_work%/config/test.bat

pause