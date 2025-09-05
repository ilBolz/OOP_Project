@echo off
echo ===============================================
echo    PERSONAL FINANCE MANAGER - CLI LAUNCHER
echo ===============================================
echo.

echo Compilazione progetto...
call mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo ERRORE: Compilazione fallita!
    pause
    exit /b 1
)
echo Compilazione completata!

echo Copia dipendenze...
call mvn dependency:copy-dependencies -q
if %ERRORLEVEL% neq 0 (
    echo ERRORE: Copia dipendenze fallita!
    pause
    exit /b 1
)
echo Dipendenze copiate!

echo.
echo Avvio interfaccia CLI...
echo Premi Ctrl+C per uscire
echo.

java -cp "target/classes;target/dependency/*" org.finance.CLIMain

echo.
echo CLI terminata.
pause
