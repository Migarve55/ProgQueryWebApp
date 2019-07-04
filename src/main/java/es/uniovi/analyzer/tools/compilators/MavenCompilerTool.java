package es.uniovi.analyzer.tools.compilators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import es.uniovi.analyzer.exceptions.CompilerException;

public class MavenCompilerTool implements CompilerTool {
	
	private final static String PLUGIN_VERSION = "0.0.1-SNAPSHOT";
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin %s S %s";

	private static final List<String> PUBLISH_GOALS = Arrays.asList( "compile" );
	 
	public void compileFolder(String basePath, String programID, String extraArgs) throws CompilerException {
	    //Configure model
	    try {
			configurePOMFIle(new File(basePath + "pom.xml"), basePath, programID, extraArgs);
		} catch (CompilerException e) {
			e.printStackTrace();
			throw e;
		}
	    //Execute
	    compileUsingMavenAPI(basePath);
	}
	
	
	private void compileUsingMavenAPI(String basePath) throws CompilerException {
		File folder = new File(basePath);
		Invoker newInvoker = new DefaultInvoker();
		newInvoker.setOutputHandler(null);
		//Configure request 
		InvocationRequest request = new DefaultInvocationRequest();
	    request.setBaseDirectory(folder);
	    request.setBatchMode(true);
	    request.setGoals( PUBLISH_GOALS );
		InvocationResult result = null;
		try {
			result = newInvoker.execute( request );
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			throw new CompilerException("error.compiler.maven.invocation");
		}
	    if (result.getExitCode() != 0) {
	    	throw new CompilerException("error.compiler.maven.execution");
	    }
	}
	
	private void configurePOMFIle(File pom, String basepath, String programID, String extraArgs) throws CompilerException {
		Model model = null;
		//Read model
		try (FileReader fr = new FileReader(pom)) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(fr);
			modifyCompilerArgs(model, basepath, programID, extraArgs);
			addDependencies(model);
			//addRepo(model);
		} catch (FileNotFoundException e) {
			throw new CompilerException("error.compiler.maven.pomFileNotFound");
		} catch (IOException e) {
			e.printStackTrace();
			throw new CompilerException("error.compiler.io");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new CompilerException("error.compiler.maven.pomMalformed");
		} 
		//Modify it
		try (FileWriter fw = new FileWriter(pom)) {
			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(fw, model);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CompilerException("error.compiler.io");
		}
	}
	
	private void addDependencies(Model model) {
		Dependency pluginDependency = new Dependency();
		pluginDependency.setGroupId("es.uniovi.progQuery");
		pluginDependency.setArtifactId("progQuery");
		pluginDependency.setVersion(PLUGIN_VERSION);
		model.addDependency(pluginDependency);
		//Add neo4j plugins used by the compilator plugin
	}
	
	private void modifyCompilerArgs(Model model, String basepath, String programID, String extraArgs) throws CompilerException {
		Plugin plugin = model.getBuild().getPluginsAsMap()
			.get("org.apache.maven.plugins:maven-compiler-plugin");
		if (plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId("org.apache.maven.plugins");
			plugin.setArtifactId("maven-compiler-plugin");
			model.getBuild().addPlugin(plugin);
		}
		//Change plugin configuration
		Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
		if (configuration == null) {
			configuration = new Xpp3Dom("configuration");
			plugin.setConfiguration(configuration);
		}
		Xpp3Dom compArgs = configuration.getChild("compilerArgs");
		if (compArgs == null) {
			compArgs = new Xpp3Dom("compilerArgs");
			configuration.addChild(compArgs);
		}
		//Xplugin argument
		addArg(compArgs, String.format(PLUGIN_ARG, programID, System.getProperty("neo4j.url")));
		plugin.setConfiguration(configuration);
		//Extra arguments
		if (!extraArgs.trim().isEmpty()) {
			for (String arg : extraArgs.split(" ")) {
				if (!arg.trim().isEmpty()) 
					addArg(compArgs, arg);
			}
		}
	}
	
	private void addArg(Xpp3Dom compArgs, String argument) {
		Xpp3Dom arg = new Xpp3Dom("arg");
		arg.setValue(argument);
		compArgs.addChild(arg);
	}
	
}
