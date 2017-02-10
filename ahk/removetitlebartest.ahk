;-Caption
LWIN & LButton::
WinSet, Style, -0xC00000, A
WinSet, Style, -0x800000, A
WinSet, Style, -0x40000, A
WinGetPos, winx, winy, winw, winh, A
winwb := winw+40
WinMove, A,,,,%winwb%,%winh%
WinMove, A,,,,%winw%,%winh%
return
;

;+Caption
LWIN & RButton::
WinSet, Style, +0x40000, A
WinSet, Style, +0x800000, A
WinSet, Style, +0xC00000, A
return
;