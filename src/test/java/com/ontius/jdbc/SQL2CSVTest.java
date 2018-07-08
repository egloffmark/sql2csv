package com.ontius.jdbc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.extern.log4j.Log4j2;

@Log4j2
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SQL2CSVTest {
	
	static Server server;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:test;MODE=mysql");
		ds.setUser("sa");
		ds.setPassword("sa");
		InputStream is = SQL2CSVTest.class.getClassLoader().getResourceAsStream("schema.sql");
		RunScript.execute(ds.getConnection(), new InputStreamReader(is));
		server = Server.createWebServer("-webAllowOthers", "-webPort","8885");
		server.start();
	}
	
	@Test
	public void test1CheckDB() throws Exception {
		log.info("test1CheckDB...");
		Class.forName ("org.h2.Driver"); 
		Connection con = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","sa"); 
		Statement stmt = con.createStatement(); 
		ResultSet rs = stmt.executeQuery("select * from currency order by id");
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			String name = rs.getString("name");
			String code = rs.getString("code");
			log.info("currency code:{} name:{}", code,name);
			list.add(code);
		}
		
		assertArrayEquals("test result", new String[] {"CHF","USD"}, list.toArray());
		
		con.close();
	}
	
	@Test
	public void test2SQLToCSV() throws Exception {
		
		log.info("test2SQLToCSV...");
		Class.forName ("org.h2.Driver"); 
		Connection con = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","sa"); 
		StringWriter sw = new StringWriter();
		SQL2CSV sqlToCsv = new SQL2CSV();
		sqlToCsv.writeCSV(con, "select * from currency order by id", sw, true, false, true, "dd/MM/YYYY", "dd/MM/YYYY HH:mm:ss");
		con.close();
		System.out.print(sw);
		// test the returned lines
		BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
		String header = br.readLine();
		assertNotNull(header);
		assertEquals("test header row", "\"ID\",\"CODE\",\"NAME\",\"LAST_MODIFICATION\"",header);
		String line2 = br.readLine();
		assertNotNull(line2);
		assertTrue("test line with regexp",  line2.matches("^\\\"1\\\",\\\"CHF\\\",\\\".+\\\"$"));
		String line3 = br.readLine();
		assertNotNull(line3);
		assertTrue("test line with regexp",  line3.matches("^\\\"2\\\",\\\"USD\\\",\\\".+\\\"$"));
		String line4 = br.readLine();
		assertNull(line4);
	}
	

	@AfterClass
	public static void shutdownAfterClass() throws Exception {
		if(server != null && server.isRunning(false)) {
			server.stop();
		}
	}

}
