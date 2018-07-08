# sql2cvs
Java Tool to dump via simple SQL query the result to CSV

## Examples
This tool can be used via command line as follows

### Example 1 - dump a simple table via command line
```
sql2csv  -url jdbc:mysql://localhost:3306/currencies -user root -query "select * from currency" -output currencies.csv
```
This would produce for example the following output
```
"id","code","name","last_modification"
"1","CHF","Swiss Franc","08/07/2018 23:10:46"
"2","USD","US Dollar","08/07/2018 23:10:46"
```

## Installation & Configuration

The deployment package can be built with maven and it will create automatically shell scripts .sh / .bat files for you. The following commands generates in the `target/sql2csv` folder a runnable tool with the shellscripts for linux and windows platforms. 
```
export JAVA_HOME=/opt/java/jdk1.8.0_152
cd sql2csv
mvn clean package
```
To run it
```
cd target\sql2csv
sql2csv -version
```

## FAQ 
### Can I re-use your classes  within my java applicaiton? If yes how? 

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
