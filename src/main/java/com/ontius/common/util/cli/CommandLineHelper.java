package com.ontius.common.util.cli;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lombok.AccessLevel;
import lombok.Setter;

public class CommandLineHelper {

	@Setter(AccessLevel.PROTECTED)
	private Options options = new Options();
	private CommandLine cmdLine;
	
	public void parseCmdLine(String[] args) throws ParseException {
		if (!options.hasOption("help")) {
			options.addOption("help","prints help description");
		}
		CommandLineParser clp = new DefaultParser();
		cmdLine = clp.parse(options, args); 
	}
	
	public void addCmdLineOption(Option opt) {
		options.addOption(opt);
	}

	public void addCmdLineOption(String opt) {
		options.addOption(Option.builder(opt).build());
	}
	
	public void addCmdLineOption(String opt, String description) {
		options.addOption(opt, description);
	}

	public void addCmdLineOption(String opt, boolean hasArg, String description) {
		options.addOption(opt, hasArg, description);
	}

	public void addCmdLineListOption(String opt, String description) {
		addCmdLineListOption(opt, ',', description);
	}
	
	public void addCmdLineListOption(String opt, char delimiter, String description) {
		options.addOption(Option.builder(opt).argName("a,b,c").hasArgs().valueSeparator(delimiter).desc(description).build());
	}
	
	public void printUsageDescription(String appName, String header, String footer) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(appName + " [OPTIONS]", header, options, footer);
	}
	
	private void verifyState() {
		if (cmdLine == null) {
			throw new IllegalStateException("please call first 'parseCmdLine()' in order to initialize this helper properly");
		}
	}
	
	public List<String> getDefinedOptions() {
		Collection<Option> opts = options.getOptions();
		ArrayList<String> list = new ArrayList<String>();
		for(Option opt : opts) {
			list.add(opt.getOpt());
		}
		return list;
	}
	
	public List<String> getOptions() {
		Option[] opts = cmdLine.getOptions();
		ArrayList<String> list = new ArrayList<String>();
		for(Option opt : opts) {
			list.add(opt.getOpt());
		}
		return list;
	}
	
	public boolean hasOptions() {
		verifyState();
		Option[] opt = cmdLine.getOptions();
		return opt != null && opt.length > 0;
	} 
	
	public boolean hasOption(String opt) {
		verifyState();
		return cmdLine.hasOption(opt);
	}

	public String getValue(String opt) {
		return getValue(opt,null);
	}
	
	public String getValue(String opt, String defaultValue) {
		return cmdLine.getOptionValue(opt,defaultValue);
	}
	
	public List<String> getValues(String opt) {
		return getValues(opt, new ArrayList<String>());
	}

	public List<String> getValues(String opt, String... defaultValues) {
		return getValues(opt, Arrays.asList(defaultValues));
	}
	
	public List<String> getValues(String opt, List<String> defaultValues) {
		if (hasOption(opt)) {
			return Arrays.asList(cmdLine.getOptionValues(opt));
		}
		return null;
	}
	
	public Properties getPropertyValues(String opt) {
		return getPropertyValues(opt, new Properties());
	}

	public Properties getPropertyValues(String opt, Properties defaultValues) {
		if (hasOption(opt)) {
			return cmdLine.getOptionProperties(opt);
		}
		return defaultValues;
	}
	
	public String readPassword(String prompt) throws IOException {
		System.out.print(prompt);
		Console console = System.console();
		if (console != null) {
			return new String(console.readPassword());
		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		    return reader.readLine();
		}
	}
	
	public String readValue(String prompt) throws IOException {
		System.out.print(prompt);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    return reader.readLine();
	}
}
