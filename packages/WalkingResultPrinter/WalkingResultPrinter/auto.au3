Opt("WinTitleMatchMode", 3)
If WinExists("データ読込み") Or WinExists("機器設定") Or WinExists("被験者一覧") Or Not WinExists("活動量計アプリケーション") Then
	MsgBox(0, "活動量計アプリケーション補助ツール", "活動量計アプリケーションのメニュー画面を表示してください。")
EndIf
WinWaitClose("データ読込み")
WinWaitClose("機器設定")
WinWaitClose("被験者一覧")
Local $menuWindow = WinWait("活動量計アプリケーション")
Opt("WinTitleMatchMode", 1)
WinActivate($menuWindow)
ControlClick($menuWindow, "", "被験者管理")

Local $listWindow = WinWaitActive("被験者一覧")
Send("{TAB}")
Local $focusedControl
Do
	ControlClick($listWindow, "", "集計ファイル作成")
	Local $dialogBox = WinWaitActive("", "OK")
	ControlClick($dialogBox, "", "OK")
	WinWaitActive($listWindow)
	For $i = 1 To 7
		Send("{TAB}")
	Next
	$focusedControl = ControlGetFocus($listWindow)
Until ControlGetText($listWindow, "", $focusedControl) = "被験者データ登録"
