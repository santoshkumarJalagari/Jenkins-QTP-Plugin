package QTPPlugin.JenkinsPlugin;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Map.Entry;

public class Supporting {
	static BuildListener listener;
	public Supporting(BuildListener listener){
		this.listener=listener;
	}
	public static String setEnvironmentVariables(String actualString, EnvVars envVars){
   	 String stringBeforeReplace=actualString;
        for(Entry<String, String> entry : envVars.entrySet()) {
        	  String envToSearch = "%"+entry.getKey()+"%";
        	  String envValue = entry.getValue();
        	  if(stringBeforeReplace.contains(envToSearch)){
        		  stringBeforeReplace=stringBeforeReplace.replace(envToSearch, envValue);
        	  }
        }
   	return stringBeforeReplace;
   }
	
   public static void createLibForEnv(EnvVars envVars,String dependencyFolderPath){
	   	 String varableAccessModifier="public ";
	   	 File fout = new File(dependencyFolderPath+File.separator+"env.vbs");
	   	 
		 FileOutputStream fos;
		 
		try {
			fout.createNewFile();
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	        for(Entry<String, String> entry : envVars.entrySet()) {
	        	String envVarable = varableAccessModifier+entry.getKey()+"="+entry.getValue();
				bw.write(envVarable);
	      		bw.newLine();
	        }
	        fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	public static void killProcess(String exactProcessNameToKill, Launcher launcher, String[] strings, BuildListener listener, FilePath projectWorkspaceOnSlave) {

		final String KILL = "taskkill /F /IM ";
		if (!exactProcessNameToKill.contains(".exe")){
			exactProcessNameToKill=exactProcessNameToKill+".exe";
		}
		String cmd =KILL+exactProcessNameToKill;
		try {
			int cmdResult = launcher.launch(cmd, new String[0], listener.getLogger(), projectWorkspaceOnSlave).join();
			printInfo("Killing process '"+ exactProcessNameToKill+"'" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printError("Unable to kill the proces '"+exactProcessNameToKill+"'");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			printError("Unable to kill the proces '"+exactProcessNameToKill+"'");
			e.printStackTrace();
		}
	}
	
	public static void printInfo(String info){
		listener.getLogger().println("[Info]--> "+info);
	}
	public static void printError(String info){
		listener.getLogger().println("[Error]--> "+info);
	}
}
