@echo off
setlocal ENABLEDELAYEDEXPANSION

set JAR=mysql-connector-j-9.4.0.jar
set SRC=src
set OUT=out

if not exist "%OUT%" mkdir "%OUT%"

javac -encoding UTF-8 -d "%OUT%" -cp ".;%JAR%" ^
  "%SRC%\com\employee\app\Database.java" ^
  "%SRC%\com\employee\app\Employee.java" ^
  "%SRC%\com\employee\app\EmployeeDAO.java" ^
  "%SRC%\com\employee\app\EmployeeTableModel.java" ^
  "%SRC%\com\employee\app\EmployeeManagementApp.java"

if %ERRORLEVEL% NEQ 0 (
  echo Build failed.
  exit /b %ERRORLEVEL%
)

echo Build succeeded. Classes in %OUT%.
