package com.uniovi.analyzer.tools.compilators;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class MavenCompilerTool {

	private static final List<String> PUBLISH_GOALS = Arrays.asList( "install", "build" );
	 
	public boolean compileFolder(String basePath, String extraClassPath, String arguments) {
		Invoker newInvoker = new DefaultInvoker();
		File folder = new File(basePath);
		newInvoker.setLocalRepositoryDirectory(folder);
		InvocationRequest request = new DefaultInvocationRequest();
	    request.setBaseDirectory(folder);
	    //request.setInteractive( false );
	    request.setGoals( PUBLISH_GOALS );
	    
	    InvocationResult result = null;
		try {
			result = newInvoker.execute( request );
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			return false;
		}
	    return result.getExitCode() == 0;
	}
	
}
