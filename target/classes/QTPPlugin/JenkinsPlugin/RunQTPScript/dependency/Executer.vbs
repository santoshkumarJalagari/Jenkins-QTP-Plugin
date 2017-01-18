Set Executer = WScript.Arguments
SctipName = Executer(0)
startIteration = Executer(1)
endIteration = Executer(2)
QTPStatusPath = Executer(3)
generateReport = Executer(4)
makeQTPVisible = Executer(5)
scriptStatusFilePath = QTPStatusPath & "\QTPScriptStatus"
ResultFolder = QTPStatusPath+"\QTPResult"
Dim objLib


If InStr(1, QTPStatusPath, "_") > 0 Then
    QTPStatusPath = Replace(QTPStatusPath, "_", " ")
End If

Err.Clear
On Error Resume Next
Set App = CreateObject("QuickTest.Application")
Set objRunResult = CreateObject("QuickTest.RunResultsOptions")
intErr = Err.Number
If intErr <> 0 Then
	WScript.StdOut.Write "#####Error in launching QTP####" & vbCrLf
	WScript.StdOut.Write "Error Number: " & Err.Number & vbCrLf
	WScript.StdOut.Write "Source of Error: " & Err.Source & vbCrLf
    WScript.StdOut.Write "Error Description: " & Err.Description & vbCrLf
    WScript.StdOut.Write "Please wait for 5 sec to try opening QTP for " & intCount & " time" & vbCrLf
	createStatusFile("Error While opening QTP. Please check we have any problem in opening QTP manually in slave machine")                        
Else
	QTPStatus = True
'If QTP is already launched, do nothing, else launch it
	If App.Launched = False Then
		App.Launch
		'WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
		WScript.StdOut.Write "[Info]--> QTP Opened Successfully" & vbCrLf		
		'WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf	
	Else
		'WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
		WScript.StdOut.Write "[Info]--> QTP is already Opened in the machine" & vbCrLf
		'WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
	End If
'Set qtp to visible
If(makeQTPVisible) Then
		App.Visible = True
End If
End If
Err.Clear
App.Open (SctipName)

If Err.Description <> "" Then
                WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
                WScript.StdOut.Write "Execution has been stop because of below Error " & vbCrLf
                WScript.StdOut.Write "Error Number: " & Err.Number & vbCrLf
                WScript.StdOut.Write "Source of Error: " & Err.Source & vbCrLf
                WScript.StdOut.Write "Error Description: " & Err.Description & vbCrLf
                WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
		createStatusFile("Error While opening script. Script path not found. Path: "+SctipName)
End If
WScript.StdOut.Write "[Info]--> QTP Script loaded " & vbCrLf
Set objLib = App.Test.Settings.Resources.Libraries

call addLibraryFilesForQTPFromJenkins ("env")
If (generateReport) Then
    'If the library is not already associated with the test case, associate it..
	call addLibraryFilesForQTPFromJenkins ("ReportFunctions")
    call addLibraryFilesForQTPFromJenkins ("SAP")
    call addLibraryFilesForQTPFromJenkins ("Siebel")
    call addLibraryFilesForQTPFromJenkins ("Web")
End If
objRunResult.ResultsLocation = ResultFolder

'App.Test.Settings.Run.IterationMode = "rngAll"
App.Test.Settings.Run.IterationMode = "rngIterations"
App.Test.Settings.Run.startIteration = Trim(CInt(startIteration))
App.Test.Settings.Run.endIteration = Trim(CInt(endIteration))
App.Options.Run.RunMode = "Normal"
App.Options.Run.ViewResults = False
App.Test.Run objRunResult
RunStatus = App.Test.LastRunResults.Status
createStatusFile(RunStatus)

Function addLibraryFilesForQTPFromJenkins(fileName)

    Set fso1 = CreateObject("Scripting.FileSystemObject")
    If fso1.FileExists(QTPStatusPath + "\"+fileName) Then
		If objLib.Find(QTPStatusPath + "\"+fileName) = 0  Then
			'WScript.StdOut.Write "Removed existing library: " & QTPStatusPath + "\"+fileName& vbCrLf
			objLib.Remove(QTPStatusPath + "\"+fileName )
		End If
       If objLib.Find(QTPStatusPath + "\"+fileName) = -1 Then ' If library is not already added
			Err.Clear
            objLib.Add QTPStatusPath + "\"+fileName, 1 ' Associate the library to the test case
			'WScript.StdOut.Write "Added existing library: " & QTPStatusPath + "\"+fileName& vbCrLf
			If Err.Description <> "" Then
						WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
						WScript.StdOut.Write "Execution has been stop because of below Error " & vbCrLf
						WScript.StdOut.Write "Error Number: " & Err.Number & vbCrLf
						WScript.StdOut.Write "Source of Error: " & Err.Source & vbCrLf
						WScript.StdOut.Write "Error Description: " & Err.Description & vbCrLf
						WScript.StdOut.Write "***************************************************************************************************************" & vbCrLf
						createStatusFile("Error While loading Libraty file. Library file path: "+QTPStatusPath + "\"+fileName)
			End If
       End If
    End If
End Function
Function createStatusFile(status)
	Set fso1 = CreateObject("Scripting.FileSystemObject")
	If fso1.FileExists(scriptStatusFilePath) Then
		fso1.DeleteFile (scriptStatusFilePath)
		WScript.StdOut.Write "[Info]--> Successfully deleted the file at path: " & scriptStatusFilePath & vbCrLf
	End If
	fso1.CreateTextFile (scriptStatusFilePath)
	Set MyFile = fso1.OpenTextFile(scriptStatusFilePath, 2, True)
	MyFile.WriteLine status
	MyFile.Close
End Function




