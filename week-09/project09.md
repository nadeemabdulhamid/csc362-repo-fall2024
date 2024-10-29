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


# Project 9: Query Processing - Part 2

Complete exercises:

- 8.9 (extending `Term`)
- 8.11 (null handling)

See the instructions in the book. Here are some hints:

1. Modify `Constant` to implement a "null constant".
    - Add a constructor to the class to create the null constant. Let’s assume that a null constant has null values for both `ival` and `sval`. Then this constructor is trivial — it has no argument and have it do nothing (since ival and sval are null by default).
    - Add the method `isNull()` to class `Constant` that returns true if the object is a null
constant and false otherwise.
    - Modify the methods `equals`, `compareTo`, `hashCode`, and `toString`. A null
    constant will never compare successfully with another object (even another null
    constant!). That is, if you have a null constant, calling the equals method should
    always return false, calling the compareTo method should return -1, calling
    hashCode should return 0, and calling toString should return “null”.

2. Modify the `TableScan` class so that the method `getVal` will return a null constant if
the value of the requested field is null, and the method `setVal` will set the requested
field value to null if its argument is a null constant.

3. You need to generalize terms so that they can 
    be of the form "`e1 < e2`", "`e1 > e2`", or "`e1 IS null`". In this last term, the operator is the keyword “IS”. You might define some constants in the `Term` class to represent different operators on terms: `public static final int EQ=0, LT=1, GT=2, IS=3;`. Define a constructor that has three arguments, the lhs, rhs, and integer constant value representing the operator. Modify the existing two-argument constructor to use `EQ`.

    For the methods in the `Term` class, just focus on modifying `isSatisfied` and `toString`.
    - The `isSatisfied` method is where a term gets evaluated. Modify it so it does the
    appropriate comparison as specified by the operator. The IS operator should return true
    if its left-side expression evaluates to a null constant. Make sure that the other operators
    work correctly in the presence of null constants. Note that comparing a null constant to
    any other constant is always false, even if the other constant is also null.
    - The `toString` method constructs a representation of the term in SQL syntax, such as
    "A > 3", "B is null", etc.


