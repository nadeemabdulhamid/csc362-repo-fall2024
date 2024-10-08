> **CSC 362 • Database Systems • Fall 2024**
# Project 7: Table scanning in reverse

[based on Exercise 6.10]

The SimpleDB record manager only knows how to read files in the forward direction.
Your task this week is to make it possible to also read files in reverse. In particular, you should add the following two methods to the class `TableScan`:

- the method `previous()`, which moves to the previous record in the file and returns
false if there is no such record;

- the method `afterLast()`, which positions the current record to be after the last
record in the file (so that a call to `previous()` will position the current record at the
last record in the file).

To implement `afterLast()`, you will need to be able to position a record page after its
last slot. To implement `previous()`, you will also need to search a record page
backwards. You should implement these capabilities by
writing the following methods in `RecordPage`:

- a method `slots()`, which returns the number of slots in the page;
- a method `nextBefore(int slot)`, which returns the next used slot before the
specified one, or -1 if no such slot exists.

You are free to implement other private methods in either class.

---

### Notes

You should assume that the first block of a file is block 0, and the last block is block
`s-1`, where `s` is the size of the file. Your code can find the size of file `f` by calling the method `tx.size(f)`.

Your changes to `RecordPage` should begin from the version you wrote for Project 6. Be
careful when writing `nextBefore` — remember that the null-value flags are now part
of the empty/inuse integer.

Make one additional change to `TableScan` to add methods allowing table scans to understand null values. The following methods should work; please add them to your file.

```
public boolean isNull(String fldname) {
    return rp.isNull(currentslot, fldname);
}
public void setNull(String fldname) {
    rp.setNull(currentslot, fldname);
}
```

Use [Project7Test.java](./Project7Test.java) to help verify that your code works. Simplify or rewrite it during debugging, so that you can test features incrementally.

When you are done, submit `RecordPage.java` and `TableScan.java` to Canvas.

