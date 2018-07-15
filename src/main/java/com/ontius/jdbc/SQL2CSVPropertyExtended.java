package com.ontius.jdbc;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TreeSet;

import com.opencsv.ICSVWriter;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SQL2CSVPropertyExtended extends SQL2CSV {
	
	
	public void writeCSV(Properties properties) throws Exception {
		
		Connection connection = null;
		
		try {

			for(Property prop : Property.values()) {
				if (prop.isMandatory() && !properties.containsKey(prop.getName())) {
					throw new IllegalArgumentException("mandatory parameter " + prop.getName() + " is missing!");
				}
			}
			if (properties.containsKey(Property.DRIVER_CLASS.getName())) {
				Class.forName(properties.getProperty(Property.DRIVER_CLASS.getName()));
			}
		    String url = properties.getProperty(Property.JDBC_URL.getName());
		    String user = properties.getProperty(Property.USER.getName());
		    String password = properties.getProperty(Property.PASSWORD.getName());
		    
		    String sql = "";
		    if (properties.containsKey(Property.SQL.getName())) {
		    	sql = properties.getProperty(Property.SQL.getName());
		    }
		    if (properties.containsKey(Property.QUERY.getName())) {
		    	sql = properties.getProperty(Property.QUERY.getName());
		    }
		    String output = properties.getProperty(Property.OUTPUT.getName(),"output.csv");
		    boolean header = "yes".equals(properties.getProperty(Property.HEADER.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.HEADER.getName(),"true").toLowerCase());
		    boolean trim = "yes".equals(properties.getProperty(Property.TRIM.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.TRIM.getName(),"true").toLowerCase());
		    boolean quotes = "yes".equals(properties.getProperty(Property.QUOTES.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.QUOTES.getName(),"true").toLowerCase());
		    String dateFormat = properties.getProperty(Property.DATE_FORMAT.getName(),DEFAULT_DATE_FORMAT);
		    String dateTimeFormat = properties.getProperty(Property.DATE_TIME_FORMAT.getName(),DEFAULT_DATE_TIME_FORMAT);
			char seperatorchar = properties.getProperty(Property.SEPERATOR.getName(),String.valueOf(ICSVWriter.DEFAULT_SEPARATOR)).charAt(0);
			char quotechar = properties.getProperty(Property.QUOTE_CHAR.getName(),String.valueOf(ICSVWriter.DEFAULT_QUOTE_CHARACTER)).charAt(0);
		   
			log.info("open connection...");
			if(user != null && !user.isEmpty()) {
				connection = DriverManager.getConnection(url, user, password);
			} else {
				connection = DriverManager.getConnection(url);
			}
		
			if (sql != null && !sql.isEmpty()) {
				FileWriter writer = new FileWriter(new File(output));
				log.info("start csv dump to {}...",output);
				writeCSV(connection, sql, writer, header, trim, quotes, dateFormat, dateTimeFormat, seperatorchar, quotechar);
			}
			for(Property prop : Property.values()) {
					properties.remove(prop.getName());
			}
			if (!properties.isEmpty()) {
				for(Object key : new TreeSet<Object>(properties.keySet())) {
					String query = properties.getProperty(key.toString());
					String out = key.toString();
					FileWriter writer = new FileWriter(new File(out));
					log.info("start csv dump to {}...",out);
					writeCSV(connection, query, writer, header, trim, quotes, dateFormat, dateTimeFormat, seperatorchar, quotechar);
				}
			}
		}
		finally {
							
				try {
					if (connection != null && !connection.isClosed()) connection.close();
				} catch (SQLException e) {
					log.error("error has happened!", e);
				}
		}
	}

	
	public static enum Property {
		
		JDBC_URL("url", "JDBC URL to be used", true),
		DRIVER_CLASS("driver", "JDBC driver class to be used"),
		DRIVER_DIR("driverDir","directory path for JDBC driver jars"),
		SQL("sql","sql query to be executed"),
		QUERY("query","sql query to be executed"),
		DATE_FORMAT("dateFormat","date format to be used"),
		DATE_TIME_FORMAT("dateTimeFormat","date time format to be used"),
		USER("user", "user name for login"),
		PASSWORD("password", "password for login"),
		OUTPUT("output","path to output file"),
		HEADER("header","column names as header row (default yes)"),
		TRIM("trim", "trim values (default false"),
		QUOTES("quotes","apply quotes (default yes)"),
		SEPERATOR("seperator","char for the field separation, by default a comma"),
		QUOTE_CHAR("quotechar", "char used for escaping values, by default a double quote")
		;
		
		  @Getter
		  private final String name;
		  @Getter
		  private final String description;    
		  @Getter
		  private final boolean mandatory;
		  
		  Property(String name, String descr) {
			  this(name,descr,false);
		  }

		  Property(String name, String descr, boolean mandatory) {
			  this.name = name;
			  this.description = descr;
			  this.mandatory = mandatory;
		  }	  
	}
}
