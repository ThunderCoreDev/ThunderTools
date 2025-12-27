@echo off
set DIR=%~dp0
set APP_HOME=%DIR%

if defined JAVA_HOME (
  set JAVA_EXEC=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXEC=java.exe
)

"%JAVA_EXEC%" -Xmx64m -Xms64m -cp "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*