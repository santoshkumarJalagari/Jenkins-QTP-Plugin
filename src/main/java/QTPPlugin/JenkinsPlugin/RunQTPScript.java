package QTPPlugin.JenkinsPlugin;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.Proc;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;

import org.apache.tools.ant.taskdefs.Mkdir;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.net.ssl.ManagerFactoryParameters;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class RunQTPScript extends Builder {

    public final String strQTPScriptName;
    public final int intStartIteration;
    public final int intEndIteration;
    public final int intTesting;
    public final boolean isKillQTPSelected;
    public final boolean isResultAtWorkSpaceSelected;
    public final String processName;
    public final boolean isAnyProcessToKill;
    public final boolean isGenerateReportSelected;
    public final boolean isQTPVisibleSelected;
    String QTP_REPORT = "qtpReport";
       
         
    @DataBoundConstructor
    public RunQTPScript(String strQTPScriptName,int intStartIteration, int intEndIteration,int intTesting, boolean isKillQTPSelected, boolean isResultAtWorkSpaceSelected, String processName, boolean isAnyProcessToKill, boolean isGenerateReportSelected, boolean isQTPVisibleSelected) {
        this.strQTPScriptName = strQTPScriptName;
        this.intStartIteration=intStartIteration;
        this.intEndIteration=intEndIteration;
        this.intTesting=intTesting;
        this.isKillQTPSelected=isKillQTPSelected;
        this.isResultAtWorkSpaceSelected= isResultAtWorkSpaceSelected;
        this.processName=processName;
        this.isAnyProcessToKill=isAnyProcessToKill;
        this.isGenerateReportSelected=isGenerateReportSelected;
        this.isQTPVisibleSelected=isQTPVisibleSelected;
    }

    public boolean isResultAtWorkSpaceSelected() {
		return isResultAtWorkSpaceSelected;
	}
    public boolean isQTPVisibleSelected() {
		return isQTPVisibleSelected;
	}
    public boolean isGenerateReportSelected() {
		return isGenerateReportSelected;
	}

	public String getStrQTPScriptName() {
        return strQTPScriptName;
    }
    public int getIntStartIteration() {
		return intStartIteration;
	}

	public int getIntTesting() {
		return intTesting;
	}

	public boolean isKillQTPSelected() {
		return isKillQTPSelected;
	}

	public int getIntEndIteration() {
		return intEndIteration;
	}
	public String getProcessName() {
        return processName;
    }
	public boolean getIsAnyProcessToKill() {
		return isAnyProcessToKill;
	}

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
    	EnvVars envVars = new EnvVars();
//    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//    	URL url = classLoader.getResource("");
    	FilePath sourceFilePathInMaster=new FilePath(new File("./RunQTPScript/dependency"));
    	
    	FilePath projectWorkspaceOnSlave=build.getProject().getWorkspace();;
    	String filePath=projectWorkspaceOnSlave.toString();
    	VirtualChannel channel = launcher.getChannel();
    	String qtpStatusFileName="QTPScriptStatus";
    	String detailStepsFileName="detailsteps";
    	ArrayList<String> reportData=null;
    	Supporting support=new Supporting(listener);
    	projectWorkspaceOnSlave.deleteContents();
    	
    	envVars = build.getEnvironment(listener);
        String filePaths="Executer.vbs;" +
    		   "ReportFunctions;" +
       		"SAP;" +
       		"Siebel;" +
       		"Web;";
      
        String[] filePathArr=filePaths.split(";");
    	Supporting.printInfo("Master Path: "+sourceFilePathInMaster.toURI());
    	Supporting.printInfo("Slave workspace path: "+projectWorkspaceOnSlave.toURI());
    	FilePath sourceFilePathInMaster1=null;;
    	for(int i=0;i<filePathArr.length;i++){
    		try{
    			ArrayList<String> data=readFile("/QTPPlugin/JenkinsPlugin/RunQTPScript/dependency/"+filePathArr[i]);
    			File filePathOnSlave=new File(filePath+File.separator+filePathArr[i]);
    			writeFilesToSlave(channel, listener, filePathOnSlave, data);
        	}catch(NullPointerException e){
        		Supporting.printInfo("No files at "+sourceFilePathInMaster1);
        		e.printStackTrace();
        	}
    	}
        
        String scriptPath=Supporting.setEnvironmentVariables(getStrQTPScriptName(), envVars);
        if(checkQTPScriptPath(channel,listener,scriptPath).equalsIgnoreCase("NO")){
        	Supporting.printError("Given QTP script path '"+scriptPath+"' not found on slave. Please recheck the script path given");
        	build.setResult(Result.UNSTABLE);
    		return true;
        }
        
        if(isKillQTPSelected()){
        	Supporting.printInfo("Finding for QTP process to Kill");
        	Supporting.killProcess("QTPro",launcher,new String[0],listener,projectWorkspaceOnSlave);
        }
        
        if(getIsAnyProcessToKill()){
        	String[] processNames=getProcessName().split(";");
        	for(int i=0;i<processNames.length;i++){
        		Supporting.killProcess(processNames[i],launcher,new String[0],listener,projectWorkspaceOnSlave);
        	}
        }
        
        if(isGenerateReportSelected){
        	Supporting.printInfo("You have selected generate details HTML report");
        }else{
        	Supporting.printInfo("You have not selected generate details HTML report");
        }
        
        channel.call(new CreateEnvFile(envVars,filePath, listener));
        
        String cmd = "cscript "+projectWorkspaceOnSlave+"\\Executer.vbs"  +" "+  scriptPath+" "+ getIntStartIteration() + " " + getIntEndIteration()+" "+filePath+" "+isGenerateReportSelected()+" "+isQTPVisibleSelected;
//        Supporting.printInfo("Command executing: "+cmd);
        launcher.launch(cmd, new String[0], listener.getLogger(), projectWorkspaceOnSlave);
        
        File statusFile=new File(filePath + File.separator+qtpStatusFileName);
        File detailStepsFile=new File(filePath + File.separator+detailStepsFileName);
        File qtpReport = new File(filePath+File.separator+"qtpReport"+File.separator+"Report.html");
        FilePath target = new FilePath(getQTPReportDir(build,listener));
        File targetFile=new File(target.toString());
        Supporting.printInfo("QTP Report Path on slave: "+qtpReport.toString());
        
        String qtpScriptStatus=getScriptStatus(channel,statusFile,listener,detailStepsFile);
        
        if(isGenerateReportSelected){
        	reportData=readQTPReport(channel,listener,qtpReport,targetFile);
        	writeFile(targetFile, reportData, listener);
        	ReportAction buildAction = new ReportAction(build,target,listener);
            build.addAction(buildAction);
        }
        
       
        
        if (qtpScriptStatus.equalsIgnoreCase("Passed") || qtpScriptStatus.equalsIgnoreCase("Warning")){
    		listener.getLogger().println("Scritp Status: : "+qtpScriptStatus);
    		return true;
    	}
    	else{
    		listener.getLogger().println("Scritp Status: : "+qtpScriptStatus);
    		return false;
    	}
        
    }  
    public String getScriptStatus(VirtualChannel channel, File statusFile,BuildListener listener, File detailsStepsFile) throws InterruptedException, IOException {
    	String status=channel.call(new ReadQTPStatus(statusFile,listener,detailsStepsFile));
        return status;	
    }
    public String checkQTPScriptPath(VirtualChannel channel,BuildListener listener,String qtpScriptpath) throws InterruptedException, IOException {
    	String status=channel.call(new CheckQTPScritpt(listener,qtpScriptpath));
        return status;	
    }
    
    public ArrayList<String> readQTPReport(VirtualChannel channel, BuildListener listener,File qtpFilePath, File targetFilePath) throws InterruptedException, IOException {
    	ArrayList<String> reportData=channel.call(new ReadQTPReport(listener,qtpFilePath,targetFilePath));
        return reportData;	
    }
    public ArrayList<String> writeFilesToSlave(VirtualChannel channel, BuildListener listener,File filePath, ArrayList<String> data) throws InterruptedException, IOException {
    	ArrayList<String> reportData=channel.call(new WriteFiles(data,filePath,listener));
        return reportData;	
    }
    public void writeFile(File target,ArrayList<String> reportData,BuildListener listener ) throws IOException {
    	BufferedWriter targetBuffer=null;
    	try{
        	targetBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target)));
        	
        	for (int i = 0; i < reportData.size(); i++) {
        		targetBuffer.write(reportData.get(i).replace("ÿþ", "").replace("< ", "<"));
//        		targetBuffer.newLine();
        		
        	}
    	}catch(FileNotFoundException e){
    		listener.fatalError("Unable to write report data to Master. QTP report file not found in master at "+target.toString());
    	}finally{
    		targetBuffer.close();
    	}
    	
    }
    protected File getQTPReportDir(AbstractBuild<?,?> build, BuildListener listener) {
    	String path=build.getRootDir().toString()+File.separator+"qtpReport"+File.separator;
    	
    	File targetDir=new File(path);
    	if(!targetDir.exists()){
    		targetDir.mkdir();
    	}
    	File reportPath=new File(path+File.separator+"Report.html");
    	if(!reportPath.exists()){
    		try {
				reportPath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        return reportPath;
    }
    public ArrayList<String> readFile(String filePath) throws java.io.FileNotFoundException,
    	java.io.IOException {
    	ArrayList<String> aList = new ArrayList<String>();

		try {
		    final InputStream is = this.getClass().getResourceAsStream(filePath);
		    try {
		        final Reader r = new InputStreamReader(is);
		        try {
		            final BufferedReader br = new BufferedReader(r);
		            try {
		                String line = null;
		                while ((line = br.readLine()) != null) {
//		                	Supporting.printInfo(line);
		                    aList.add(line);
		                }
		                br.close();
		                r.close();
		                is.close();
		            } finally {
		                try {
		                    br.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        } finally {
		            try {
		                r.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		} catch (IOException e) {
		    // failure
		    e.printStackTrace();
		}
		return aList;
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension 
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
       
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) { 
            return true;
        }

        public String getDisplayName() {
            return "Execute QTP Script";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }
    }
}


