> **CSC 362 • Database Systems • Fall 2024**
# Project 10: Extending SQL Syntax

Complete exercises:

- 9.7 (Extend the grammar/parser to support `<`, `>`, as terms)

- 9.18 (Extend the grammar/parser to support `null` constants/values/operator)

- 9.11 (Implement *range* variables)

- 9.13 (Extend SQL with `union`)

- 9.15 (Support `*` in `select` clause)

> For the latter three above, you may need to wait until completing the next project to ensure they are actually working correctly. (See the dependencies below.)

Keep track of the files you modify/create. You will upload all modified files for this project and the next one together to Canvas.



---

## Inter-project dependencies

- 8.9 (`<`, `>` terms) ... 9.7 (modify grammar)
- 8.11 (`null` support) ... 9.18 (modify grammar)
- 8.14 (`RenameScan`) ... 9.11 (modify grammar) ... 10.13 (`RenamePlan`)
- 8.16 (`UnionScan`) ... 9.13 (modify grammar) ... 10.15 (`UnionPlan`)
- 9.15 (`*` op) ... 10.17 (extend planner)
