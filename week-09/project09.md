> **CSC 362 • Database Systems • Fall 2024**
# Project 9: Query Processing - Part 1

Complete exercises:

- 8.14 (`RenameScan`)

    The rename operator takes three arguments: a scan denoting the input table, the
name of a field from the table, and a new name for that field. The output table is
identical to the input table, except that the specified field has been renamed.

- 8.16 (`UnionScan`) 

    The union operator takes two scans as input, both of which have the same schema.
The output consists of the union of those records, *without* removing duplicates.

When you are done, submit `RenameScan.java` and
`UnionScan.java` to Canvas.