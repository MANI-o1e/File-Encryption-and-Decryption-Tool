@echo off
echo Starting File Encryption and Decryption Tool...
java -cp bin Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo If bin is empty, please run compile.bat first.
    pause
)
