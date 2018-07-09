# sql2cvs
Java Tool to dump via simple SQL query the result to CSV. It can be sued fro large tables with millions of entries. 

## Examples
This tool can be used via command line as follows

### Example 1 - dump a simple table via command line by using JDBC auto driver detection

As a prerequisite please make sure that your JDBC driver jar has been copied to the current working directory
```
sql2csv  -url jdbc:mysql://localhost:3306/currencies -user root -query "select * from currency" -output currencies.csv
```
This would produce for example the following output
```
"id","code","name","last_modification"
"1","CHF","Swiss Franc","08/07/2018 23:10:46"
"2","USD","US Dollar","08/07/2018 23:10:46"
```
The tool automatically tries to locate the JDBC driver ja in the current working directory and uses  automatic JDBC driver detection. If it fails then you should:
* specify with the command line property `driverDir`the location of the JDBC driver jars
* specify with the command line property `driver`the full name of your JDBC driver class

### Example 2 - dump a simple table via command line by passing JDBC driver details
```
sql2csv -driverDir c:\jdbcLibs -driver com.mysql.jdbc.Driver -url jdbc:mysql://localhost:3306/currencies -user root -query "select * from currency" -output currencies.csv
```

### Example 3 - dump a mutliple tables via command line by using a property file
You can pass the command line properties via property file. The property file allows you to specify mutliple queries for the same connection by using the pattern

`<outputpath> = <sql query>`

An example would look like this:

```
sql2csv -propertyFile conf.properties
```

example conf.properties:

```
driverDir=.
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/currencies
user=root

currencies1.csv=select * from currencies order by id asc
currencies2.csv=select * from currencies order by id desc

```

## Build, Installation & Configuration

### Building from source 
The deployment package can be built with maven and it will create automatically shell scripts .sh / .bat files for you. The following commands generates in the `target/sql2csv` folder a runnable tool with the shellscripts for linux and windows platforms. 
```
export JAVA_HOME=/opt/java/jdk1.8.0_152
cd sql2csv
mvn clean package
cd target/sql2csv
```
### Installation
Just copy it where you like to have. The only pre-requisite before you execute is that you make sure that you copy the JDBC driver jar(s) into the current working directory

### Configuration
The minimum configuration is to pass the `-url` for the JDBC string. Don't forget user and password parameters. Please have a look at the examples above for more information and read also the FAQ.

## FAQ 
### Can I omit the column headers? If yes how?
Yes you can! Simple pass the property `-header no`

### Can I deactivate the field doulbe quote encoding ""?
Yes you can! Simple pass the property `-quotes no`

### Can the values be trimmed?
Yes you can! Simple pass the property `-trim yes`

### Can I specify another delimiter?
No this feature has not been foreseen, comma is currently hardcoded

### Can I sue that to dump large tables with over 100'000 entries or even more?
Yes you can!  I programmed it exactyl for such a purpose. The tool is using streams and iterates through the resultset and does not store them in the memory. So memory consumption is quite small.

### Can I mix command line and property file? what is the order?
Yes You can! The property file has the lowest priority. The lookup for the porperties are as follows
1. property file (lowest priority)
2. command line properties
3. system properties (highest priority)

### Can I execute multiple queries and save them as separate output?
Yes you can, see example 3

### Can I execute multiple queries and save them as single output appended?
Well for this you need to use union queries and build the query as single line

### Can I re-use your classes within my java application? If yes how? 
Yes you can! Simple use the class `SQL2CSV`as follows or have a look at the Junit Test `SQL2CSVTest`for a running example.

```
Class.forName ("org.h2.Driver");  // your JDB driver
Connection con = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","sa");  // your JBS connection string with user and password
StringWriter sw = new StringWriter();

SQL2CSV sqlToCsv = new SQL2CSV();
sqlToCsv.writeCSV(con, "select * from currency order by id", sw, true, false, true, "dd/MM/YYYY", "dd/MM/YYYY HH:mm:ss");

con.close();
System.out.print(sw);

```
### Do you have somewhere the xample "currencies" schema with example data to test?
Yes please have a look at the `src/test/resources/schema.sql`and the running JUnit example `SQL2CSVTest` which uses a H2 in-memory DB.

### Which libraries is this tool using? 
The following libraries / open source projects are used
* apache commons beanutils
* apache commons cli
* apache commons collections
* apache commons lang
* apache commons logging
* apache commons text
* apache log4j
* opencsv
* lombok (only for compilation)
