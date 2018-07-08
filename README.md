# sql2cvs
Java Tool to dump via simple SQL query the result to CSV

## Examples
This tool can be used via command line as follows

### Example 1 - dump a simple table via command line
```
sql2csv  -url jdbc:mysql://localhost:3306/currencies -user root -query "select * from currencies" -output currencies.csv
```

## Installation & Configuration

The deployment package can be built with maven and it will create automatically shell scripts .sh / .bat files for you   

## FAQ 
### Can I re-use your classes  within my java applicaiton? If yes how? 
