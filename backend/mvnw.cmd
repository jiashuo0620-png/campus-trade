@echo off
setlocal

rem Find java.exe
set "JAVA_EXE="
if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    for %%i in (java.exe) do set "JAVA_EXE=%%~$PATH:i"
)

if not exist "%JAVA_EXE%" (
    echo ERROR: Java not found. Please set JAVA_HOME.
    exit /b 1
)

set "MAVEN_PROJECT_DIR=%~dp0"
set "WRAPPER_JAR=%MAVEN_PROJECT_DIR%.mvn\wrapper\maven-wrapper.jar"

if not exist "%WRAPPER_JAR%" (
    echo ERROR: maven-wrapper.jar not found at %WRAPPER_JAR%
    exit /b 1
)

"%JAVA_EXE%" ^
  -classpath "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECT_DIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  %*
