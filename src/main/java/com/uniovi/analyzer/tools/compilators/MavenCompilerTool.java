package com.uniovi.analyzer.tools.compilators;

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
import org.apache.maven.model.Repository;
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

import com.uniovi.analyzer.exceptions.CompilerException;

public class MavenCompilerTool implements CompilerTool {
	
	private final static String PLUGIN_CLASSPATH = "src/main/resources/plugin/ProgQuery.jar;src/main/resources/plugin/neo4jLibs/*;";
	
	private final static String PLUGIN_ARG = "-Xplugin:ProgQueryPlugin %s";
	private final static String DB_PATH = "neo4j/data/ProgQuery.db";

	private static final List<String> PUBLISH_GOALS = Arrays.asList( "install", "compile" );
	 
	public void compileFolder(String basePath, String extraClassPath, String arguments) throws CompilerException {
		File folder = new File(basePath);
		//Config invoker
		Invoker newInvoker = new DefaultInvoker();
		newInvoker.setLocalRepositoryDirectory(folder);
		newInvoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
		//newInvoker.setOutputHandler(null);
		//Configure request 
		InvocationRequest request = new DefaultInvocationRequest();
	    request.setBaseDirectory(folder);
	    request.setBatchMode(true);
	    request.setGoals( PUBLISH_GOALS );
	    //Configure model
	    try {
			configurePOMFIle(new File(basePath + "pom.xml"), basePath);
		} catch (CompilerException e) {
			e.printStackTrace();
			throw e;
		}
	    //Execute
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
	
	private void configurePOMFIle(File pom, String basepath) throws CompilerException {
		Model model = null;
		//Read model
		try (FileReader fr = new FileReader(pom)) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(fr);
			addRepo(model);
			addDependencies(model);
			modifyCompilerArgs(model, basepath);
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
	
	private void addRepo(Model model) {
		Repository repository = new Repository();
		repository.setId("LocalRepo");
		repository.setName("LocalRepo");
		repository.setUrl("file://${project.basedir}/../../plugin");
		model.addPluginRepository(repository);
	}
	
	private void addDependencies(Model model) {
		Dependency pluginDependency = new Dependency();
		pluginDependency.setGroupId("es.uniovi.progQuery");
		pluginDependency.setArtifactId("progQuery");
		pluginDependency.setVersion("1.0");
		model.addDependency(pluginDependency);
//		Dependency pluginLibsDependency = new Dependency();
//		pluginLibsDependency.setGroupId("es.uniovi.progQuery.libs");
//		pluginLibsDependency.setArtifactId("libs");
//		pluginLibsDependency.setVersion("1.0");
//		model.addDependency(pluginLibsDependency);
	}
	
	private void modifyCompilerArgs(Model model, String basepath) throws CompilerException {
		Plugin plugin = model.getBuild().getPluginsAsMap()
			.get("org.apache.maven.plugins:maven-compiler-plugin");
		if (plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId("org.apache.maven.plugins");
			plugin.setArtifactId("maven-compiler-plugin");
			model.getBuild().addPlugin(plugin);
		}
		//Change plugin configuarion
		Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
		if (configuration == null)
			throw new CompilerException("error.compiler.maven.pomCompilerPluginConfig");
		Xpp3Dom compArgs = configuration.getChild("compilerArgs");
		if (compArgs == null) {
			compArgs = new Xpp3Dom("compilerArgs");
			configuration.addChild(compArgs);
		}
		//Xplugin argument
		addArg(compArgs, String.format(PLUGIN_ARG, basepath + DB_PATH));
		plugin.setConfiguration(configuration);
	}
	
	private void addArg(Xpp3Dom compArgs, String argument) {
		Xpp3Dom arg = new Xpp3Dom("arg");
		arg.setValue(argument);
		compArgs.addChild(arg);
	}
	
}
