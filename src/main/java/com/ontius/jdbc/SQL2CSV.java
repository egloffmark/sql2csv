package com.ontius.jdbc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.TreeSet;

import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelperService;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SQL2CSV {
	
	private static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/YYYY HH:mm:ss";
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/YYYY";
	
	
	public void writeCSV(Connection connection, String sql, Writer writer) throws Exception {
		writeCSV(connection,sql,writer,true,false,true,DEFAULT_DATE_FORMAT,DEFAULT_DATE_TIME_FORMAT);
	}

	public void writeCSV(Connection connection, String sql, Writer writer, boolean header) throws Exception {
		writeCSV(connection,sql,writer,header,false,true,DEFAULT_DATE_FORMAT,DEFAULT_DATE_TIME_FORMAT);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim) throws Exception {
		writeCSV(connection,sql,writer,header,trim,true,DEFAULT_DATE_FORMAT,DEFAULT_DATE_TIME_FORMAT);
	}

	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes,DEFAULT_DATE_FORMAT,DEFAULT_DATE_TIME_FORMAT);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat) throws Exception {
		
		Statement stmt = null;
		ResultSet rs = null;

	    CSVWriter csvw = null;
	    
	    ResultSetHelperService resultSetHelperService = new ResultSetHelperService();
	    resultSetHelperService.setDateFormat(dateFormat);
	    resultSetHelperService.setDateTimeFormat(dateTimeFormat);
	    
	    try {
		    stmt = connection.createStatement();
			log.info("executing query '{}'",sql);
		    rs = stmt.executeQuery(sql);
		    csvw = new CSVWriter(writer);
			csvw.setResultService(resultSetHelperService);
			log.info("writing output...");
		    int linesWritten = csvw.writeAll(rs, header, trim, applyQuotes);
		    log.info("{} lines written...",linesWritten);

	    } finally {
			try {
				if (csvw != null) {
					csvw.close();
				}
			} catch (IOException e) {
				log.error("error has happened!", e);
			}
			
			try {
				if (rs != null) rs.close();
			} catch (SQLException e) {
				log.error("error has happened!", e);
			}

			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				log.error("error has happened!", e);
			}
	    }
	}
	
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
		    String sql = properties.getProperty(Property.SQL.getName());
		    String output = properties.getProperty(Property.OUTPUT.getName(),"output.csv");
		    boolean header = "yes".equals(properties.getProperty(Property.HEADER.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.HEADER.getName(),"true").toLowerCase());
		    boolean trim = "yes".equals(properties.getProperty(Property.TRIM.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.TRIM.getName(),"true").toLowerCase());
		    boolean quotes = "yes".equals(properties.getProperty(Property.QUOTES.getName(),"yes").toLowerCase()) || Boolean.valueOf(properties.getProperty(Property.QUOTES.getName(),"true").toLowerCase());
		    String dateFormat = properties.getProperty(Property.DATE_FORMAT.getName(),DEFAULT_DATE_FORMAT);
		    String dateTimeFormat = properties.getProperty(Property.DATE_TIME_FORMAT.getName(),DEFAULT_DATE_TIME_FORMAT);
		   
			log.info("open connection...");
			if(user != null && !user.isEmpty()) {
				connection = DriverManager.getConnection(url, user, password);
			} else {
				connection = DriverManager.getConnection(url);
			}
		
			if (sql != null && !sql.isEmpty()) {
				FileWriter writer = new FileWriter(new File(output));
				log.info("start csv dump to {}...",output);
				writeCSV(connection, sql, writer, header, trim, quotes, dateFormat, dateTimeFormat);
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
					writeCSV(connection, query, writer, header, trim, quotes, dateFormat, dateTimeFormat);
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
		DATE_FORMAT("dateFormat","date format to be used"),
		DATE_TIME_FORMAT("dateTimeFormat","date time format to be used"),
		USER("user", "user name for login"),
		PASSWORD("password", "password for login"),
		OUTPUT("output","path to output file"),
		HEADER("header","column names as header row (default yes)"),
		TRIM("trim", "trim values (default false"),
		QUOTES("quotes","apply quotes (default yes)")
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
