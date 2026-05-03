@echo off
SETLOCAL

IF "%~1"=="" (
    echo Usage: start.bat ^<PORT^>
    exit /b 1
)

SET APP_PORT=%1

echo Starting Stock Market API on localhost:%APP_PORT%

docker compose up -d --build --scale stockmarket-core=5

echo Waiting for Application instances to be fully ready...


:check_ready
REM
REM
FOR /F "tokens=*" %%i IN ('curl -s -L -o nul -w "%%{http_code}" http://localhost:%APP_PORT%/swagger-ui.html') DO SET HTTP_STATUS=%%i

REM
IF "%HTTP_STATUS%"=="" SET HTTP_STATUS=000

REM
IF "%HTTP_STATUS%"=="200" (
    goto :app_ready
) ELSE (
    REM
    <nul set /p =. 
    REM
    timeout /t 3 /nobreak > nul
    goto :check_ready
)

:app_ready
echo.
echo Application is READY and accepting traffic! 
echo Swagger UI: http://localhost:%APP_PORT%/swagger-ui.html
ENDLOCAL