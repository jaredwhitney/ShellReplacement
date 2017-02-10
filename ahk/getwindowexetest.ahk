; Code to get exe from window written by autohotkey board user YMP
; https://autohotkey.com/board/topic/53895-how-get-path-exe-of-active-window/?p=337967

^Space::
PID = 0
WinGet, hWnd,, A
DllCall("GetWindowThreadProcessId", "UInt", hWnd, "UInt *", PID)
hProcess := DllCall("OpenProcess", "UInt", 0x400 | 0x10, "Int", False, "UInt", PID)
pathLen = 260*2
VarSetCapacity(filePath, pathLen, 0)
DllCall("Psapi.dll\GetModuleFileNameExW", "UInt", hProcess, "Int", 0, "Str", filePath, "UInt", pathLen)
DllCall("CloseHandle", "UInt", hProcess)
MsgBox, %filePath%
Return