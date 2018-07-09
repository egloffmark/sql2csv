package com.ontius.jdbc;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelperService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SQL2CSV {
	
	static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/YYYY HH:mm:ss";
	static final String DEFAULT_DATE_FORMAT = "dd/MM/YYYY";
	
	
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
}
