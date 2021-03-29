package es.uniovi.analyzer.tools.compilators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Build;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uniovi.analyzer.exceptions.CompilerException;

public class MavenCompilerTool extends AbstractCompiler {
	
	private final static String PLUGIN_VERSION = "0.0.1-SNAPSHOT";
	private static final List<String> PUBLISH_GOALS = Arrays.asList( "compile" );
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void compileFile(String basePath, String programID, String userId, String filename) throws CompilerException {
		throw new CompilerException();
	}
	 
	@Override
	public void compileFolder(String basePath, String programID, String userId, String classpath) throws CompilerException {
	    //Configure model
	    try {
			configurePOMFIle(new File(basePath + "pom.xml"), basePath, programID, userId);
		} catch (CompilerException e) {
			e.printStackTrace();
			throw e;
		}
	    //Execute
	    logger.info("Compiling program {} using maven", programID);
	    compileUsingMavenAPI(basePath);
	}
	
	
	private void compileUsingMavenAPI(String basePath) throws CompilerException {
		File folder = new File(basePath);
		Invoker newInvoker = new DefaultInvoker();
		if (shouldHideCompilerOutput()) {
			newInvoker.setOutputHandler(null);
		} 
		//Configure request 
		InvocationRequest request = new DefaultInvocationRequest();
	    request.setBaseDirectory(folder);
	    request.setBatchMode(true);
	    request.setGoals( PUBLISH_GOALS );
	    if (shouldShowDebugOutput())
	    	request.setDebug(true);
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
	
	private void configurePOMFIle(File pom, String basepath, String programID, String userId) throws CompilerException {
		Model model = null;
		//Read model
		try (FileReader fr = new FileReader(pom)) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(fr);
			modifyCompilerArgs(model, basepath, programID, userId);
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
	}
	
	private void modifyCompilerArgs(Model model, String basepath, String programID, String userId) throws CompilerException {
		Build build = model.getBuild();
		if (build == null) {
			build = new Build();
			model.setBuild(build);
		}
		Plugin plugin = build.getPluginsAsMap()
			.get("org.apache.maven.plugins:maven-compiler-plugin");
		if (plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId("org.apache.maven.plugins");
			plugin.setArtifactId("maven-compiler-plugin");
			build.addPlugin(plugin);
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
		// Set source
		Xpp3Dom source = configuration.getChild("source");
		if (source == null) {
			source = new Xpp3Dom("source");
			configuration.addChild(source);
		} 
		source.setValue("1.8");
		// Set target
		Xpp3Dom target = configuration.getChild("target");
		if (target == null) {
			target = new Xpp3Dom("target");
			configuration.addChild(target);
		} 
		target.setValue("1.8");
		//Xplugin argument
		addArg(compArgs, getPluginArg(programID, userId));
		plugin.setConfiguration(configuration);
	}
	
	private void addArg(Xpp3Dom compArgs, String argument) {
		Xpp3Dom arg = new Xpp3Dom("arg");
		arg.setValue(argument);
		compArgs.addChild(arg);
	}
	
}
