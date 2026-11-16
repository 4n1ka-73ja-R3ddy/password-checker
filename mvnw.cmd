@REM ----------------------------------------------------------------------------
@REM Maven Wrapper for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set ERROR_CODE=0

@REM Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%"=="" goto OkJHome
for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
goto checkJCmd

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

:checkJCmd
if exist "%JAVACMD%" goto chkMvn
echo The JAVA_HOME environment variable is not defined correctly >&2
echo This environment variable is needed to run this program >&2
goto error

:chkMvn
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    mvn %*
    goto end
)

echo [INFO] Maven Wrapper executing...
set MAVEN_PROJECTBASEDIR=%~dp0
if exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    "%JAVACMD%" -jar "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" %*
) else (
    echo [ERROR] Maven not found on PATH and maven-wrapper.jar not found.
    echo Please install Apache Maven or place maven-wrapper.jar in .mvn/wrapper/
    exit /b 1
)

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%

