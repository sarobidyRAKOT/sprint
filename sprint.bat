
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

@REM config ENVIRONEMENT _______
@REM call config/build_test.bat
mkdir %temp_src%
mkdir %bin%

@REM COMPILATION _______
echo COMPILATION en cours
    rem copy *.java --to temp_src
    for /r "%src%" %%i in (*.java) do (
        copy "%%i" "%temp_src%"
    )
    rem _______ compilaton _______
    javac -cp ../lib/* -d bin temp_src/*.java 
echo CONPILATION terminer

@REM @REM CONVERTIR JAR _______
if exist "..\%frame_work%.jar" del "..\%frame_work%.jar"
jar cvf "../%frame_work%.jar" -C %bin% .

@REM NETTOYAGE _______
rmdir /s /q %temp_src%
rmdir /s /q %bin%


pause