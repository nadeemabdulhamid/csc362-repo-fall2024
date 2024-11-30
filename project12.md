> **CSC 362 • Database Systems • Fall 2024**
# Project 12: ResultSet revisions 

Modify the SimpleDB `EmbeddedResultSet` implementation to support methods you  implemented in previous projects. Complete these exercises:

- 11.4a and 11.5 (Implement the `beforeFirst`, `afterLast`, and `previous` methods.) You should add `previous` and `afterLast` method signatures to the `Scan` interface, and implement them in `SelectScan`,
`ProjectScan`, and `ProductScan`.

    Technically, you should implement these methods in all of the classes in SimpleDB that implement `Scan`, but doing so is beyond the scope of this assignment. You should provide default implementation of the methods in the `Scan` interface to keep`the compiler happy.

- 11.6 (we went over in class - see the lecture recording if necessary)

The [`NullPrintTestClient.java`](./NullPrintTestClient.java) program can be used to test the various functionality you've implemented. (Change the method calls to use `previous` and `afterLast` to print the results in reverse order.)

Upload to Canvas your modified:

    - `EmbeddedResultSet.java`
    - `Scan.java`
    - `SelectScan.java`
    - `ProjectScan.java`
    - `ProductScan.java`
