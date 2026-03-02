@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM Maven Wrapper Script for Windows
@REM
@REM Optional ENV vars
@REM   M2_HOME - location of maven2's installed home dir
@REM   MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM   MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM   MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM
@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable delayed expansion
setlocal enabledelayedexpansion

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" (
    "%USERPROFILE%\mavenrc_pre.bat" %*
    if errorlevel 1 goto error
)
if exist "%USERPROFILE%\mavenrc_pre.cmd" (
    "%USERPROFILE%\mavenrc_pre.cmd" %*
    if errorlevel 1 goto error
)
:skipRcPre

@REM To isolate internal variables from possible post scripts, we use another setlocal
setlocal

set ERROR_CODE=0

@REM Get command-line arguments, handling Windows variants
if not "%OS%" == "Windows_NT" goto win9xME_args

@REM Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:execute
@REM Setup the command line

set CLASSPATH=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar

@REM find JAVA_HOME since Java and Maven may run on different version of Java
if not "%JAVA_HOME%" == "" goto foundJavaHome

for /f "output of 'where java.exe'" %%z in ('where java.exe') do (
  set JAVA_HOME=%%z
  goto foundJavaHome
)

if "%JAVA_HOME%"=="" (
    echo.
    echo Error: JAVA_HOME not found in your environment. >&2
    echo Please set the JAVA_HOME variable in your environment to match the
    echo location of your Java installation. >&2
    goto error
)

:foundJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
goto error

@REM Initialize environment
:init
@REM Find the project base directory, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
if "%MAVEN_PROJECTBASEDIR%"=="" goto findBaseDir

pushd "%MAVEN_PROJECTBASEDIR%"
goto endFindBaseDir

:findBaseDir
cd /d %~dp0
pushd .
for /d %%%%A in (.) do set MAVEN_PROJECTBASEDIR=%%%%~nxA

:endFindBaseDir
if "%MAVEN_PROJECTBASEDIR%"=="" (
    popd
    echo.
    echo Error: Could not find the Maven project base directory. >&2
    goto error
)

set WRAPPERDIR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
set WRAPPER_JAR="%WRAPPERDIR%\maven-wrapper.jar"

if not exist "%WRAPPERDIR%" (
    echo Downloading maven-wrapper.jar ...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar', '%WRAPPER_JAR%')"
    if errorlevel 1 (
        echo Error downloading maven-wrapper.jar
        goto error
    )
)

set MAVEN_OPTS=%MAVEN_OPTS% "-Dmaven.home=%M2_HOME%"
set MAVEN_OPTS=%MAVEN_OPTS% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%"

@REM Execute Maven
"%JAVA_HOME%\bin\java" ^
  %MAVEN_OPTS% ^
  -classpath %CLASSPATH% ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  %CMD_LINE_ARGS%

if errorlevel 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_BATCH_PAUSE%" == "" pause

exit /b %ERROR_CODE%

