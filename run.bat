@set olddir=%CD%
@echo Say (n)o upon termination to restore explorer...
@cd src
@java ShellReplacement
@for /r %%f in (..\exe\*) do @taskkill /FI "IMAGENAME eq %%~nxf" > nul
@start explorer.exe
@cd %olddir%