package com.ontius.jdbc;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.ResultSetHelperService;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SQL2CSV {
	
	static final String DEFAULT_DATE_FORMAT = "dd/MM/YYYY";
	static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/YYYY HH:mm:ss";
	
	@Setter
	private String dateFormat = DEFAULT_DATE_FORMAT;
	
	@Setter
	private String dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;

	@Setter
	private  boolean header = true; 
	
	@Setter
	private	boolean trim = false;
	
	@Setter
	private	boolean applyQuotes = true;
	
	@Setter
	private	char seperatorchar = ICSVWriter.DEFAULT_SEPARATOR;
	
	@Setter
	private	char quotechar = ICSVWriter.DEFAULT_QUOTE_CHARACTER;
	
	@Setter
	private	char escapechar = ICSVWriter.NO_ESCAPE_CHARACTER; 
	
	@Setter
	private	String lineSepaerator = System.getProperty("line.separator"); 
	
	
	public void writeCSV(Connection connection, String sql, Writer writer) throws Exception {
		writeCSV(connection, sql, writer, header, trim, applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar , escapechar, lineSepaerator);
	}

	public void writeCSV(Connection connection, String sql, Writer writer, boolean header) throws Exception {
		writeCSV(connection,sql,writer,header, trim, applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim) throws Exception {
		writeCSV(connection,sql,writer,header,trim, applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}

	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}

	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat, char seperatorchar) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat, char seperatorchar, char quotechar) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat, char seperatorchar, char quotechar, char escapechar) throws Exception {
		writeCSV(connection,sql,writer,header,trim,applyQuotes, dateFormat, dateTimeFormat, seperatorchar, quotechar, escapechar, lineSepaerator);
	}
	
	public void writeCSV(Connection connection, String sql, Writer writer, boolean header, boolean trim, boolean applyQuotes, String dateFormat, String dateTimeFormat, char seperatorchar, char quotechar, char escapechar, String lineSepaerator) throws Exception {
		
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
		    csvw = new CSVWriter(writer,seperatorchar,quotechar,escapechar,lineSepaerator);
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
}
