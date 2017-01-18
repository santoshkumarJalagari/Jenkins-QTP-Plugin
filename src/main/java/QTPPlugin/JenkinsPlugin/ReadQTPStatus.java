package QTPPlugin.JenkinsPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import hudson.model.BuildListener;
import hudson.remoting.Callable;


	public class ReadQTPStatus implements Callable<String , IOException> {
		
		 private static final long serialVersionUID = 1L;
		 File statusFile;
		 File detailsStepsFile;
		 BufferedReader statusFileBuffer;
		 BufferedReader detailFileBuffer;
		 BufferedReader qtpReportBuffer;
		 String line="";
		 BuildListener listener;
		 boolean qtpStatusFileFound=true;
		 boolean detailFileFound=true;
		 String oldLineNumber="(LINECOUNT).";
     	 String newLine="";
     	 int count=0;
     	 
		public ReadQTPStatus(File statusFile,BuildListener listener, File detailsStepsFile){
			this.statusFile=statusFile;
			this.listener=listener;
			this.detailsStepsFile=detailsStepsFile;
		}
		
        public String call() throws IOException {
        	printInfo("Reading the status of the scipt");
        	do  {   
        		if(detailsStepsFile.exists()){
        			detailFileFound=false;
        			detailFileBuffer = new BufferedReader(new FileReader(detailsStepsFile));
        			listener.getLogger().println("");
        			listener.getLogger().println("**************************************************************************************");
     				listener.getLogger().println("                               ####QTP Execution Steps###                              ");
        			break;
        		}else if(statusFile.exists()){
        			break;
        		}
        	}while(detailFileFound);
        	
        	
 			 try {
 				
 			   	do{
 			   		if(statusFile.exists()){
 			   			statusFileBuffer = new BufferedReader(new FileReader(statusFile));
 			   			qtpStatusFileFound=false;
 			   			listener.getLogger().println("**************************************************************************************");
 			   			printInfo("QTP Status file found");
 			   			break;
 			   		}
 			   		if(detailsStepsFile.exists()){
 			   			String oldNum=oldLineNumber.replace("LINECOUNT", String.valueOf(count));
 			   			while((newLine=detailFileBuffer.readLine())!=null){
	 			   			if(!newLine.contains(oldNum)){
	 			   				count=count+1;
	 			   				if(newLine!="" && newLine.contains(".)")){
	 			   						String temp[]=newLine.split(".\\)");
	 			   						String stepToWrite="";
	 			   						if (!temp[1].contains("Test iteration - ")){
	 			   							stepToWrite=count+". "+temp[1];
	 			   						}else{
	 			   							stepToWrite=temp[1];
	 			   						}
		 			   					if(stepToWrite!=""){
		 			   						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 			   						Calendar cal = Calendar.getInstance();
		 			   						if (stepToWrite.contains("Test iteration - ")){
		 			   							listener.getLogger().println("**************************************************************************************");
		 			   							listener.getLogger().println("");
		 			   							listener.getLogger().println("************************************"+stepToWrite+"**********************************");
		 			   						}else{
		 			   							listener.getLogger().println("[Test Steps]-->"+(dateFormat.format(cal.getTime()))+stepToWrite);
		 			   						}
		 			   					}
	 			   				}
	 			   			}
 			   			}
 			   		}
 			    		
 			   	}while(qtpStatusFileFound); 
 			   
 			   boolean fileEmpty=true;
 			   	do{
 			   		line = statusFileBuffer.readLine();
 			   		fileEmpty=StringUtils.isNotBlank(line);
 			   	}while(!fileEmpty);
		    } catch (FileNotFoundException e){
		    	listener.fatalError("[Fatal Error]-->File not found, Please check whether data provided to run QTP script is correct");
		    }catch (ArrayIndexOutOfBoundsException e){
		    	listener.fatalError("[Fatal Error]-->Array out of bound exception");
		    	e.printStackTrace();
		    }
		    finally {
		    	statusFileBuffer.close();
		    	if(!detailFileFound){
		    		detailFileBuffer.close();
		    	}
		    }
 		    return line;
        }
        public void printInfo(String info){
    		listener.getLogger().println("[Info]--> "+info);
    	}
    	public void printError(String info){
    		listener.getLogger().println("[Error]--> "+info);
    	}
        
    }
        
       


	