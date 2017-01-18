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
import java.util.Map.Entry;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.Callable;


	public class CreateEnvFile implements Callable<String , IOException> {
		
		 private static final long serialVersionUID = 1L;
		 File file; 
		 String line;
		 BuildListener listener;
		 String dependencyFolderPath;
		 FileOutputStream fos;
		 EnvVars envVars=null;
		 String varableAccessModifier="public ";
		 String envFilePath="";
		 
		
		 
		public CreateEnvFile(EnvVars envVars,String dependencyFolderPath, BuildListener listner){
			this.dependencyFolderPath=dependencyFolderPath;
			this.envVars=envVars;
			this.listener=listner;
		}
		
        public String call() throws IOException {
        	envFilePath =dependencyFolderPath+File.separator+"env";
        	String textWhichDeclareVarable="";
      		try {
      			file = new File(envFilePath);
      			file.createNewFile();
      			FileWriter writer = new FileWriter(file, true);
      			PrintWriter output = new PrintWriter(writer);
      	        for(Entry<String, String> entry : envVars.entrySet()) {
      	        	String envVarable="";
      	        	if(entry.getKey().matches("[a-zA-Z]*") && !entry.getKey().equals("")){
      	        		textWhichDeclareVarable=textWhichDeclareVarable+entry.getKey()+",";
      	        	}
      	        }
      	       
      	        textWhichDeclareVarable=varableAccessModifier+textWhichDeclareVarable+"JenkinsReportPahtOnSlaveMachine";
      	        output.println(textWhichDeclareVarable);
      	        output.println("JenkinsReportPahtOnSlaveMachine= \""+dependencyFolderPath+"\"");
      	        for(Entry<String, String> entry : envVars.entrySet()) {
    	        	String envVarable="";
    	        	if(entry.getKey().matches("[a-zA-Z]*") && !entry.getKey().equals("")){
    	        		textWhichDeclareVarable=textWhichDeclareVarable+entry.getKey()+",";
    	        		envVarable = entry.getKey()+"=\""+entry.getValue().replace("\"", "")+"\"";
    	        		output.println(envVarable);
    	        	}
//    	      	    listener.getLogger().println(envVarable+"\n");
    	        }
      	        
      	      output.close();
      		} catch (FileNotFoundException e) {
      			// TODO Auto-generated catch block
      			printError("Enviroment variable File not found at '"+envFilePath+"' write a environment varables");
      			
      		}
      		catch (IOException e) {
      			// TODO Auto-generated catch block
      			printError("Unable to create file at "+envFilePath);
      		}
			return dependencyFolderPath;
           }
        
        public String replaceComma(String str){
        	String textWhichDeclareVarable=str;
        	try{
   			  int length=textWhichDeclareVarable.length()-1;
   			  char subString=textWhichDeclareVarable.charAt(length);
   			  String lastCharInString=String.valueOf(subString); 
   		        if (lastCharInString.equalsIgnoreCase(",")){
   		        	textWhichDeclareVarable=textWhichDeclareVarable.substring(0,length);
   		        }
   			}catch(StringIndexOutOfBoundsException s){
   				s.printStackTrace();
   				printError("String out of bound execption while writing environment varables");
   			}
        	return textWhichDeclareVarable;
        }
        public void printInfo(String info){
    		listener.getLogger().println("[Info]--> "+info);
    	}
    	public void printError(String info){
    		listener.getLogger().println("[Error]--> "+info);
    	}
     }
       

