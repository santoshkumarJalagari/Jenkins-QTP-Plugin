package QTPPlugin.JenkinsPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.Callable;


	public class CheckQTPScritpt implements Callable<String , IOException> {
		
		 private static final long serialVersionUID = 1L;
		 BufferedReader qtpScriptBuffer;
		 BuildListener listener;
     	 String qtpScriptPath;
     	 
		public CheckQTPScritpt(BuildListener listener, String qtpScriptPath){
			this.listener=listener;
			this.qtpScriptPath=qtpScriptPath;
		}
		
        public String call() throws IOException {
        	printInfo("Checking QTP script path...");
        	
        	File scriptPath= new File(qtpScriptPath);
        	if (scriptPath.exists()){
        		return "YES";
        	}else{
        		return "NO";
        	}
 			
        }
        public void printInfo(String info){
    		listener.getLogger().println("[Info]--> "+info);
    	}
    	public void printError(String info){
    		listener.getLogger().println("[Error]--> "+info);
    	}
        
    }


	