
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
set "lib_test=test\lib"

@REM config ENVIRONEMENT _______
mkdir %temp_src%
mkdir %bin%

@REM COMPILATION _______
echo COMPILATION en cours
    rem copy *.java --to temp_src
    for /r "%src%" %%i in (*.java) do (
        copy "%%i" "%temp_src%"
    )
    rem _______ compilaton _______
    javac -d bin -cp .;lib/* temp_src/*.java
    @REM javac -d bin -cp lib/paranamer-2.8.jar;lib/servlet-api.jar temp_src/*.java
    @REM javac -g -d %BIN_DIR% -cp "lib/*" %SRC_DIR%\**\*.java
echo CONPILATION terminer

@REM @REM CONVERTIR JAR _______
if exist "..\%frame_work%.jar" del "..\%frame_work%.jar"
jar cvf "../%frame_work%.jar" -C %bin% .

@REM NETTOYAGE _______
rmdir /s /q %temp_src%
rmdir /s /q %bin%


@rem COPY lib framework _______
cd ..
dir
xcopy "%frame_work%\%lib%\*" "%lib_test%\" /s /i /y
move sprint.jar "%lib_test%\"

