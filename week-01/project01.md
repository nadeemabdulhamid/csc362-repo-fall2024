> **CSC 362 • Database Systems • Fall 2024**
# Project 1: JDBC Programming

- On this and all of your projects that modify SimpleDB internals, you may make use of your local Git repository of the SimpleDB code to save snapshots of its state in case you need to refer back to or restore a previous working version if you end up in a state where you cannot debug your code at all. Right-click on the project in Eclipse, and use the "Team" submenu to manage your Git repository.
- Any modified code files that you submit for this and all projects (e.g. the `SimpleIJ.java` program) should only contain modifications that are related to the tasks of this assignment. Portions of the code that are completely unrelated to any part of this assignment should not be modified or rewritten in any way. A [Diff tool](https://www.diffchecker.com/text-compare/) may be used to ensure that all modifications to code are relevant and on task. 
- Remember that if you adopt/adapt code from a generative AI source (e.g. ChatGPT) you are entirely responsible for ensuring the correctness of the code, and you must put a comment attributing to the source (e.g. “ChatGPT”) and the prompt used (e.g. “Refactor the main method so that the bulk of the code that processes a command is moved to a separate method that simple takes a command string and processes it.”). 


## Part 1: Simple JDBC client

Write a JDBC program named `SectionInfo.java`. Your program should make a ***network*** connection to a SimpleDB engine that holds the university database of Figure 1.1. It should request a section number from the user and then print the professor of the section, the number of students in the section, and the number of “A” grades given. For example:

```
Enter a section number:  43
Professor einstein had 2 students and gave 0 A's.
```

If there is no section with that number, it should print "No such section". For example:

```
Enter a section number:  1234
No such section.
```

Your program ***must*** execute two SQL queries: one that retrieves the professor for that section from the `SECTION` table, and one that retrieves the grades for that section from the `ENROLL` table. Note that SimpleDB's version of SQL cannot perform calculations, so your Java code will need to do all the necessary arithmetic.

Those of you with experience with SQL may realize that it is possible to solve the
problem using a single multi-table query. However, I do not want you to do it this way, because I want you to experience writing a program that uses multiple result sets. Please use two single-table SQL queries, as requested above. (Also, do not use SQL operators that we have not talked about, like `join`, `count`, `group by`, etc.)

WARNING: A JDBC program can exhibit bizarre behavior when multiple result sets are
open at the same time. (Section 2.2.2 of the text explains this issue.) Consequently, your program should always close a result set before opening another.


## Part 2: Multi-line SQL inputs

(Exercise 2.3) The `SimpleIJ` program (in the `simpleclient` package of the repo) requires each SQL statement to be a single line of text. Revise `SimpleIJ.java` so that a statement can comprise multiple lines and terminate with a semicolon, similar to Derby’s `ij` program.

Below is an example run. For consistency, you may assume that all commands after the connection is made are expected to end with a semicolon (i.e. including the `exit;`).

```
Connect> 
jdbc:simpledb:studentdb             <-- user input

recovering existing database
transaction 1 committed

SQL> select SName,                  <-- user input
          GradYear, DName           <-- user input
   from STUDENT, DEPT               <-- user input
   where DId = MajorId;             <-- user input

      sname gradyear    dname
-----------------------------
        joe     2021  compsci
        amy     2020     math
        max     2022  compsci
        sue     2022     math
        bob     2020    drama
        kim     2020     math
        art     2021    drama
        pat     2019     math
        lee     2021  compsci
transaction 2 committed

SQL> exit;                          <-- user input
transaction 3 committed
```


## Part 3: Import

(Exercise 2.5) It is often useful to be able to create a text file that contains SQL commands. These commands can then be executed in batch by a JDBC program. [Modify SimpleIJ so that it reads commands from a specified text file and executes them.] Assume that each line of the file is a separate command. (These are **not** semicolon-terminated.)

Extend the `SimpleIJ` program further so that it supports a new command: `import <filepath>;`

For example, suppose a file, `script.txt` contains the following:

```
select SName, GradYear, DName from STUDENT, DEPT where DId = MajorId
select DName, Prof  from DEPT, COURSE, SECTION  where DeptId = DId and CourseId = CId
```

Then, this should be the output produced by the program:

```
SQL> import script.txt;            <-- user input     
    sname gradyear    dname
-----------------------------
        joe     2021  compsci
        amy     2020     math
        max     2022  compsci
        sue     2022     math
        bob     2020    drama
        kim     2020     math
        art     2021    drama
        pat     2019     math
        lee     2021  compsci
transaction 2 committed
    dname     prof
------------------
compsci   turing
compsci   turing
    math   newton
    math einstein
    drama   brando
transaction 3 committed
```



## Part 4: Null values

Modify the `doQuery()` method to handle field values that might be `null`. If a field value is `null`, it should print as `"(null)"` in parenthesis. Also, every column must be at least 6 characters wide to ensure there is space to show if a field value is `null`.

Use the `wasNull()` method of `ResultSet`. See Exercise 2.8 in the textbook for an explanation. 

To test that your changes work, you will need to generate records with null values for some fields. You can do this by inserting new records and completing omitting values for a particular field. For example, the following statement results in a `null` value for the `GradYear` field:

```
insert into STUDENT(SId, SName, MajorId) values (15, 'zaid', 20);
```



## (Optional) Part 5: Export

(Exercise 2.7b) Export data to a text file as SQL commands to recreate a table. The client should take the name of the file, the name of the table, and the fields names as input. The first line of the file should be a `create` statement to recreate the table; the following lines should be SQL `insert` statements reflecting the data in the table. 

Extend the `SimpleIJ` program further so that it supports a new command: `export <filepath> <table> <fields> ...;`

For example, the command:

    export out.txt STUDENT SId SName MajorId GradYear;

will produce (or replace) a file, `out.txt`, with the contents:

    create table STUDENT(sid int, sname varchar(10), majorid int, gradyear int)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (1, 'joe', 10, 2021)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (2, 'amy', 20, 2020)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (3, 'max', 10, 2022)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (4, 'sue', 20, 2022)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (5, 'bob', 30, 2020)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (6, 'kim', 20, 2020)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (7, 'art', 30, 2021)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (8, 'pat', 20, 2019)
    insert into STUDENT (SId, SName, MajorId, GradYear) values (9, 'lee', 10, 2021)

Notes:

- It is necessary to specify the names of the fields to export because SimpleDB doesn't (yet) support the `select * from ...` syntax for queries.

- In order to get the length of `varchar` fields, add the following method to `simpledb.jdbc.embedded.EmbeddedMetaData.java` and use it to obtain the intended length of the `varchar` fields from the `ResultSetMetaData` object in your `SimpleIJ` program.

```
   public int getPrecision(int column) throws SQLException {
	      String fldname = getColumnName(column);
	      int fldtype = sch.type(fldname);
	      int fldlength = (fldtype == INTEGER) ? 6 : sch.length(fldname);
	      return Math.max(fldname.length(), fldlength);
   }
```


~


## Submit

> - Submit the file `SectionInfo.java` for Part 1 to Canvas.
> - Submit your revised `SimpleIJ.java` for Parts 2 and 3 (and optional).
