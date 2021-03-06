# sql2cvs
Java Tool to dump the result of SQL queries to CSV. It can be used also for large queries with millions of result rows. 

## Examples
This tool can be used via command line as follows:

### Example 1 - dump a simple table via command line by using JDBC auto driver detection

Remark: As a prerequisite please make sure that your JDBC driver jar has been copied to the current working directory
```
sql2csv -url jdbc:mysql://localhost:3306/currencies -user root -password xxx -query "select * from currency" -output currencies.csv
```
This would produce for example the following output
```
"id","code","name","last_modification"
"1","CHF","Swiss Franc","08/07/2018 23:10:46"
"2","USD","US Dollar","08/07/2018 23:10:46"
```
The tool automatically tries to locate the JDBC driver jar in the current working directory and uses automatic JDBC driver detection. If it fails to find a suitable driver then you should:
* specify with the command line property `-driverDir`the location of the JDBC driver jars, this is the path to the directory which contains the jars
* specify with the command line property `-driver`the full name of your JDBC driver class i.e. com.mysql.jdbc.Driver

### Example 2 - dump a simple table via command line by passing JDBC driver details
```
sql2csv -driverDir c:\jdbcLibs -driver com.mysql.jdbc.Driver -url jdbc:mysql://localhost:3306/currencies -user root -password xxx -query "select * from currency" -output currencies.csv
```
### Example 3 - dump a simple table via command line by passing seperator and quote char
```
sql2csv -url jdbc:mysql://localhost:3306/currencies -user root -password xxx -query "select * from currency" -output currencies.csv -seperator ";" -quotechar "'"  
```

### Example 4 - dump a mutliple tables via command line by using a property file
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

currencies1.csv=select * from currency order by id asc
currencies2.csv=select * from currency order by id desc

```
## Command Line Reference
```
usage: SQL2CSV [OPTIONS]
 -dateFormat <arg>       date format to be used
 -dateTimeFormat <arg>   date time format to be used
 -driver <arg>           JDBC driver class to be used
 -driverDir <arg>        directory path for JDBC driver jars
 -enterPassword          enter the password via console
 -header <arg>           column names as header row (default yes)
 -help                   prints help description
 -output <arg>           path to output file
 -password <arg>         password for login
 -propertyFile <arg>     path to property file
 -query <arg>            sql query to be executed
 -quotechar <arg>        char used for escaping values, by default a double quote
 -quotes <arg>           apply quotes (default yes)
 -seperator <arg>        char for the field separation, by default a comma
 -sql <arg>              sql query to be executed
 -trim <arg>             trim values (default false
 -url <arg>              JDBC URL to be used
 -user <arg>             user name for login

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
The minimum configuration is to pass the `-url` for the JDBC string and the query and user name via command line properties or via property file. Don't forget user and password parameters. Please have a look at the examples above for more information and read also the FAQ.

## FAQ 
### Can I omit the column headers? If yes how?
Yes you can! Simple pass the property `-header no`

### Can I deactivate the field double quote encoding ""?
Yes you can! Simple pass the property `-quotes no`

### Can the values be trimmed?
Yes you can! Simple pass the property `-trim yes`

### Can I specify another delimiter for the field seperation and its value escaping?
Yes you can! Simple pass the property `-seperator ";"` and `-quotechar "'"`. See example 3 for a reference.

### Can I use another line end seperator?
Yes you can, however for this you need to modify the generated shell script and add the proper system property of Java `-Dline.seperator=\n`

### Can I use that to dump large tables with over 100'000 entries or even more?
Yes you can!  I programmed it exactyl for such a purpose. The tool is using streams and iterates through the resultset and does not store them in the memory. So memory consumption is quite small.

### Can I mix command line and property file? what is the order?
Yes You can! The property file has the lowest priority. The lookup for the porperties are as follows
1. property file (lowest priority)
2. command line properties
3. system properties (highest priority)

### Can I execute multiple queries and save them as separate output?
Yes you can, see example 4

### Can I execute multiple queries and save them as single output appended?
Well for this you need to use union queries and build the query as single line

### Can I enter the password at runtime so that nobody sees it in the console?
Yes you can, simple replace the property `-password xxx` by using `-enterPassword` (no value for that property needed)

### Can I change the date and timestamp format settings?
Yes you can! Simple pass the parameter `-dateFormat dd/MM/yyyy` or `-dateTimeFormat dd/MM/yyyy HH:mm:ss` with the format according to the SimpleDateFormat pattern from JVM. The patterns "dd/MM/YYYY" and "dd/MM/YYYY HH:mm:ss" are the default ones.

### Can I reuse your classes within my java application? If yes how? 
Yes you can! Simple use the class `SQL2CSV`as follows or have a look at the Junit Test `SQL2CSVTest`for a running example.

```
Class.forName ("org.h2.Driver");  // your JDB driver
Connection con = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","sa");  // your JDBC connection string with user and password
StringWriter sw = new StringWriter();

SQL2CSV sqlToCsv = new SQL2CSV();
sqlToCsv.writeCSV(con, "select * from currency order by id", sw, true, false, true, "dd/MM/YYYY", "dd/MM/YYYY HH:mm:ss");

con.close();
System.out.print(sw);

```
### Do you have somewhere the example "currencies" schema with example data to test?
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
