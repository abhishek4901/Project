@echo off
setlocal

set JAR=mysql-connector-j-9.4.0.jar
set OUT=out

java -cp "%OUT%;%JAR%" com.employee.app.EmployeeManagementApp
