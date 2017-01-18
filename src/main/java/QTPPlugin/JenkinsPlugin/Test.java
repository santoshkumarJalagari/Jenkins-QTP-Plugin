package QTPPlugin.JenkinsPlugin;

public class Test {
	
 public static void main(String[] args) {
	
	 String textWhichDeclareVarable="ALLUSERSPROFILE,APPDATA,CLASSPATH,CLIENTNAME,CommonProgramFiles,COMPUTERNAME,ComSpec,HOMEDRIVE,HOMEPATH,LOGONSERVER,LSERVRC,LSFORCEHOST,NodeName,OS,Path,PATHEXT,ProgramFiles,PROMPT,PSModulePath,SESSIONNAME,SystemDrive,SystemRoot,TEMP,TMP,UATDATA,USERDOMAIN,USERNAME,USERPROFILE,windir,WORKSPACE,";
	  int length=textWhichDeclareVarable.length()-1;
        if (textWhichDeclareVarable.substring(length, length).equalsIgnoreCase(",")){
        	textWhichDeclareVarable=textWhichDeclareVarable.substring(1,length-1);
        }
        System.out.println(textWhichDeclareVarable);
}
}
