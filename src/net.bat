@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

for /F "tokens=*" %%f in ('netsh wlan show interface') do (
for /F %%i in ('echo %%f ^| find "SSID"') do if %%i==SSID (
set qtemp=%%f
for /F "tokens=2 delims=:" %%j in ("!qtemp!") do (
	set qtemp=%%j
	javaw SocketMessenger 40902 NETLIST CURRENT !qtemp:~1!
)))


for /F "tokens=*" %%f in ('netsh wlan show networks') do (
for /F %%i in ('echo %%f ^| find "SSID"') do if %%i==SSID (
set qtemp=%%f
for /F "tokens=2 delims=:" %%j in ("!qtemp!") do (
	set qtemp=%%j
	javaw SocketMessenger 40902 NETLIST ENTRY !qtemp:~1!
)))