> **CSC 362 • Database Systems • Fall 2024**
# Project 2: Page stats; Error checking; Date support

## Unit Tests

The following file contains unit tests for the 3 parts of this assignment. You should add a `simpledb.unittests` package to your project and copy this file into it. You may need to add the JUnit runtime library to your project build path as well.

- [FileMgrTest.java](./FileMgrTest.java)


## Part 1: Statistics Reporting

(Exercise 3.15+) A database system often contains diagnostic routines.

1. Modify the class `FileMgr` so that it keeps useful statistics, such as the number of blocks read/written. Also modify the main `SimpleDB` server with a mechanism to enable/disable statistics reporting as follows.

    - Add methods named `blockReadCount()` and `blockWriteCount()` that return the number of blocks read/written *since the last time the method was called*. (Effectively, the methods should reset the count to 0 upon returning the current count.)

    - Add a field to the `simpledb.server.SimpleDB` class that represents whether statistics reporting output is enabled or not:

        ```
        private static boolean reporting_stats = false;
        ```

        Add methods to the `SimpleDB` class to query the state of the field as well as to change it: 
        - `boolean isReportingStats()` and
        - `void setReportingStats(boolean reportStats)`.

    - Add a method `reportStats()` to `FileMgr` which prints out the number of blocks read and written, if statistics reporting is enabled.

2. Modify the methods `commit` and `rollback` of the class `RemoteConnectionImpl` (in the `simpledb.jdbc.network` package) so that they print these statistics. Do the same for the class `EmbeddedConnection` (in the `simpledb.jdbc.embedded` pack- age). The result will be that the engine prints the statistics for each SQL statement it executes.

    Also modify the `SimpleDB(String dirname)` constructor to print statistics upon creating/recovering the database.

3. Add a `stats` command to the `SimpleIJ` program (in the `simpleclient` package) that allows toggling, or turning on/off statistics reporting. 

### Sample run

[Here is sample output](./stats-run.txt) of the `SimpleIJ` program with the steps above completed.


## Part 2: Error checking

(Exercise 3.16) The methods `setInt`, `setBytes`, and `setString` do not check that the specified value will fit into the byte buffer at the specified offset. If it doesn’t, the `ByteBuffer` class will throw an `IndexOutOfBounds` exception. 

Modify these methods so that they write the specified value only if it fits. If the value does not fit, the methods should print a descriptive message and ignore the request. For example, if the capacity of the page’s byte buffer is 400 bytes, then the call `p.setInt(398,12)` should print the following message:

    ERROR: The integer 12 does not fit at location 398 of the page (block size: 400)

Note: To get the capacity of a `ByteBuffer` object, call its `capacity()` method.

Avoid duplicating code as much as possible as you implement this feature.


## Part 3: Date support

(Exercise 3.17) The class `Page` has methods to get/set integers, [blobs](https://stackoverflow.com/questions/5414551/what-is-it-exactly-a-blob-in-a-dbms-context), and strings. Modify the class to handle dates as well by adding methods:

- `public void setDate(int offset, LocalDate dt)`
- `public LocalDate getDate(int offset)`

These reference the Java standard library `java.time.LocalDate` class. 

Hint: Convert to/from epoch day counts and write/read as a `long` value to the `ByteBuffer`.

