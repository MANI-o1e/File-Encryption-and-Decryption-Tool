@echo off
echo ========================================================
echo Compiling File Encryption and Decryption Tool...
echo ========================================================

if not exist bin mkdir bin

javac -d bin -sourcepath ".;src" src\filecrypt\exception\*.java src\filecrypt\algo\*.java src\filecrypt\util\*.java src\filecrypt\model\*.java src\filecrypt\db\*.java src\filecrypt\ui\*.java src\filecrypt\App.java Main.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation finished successfully.
    echo Compiled class files are located in 'bin\' directory.
) else (
    echo [ERROR] Compilation failed. Please ensure JDK is installed and in PATH.
)
pause
