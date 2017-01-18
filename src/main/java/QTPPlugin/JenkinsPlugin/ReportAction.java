package QTPPlugin.JenkinsPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.DirectoryBrowserSupport;

public class ReportAction implements Action{
	
	AbstractBuild build;
	FilePath qtpReoprt;
	BuildListener listener;

	public ReportAction(AbstractBuild build, FilePath qtpReoprt, BuildListener listener) {
		this.build=build;
		this.qtpReoprt=qtpReoprt;
		this.listener=listener;
	}

	
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "QTP Detail Report";
	}

	@Override
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return "graph.gif";
	}

	@Override
	public String getUrlName() {
		return "Report.html";
	}
	

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp) {
        if (this.build != null) {
            return new DirectoryBrowserSupport(this, qtpReoprt,
                    "qtpReport", "clipboard.gif", false);
        }
        return null;
    	
    }

}
