package com.ontius.jdbc;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.commons.cli.ParseException;

import com.ontius.common.util.cli.CommandLineHelper;
import com.ontius.jdbc.SQL2CSVPropertyExtended.Property;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SQL2CSVMain {

	private static final String PROP_FILE = "propertyFile";
	private static final String PROP_HELP = "help";

	private static final String PROP_ENTER_PASSWORD = "enterPassword";

	public static void main(String[] args) {
		
		try {
		
			CommandLineHelper cmdLineHelper = new CommandLineHelper();
			for(Property prop : Property.values()) {
				cmdLineHelper.addCmdLineOption(prop.getName(), true, prop.getDescription());
			}
			cmdLineHelper.addCmdLineOption(PROP_FILE, true, "path to property file");
			cmdLineHelper.addCmdLineOption(PROP_ENTER_PASSWORD, false, "enter the password via console");
	
			cmdLineHelper.parseCmdLine(args);
			
			if (cmdLineHelper.hasOption(PROP_HELP)) {
				cmdLineHelper.printUsageDescription("SQL2CSV", "", "(c) by Ontius (www.ontius.com)\n");
				System.exit(0);
			}
			
			Properties properties = new Properties();
			
			if(cmdLineHelper.hasOption(PROP_FILE)) {
				properties.load(new FileReader(new File(cmdLineHelper.getValue(PROP_FILE))));
			}
	
			for(Property prop : Property.values()) {
				if (cmdLineHelper.hasOption(prop.getName())) {
					properties.put(prop.getName(), cmdLineHelper.getValue(prop.getName()));
				}
			}
	
			for(Property prop : Property.values()) {
				if (System.getProperties().containsKey(prop.getName())) {
					properties.put(prop.getName(), System.getProperties().get(prop.getName()));
				}
			}
			
			String driverDir = ".";
			if (properties.containsKey(Property.DRIVER_DIR.getName())) {
				driverDir = properties.getProperty(Property.DRIVER_DIR.getName());
			}
			File[] jars = readJarFiles(driverDir);
			for (File jar : jars) {
				addLibraryToClassPath(jar);
			}

			
			if(cmdLineHelper.hasOption(PROP_ENTER_PASSWORD)) {
				String password = cmdLineHelper.readPassword("enter password:");
				properties.put(Property.PASSWORD.getName(), password);
			}
			
			SQL2CSVPropertyExtended sqlToCsv = new SQL2CSVPropertyExtended();
			sqlToCsv.writeCSV(properties);
			
			log.info("finished!");
			
		} catch(ParseException ex) {
			log.error("error " + ex.getMessage() + ". Please use -help for a reference about the command line syntax.");
		} 
		catch(Exception ex) {
			log.error("error has happened!", ex);
		}
	}

	private static File[] readJarFiles(String directoryPath) throws Exception {
		File dir = new File(directoryPath);
		File[] jars = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
			
		});
		return jars;
	}
	
	private static void addLibraryToClassPath(File file) throws Exception  {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	}
}
