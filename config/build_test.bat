

@REM _______ BUILD DOSSIER TEST _______

@echo off

rem variable ...
REM variable miasa atu ef ao am sprint.bat daoly

cd ..

echo CONFIGURATION DOSSIER TEST _______
rem nettoyage ...
if exist %main% rmdir /s /q %main%
rem reconstruction ...
mkdir %main%
mkdir %assets%
mkdir %web-inf%
mkdir %classes%
mkdir %web-inf%\%lib%