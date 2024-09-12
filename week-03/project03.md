> **CSC 362 • Database Systems • Fall 2024**
# Project 3: Efficient buffer management

The SimpleDB buffer manager is grossly inefficient in two ways:
- When looking for a buffer to replace (`chooseUnpinnedBuffer`), it uses the first unpinned buffer it finds, instead of doing something intelligent like LRU.
- When checking to see if a block is already in a buffer (`findExistingBuffer`), it does a sequential scan of the buffers, instead of keeping a data structure (such as a map) to more quickly locate the buffer.

Fix these problems by modifying the class `BufferMgr`. Use the following strategy:

1. Keep a `list` of the unpinned buffers. When a replacement buffer needs to be chosen, remove the buffer at the head of the list and use it. When a buffer's pin count becomes 0, add it to the end of the list. This implements *LRU replacement*.

2. Keep a `map`, whose entries describe the blocks that are currently
stored in buffers. The key of each map entry is a `BlockId`, and its value is the
`Buffer` object holding that block. The map is initially empty. When you need to know
if a block is currently in a buffer, you simply look it up in the map. If you want to read
a block into a chosen buffer, you add an entry to the map. If the chosen buffer
already contains a block, then you must also remove the entry for that block. Recall
that each `Buffer` object has a method `block()`, which tells you the block (if any)
that is in that buffer.

3. Get rid of the `numAvailable` field, because you can determine the number of available buffers
by looking at the unpinned list.


### Testing

The following file contains some tests that you can use to debug and check your implementation. 

- [ProjectBufferMgrTest.java](./ProjectBufferMgrTest.java)

This program pins and unpins buffers and then checks the status of the buffer manager. In order to get it to work, you will need to implement the following two methods:

```
	/**
	 * Produces a string showing the contents of the buffer pool,
	 * in the format 
	 *	 "[#+, #-, #+, #-, #+, ...]"
	 * where each # is the number of the BlockId associated with 
	 * that slot of the buffer and +/- indicates if it is 
	 * pinned (+) or unpinned (-)
	 */
	public String bufferPoolString()

	/**
	 * Produces a comma separated string listing the indices of 
	 * buffer slots in the order that they will be filled in with
	 * new pages as they are pinned. For example, "2, 3, 0"
	 * would indicate that - conceptually - bufferpool[2] is the   
	 * next slot that will be filled in with a newly pinned page.
	 */
	public String availableLRUString()
```

### Submit

Upload your modified `BufferMgr.java` file. You should not need to modify any other files.
