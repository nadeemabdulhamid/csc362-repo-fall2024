> **CSC 362 • Database Systems • Fall 2024**
# Lecture 2 - JDBC

## Questions to be answered:
1. What is JDBC?
2. What are the steps of using JDBC to connect and query a database?
3. What are transactions and isolation levels?
4. Why would you not want to auto-commit?
5. What are prepared statements?
6. When should computations be coded by the programmer vs expressed in SQL?


## JDBC

- "JDBC" - Java Database Connectivity
    - a Java API for connecting to relational databases (or any kind of tabular data)

- Helps manage these three programming activities:
    - Connect to a data source, like a database
    - Send queries and update statements to the database
    - Retrieve and process the results


### API Summary - Basic JDBC

[Fig 2.1](./fig-2-1.pdf)


### Examples

Connect and disconnect:

```
String url = "jdbc:derby://localhost/testdb;create=true";
Driver d = new ClientDriver();
Connection conn = d.connect(url, null);
...
conn.close();
```

* A *Driver* object knows how to connect to a particular database engine
    - each engine will have its own way to connect, and the driver encapsulates this so that the client doesn't need to know it
    - simply has the method `connect()`, which returns a connection to the server

* A *Connection* object manages a user session with the server
    - has methods for interacting with the server
    - `close()` logs out of the session and disconnects from the server

* Notes:
    - necessary `import`s 
    - Many JDBC methods throw a `SQLException` on error - *checked* exception (must be handled). But have to ensure that resources are closed. (Show with try/catch) Cleanest way is to use improved *try-with-resources* statement. (https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html)

```
import java.sql.*;
import org.apache.derby.jdbc.ClientDriver;

public class CreateTestDB {
   public static void main(String[] args) {
      String url = "jdbc:derby://localhost/testdb;create=true";

      Driver d = new ClientDriver();
      try (Connection conn = d.connect(url, null)) {
         System.out.println("Database Created");
      }
      catch(SQLException e) {
         e.printStackTrace();
      }
   }
}
```

### Processing Statements

In general, to process any SQL statement with JDBC, you follow these steps:

1. Establishing a connection.
2. Create a statement.
3. Execute the query.
4. Process the ResultSet object.
5. Close the connection.


> A **`Statement`** is an interface that represents a SQL statement. You execute Statement objects, and they generate ResultSet objects, which is a table of data representing a database result set. You need a Connection object to create a Statement object.


```
String url = "jdbc:derby://localhost/studentdb";
String qry = "select SName, DName from DEPT, STUDENT where MajorId = DId";

Driver d = new ClientDriver();
try ( Connection conn = d.connect(url, null);   // 1.
      Statement stmt = conn.createStatement();    // 2.
      ResultSet rs = stmt.executeQuery(qry)) {    // 3.
    System.out.println("Name\tMajor");
    while (rs.next()) {                         // 4.
        String sname = rs.getString("SName");       // .
        String dname = rs.getString("DName");       // .
        System.out.println(sname + "\t" + dname);
    }
    printSchema(rs);
}                                                 // 5. (auto-closed)
catch(SQLException e) {
    e.printStackTrace();
}
```


Two methods for executing statements: (ask chatgpt:)
> What's the difference between executeUpdate and executeQuery in JDBC?


### `ResultSet`

A ResultSet in JDBC (Java Database Connectivity) is an object that holds the data retrieved from a database after executing a SQL query

> Go over:
> - `StudentMajor.java`
> - `FindMajors.java` (constructing query dynamically from input)



### Quiz - Basic
https://quizizz.com/admin/quiz/66a56509ff6f2a77f45ebc64



## Using Transactions

> A transaction is a set of one or more statements that is executed as a unit, so either all of the statements are executed, or none of the statements is executed.

Why?
- There are times when you do not want one statement to take effect unless another one completes. 

Examples:
- Fig 2.10
- Fig 2.11

If multiple modifications are supposed to happen together, problems can arise in the context of **concurrency**.

Examples:
- Fig 2.11 again (reading uncommitted data)
- Fig 2.14 (unexpected changes to existing record) (non-repeatable read)
- Fig 2.15 (changes to *number* of records) (phantom records)

### Transaction Levels

- read-uncommitted
- read-committed
- repeatable-read
- serializable


## Computing in Java vs. SQL

- It is always possible to write a client that just retrieves the entire database, and then write code to do what a complex SQL query might do.

- Example: Finding the students graduating in 2024
```
String s = "select sname from student where gradyear=2024";
ResultSet rs = stmt.executeQuery(s);
while (rs.next())
    System.out.println(rs.getString("sname"));
rs.close();
```
vs.
```
String s = "select sname, gradyear from student";
ResultSet rs = stmt.executeQuery(s);
while (rs.next()) {
    if (rs.getInt("gradyear") == 2024)
        System.out.println(rs.getString("sname"));
}
rs.close();
```

- Rule of thumb:  Do as much in the server as possible.
    - Reason 1:  Less network traffic
    - Reason 2:  Server knows how to optimize queries, so it is almost always faster. 



Contrast Fig 2.5 and 2.22. 
([StudentMajor.java](https://github.com/nadeemabdulhamid/csc362-simpledb-fall24/blob/main/src/derbyclient/StudentMajor.java) vs [BadStudentMajor](https://github.com/nadeemabdulhamid/csc362-simpledb-fall24/blob/main/src/derbyclient/BadStudentMajor.java), or [CleverFindMajors](https://github.com/nadeemabdulhamid/csc362-simpledb-fall24/blob/main/src/derbyclient/CleverFindMajors.java))
> The database system's query optimizer is probably going to figure out the same way (or a better way) of doing it.

- In general, always trust the database optimizer to do the right thing.
    - That is, let the server do as much as possible.


## PreparedStatements
https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html

- you can use the same statement and supply it with different values each time you execute it
- the SQL statement is sent to the database engine and precompiled so it is ready to execute efficiently many times over
- help prevent SQL injection attacks

> SQL injection is a technique to maliciously exploit applications that use client-supplied data in SQL statements. Attackers trick the SQL engine into executing unintended commands by supplying specially crafted string input, thereby gaining unauthorized access to a database to view or manipulate restricted data. SQL injection techniques all exploit a single vulnerability in the application: Incorrectly validated or nonvalidated string literals are concatenated into a dynamically built SQL statement and interpreted as **code** by the SQL engine. Prepared statements always treat client-supplied **data** as content of a parameter and never as a part of an SQL statement. 

- Demo: Run `FindMajors.java` with:   `'or''='`


### Quiz - advanced
https://quizizz.com/admin/quiz/66a5667c57768a08c9fdb631
