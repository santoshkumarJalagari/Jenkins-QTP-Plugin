package QTPPlugin.JenkinsPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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


	public class WriteFiles implements Callable<ArrayList<String> , IOException> {
		
		private static final long serialVersionUID = 1L;
		ArrayList<String> data=null;
    	File filePath=null;
     	BuildListener listener=null;
     	 
		public WriteFiles(ArrayList<String> data, File filePath, BuildListener listener){
			this.data=data;
			this.filePath=filePath;
			this.listener=listener;
		}
		
        public ArrayList<String> call() throws IOException {
//        	printInfo("Writing dependency files to slave");
        	try {
//        		printInfo(filePath.getAbsolutePath());
	        	filePath.createNewFile();
	  			FileWriter writer = new FileWriter(filePath, true);
	  			PrintWriter output = new PrintWriter(writer);
	            for (int i = 0; i < data.size(); i++) {
	            	output.println(data.get(i));
	            }
	            output.close();
        	}catch (FileNotFoundException e) {
      			printError("File not found");
      		}
      		catch (IOException e) {
      			printError("Unable to create file at "+filePath.toPath());
      		}
 		    return null;
        }
        
        public void printInfo(String info){
    		listener.getLogger().println("[Info]--> "+info);
    	}
    	public void printError(String info){
    		listener.getLogger().println("[Error]--> "+info);
    	}
       
       
       
    }


	