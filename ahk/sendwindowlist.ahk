DetectHiddenWindows Off
WinGet windows, list
RunWait javaw SocketMessenger 40902 WINDOWLISTSTART
name := "WINDOWLIST "
Loop %windows%
{
	id := windows%A_Index%
	WinGetPos WinX, WinY, WinWidth, WinHeight, ahk_id %id%
	If (WinWidth < 2)
		Continue
	WinGetTitle, title, ahk_id %id%
	pid = 0
	WinGet win,, %title%
	DllCall("GetWindowThreadProcessId", "UInt", win, "UInt *", pid)
	proc := DllCall("OpenProcess", "UInt", 0x400 | 0x10, "Int", False, "UInt", pid)
	pathLen = 260*2
	VarSetCapacity(filePath, pathLen, 0)
	DllCall("Psapi.dll\GetModuleFileNameExW", "UInt", proc, "Int", 0, "Str", filePath, "UInt", pathLen)
	DllCall("CloseHandle", "UInt", proc)
	name := name . "`nWINDOWLIST " . title . "~~~" . filePath
}
RunWait javaw SocketMessenger 40902 %name%
Exit