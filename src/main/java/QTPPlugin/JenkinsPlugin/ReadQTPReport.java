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


	public class ReadQTPReport implements Callable<ArrayList<String> , IOException> {
		
		 private static final long serialVersionUID = 1L;
		 BufferedReader qtpReportBuffer;
		 String line="";
		 BuildListener listener;
     	 File qtpReport;
     	 File target;
     	 ArrayList<String> reportData=null;
     	 
		public ReadQTPReport(BuildListener listener, File qtpReport, File target){
			this.listener=listener;
			this.qtpReport=qtpReport;
			this.target=target;
		}
		
        public ArrayList<String> call() throws IOException {
        	printInfo("Reading the QTP report to Master");
        	
 			try {
 				reportData=readFile(this.qtpReport);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				listener.fatalError("[Fatal Error]-->Error while reading QTP report");
				e.printStackTrace();
			}   
 		    return reportData;
        }
        
        private ArrayList<String> readFile(File qtpReport) throws IOException, InterruptedException {
            ArrayList<String> reportData=null;
            String line;
            try{
		        qtpReportBuffer = new BufferedReader(new FileReader(qtpReport));
		        reportData=new ArrayList<String>();
		        while ((line = qtpReportBuffer.readLine()) != null) {
		          	reportData.add(line);
		        }
            }catch(FileNotFoundException e){
            	listener.fatalError("[Fatal Error]-->Unable to read report data from Slave machine. QTP report file not found in master at "+qtpReport.toString());
            }finally{
            	qtpReportBuffer.close();
            }
            return reportData;
        }
        public void printInfo(String info){
    		listener.getLogger().println("[Info]--> "+info);
    	}
    	public void printError(String info){
    		listener.getLogger().println("[Error]--> "+info);
    	}
       
       
       
    }


	