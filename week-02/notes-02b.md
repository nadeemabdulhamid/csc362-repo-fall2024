> **CSC 362 • Database Systems • Fall 2024**
# Lecture 4 - SimpleDB File Manager

- Study the files in `simpledb.file`.

- Explain why this program prints what it does.

```
package simpledb.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MyFileMgrTest {
	public static void main(String[] args) throws IOException {
		FileMgr fm = new FileMgr(new File("testdir"), 40);

		Page p1 = new Page(fm.blockSize());
		p1.setInt(16, 8);
		p1.setInt(20, 42);
		p1.setInt(24, 128);
		byte[] bs = p1.getBytes(16);
		
		System.out.println(bs.length);			// ??????
		System.out.println(bs[3]);				// ??????
		
		fm.write(new BlockId("file1", 5), p1);
		
		Page p2 = new Page(100);
		fm.read(new BlockId("file1", 4), p2);
		
		int i = p2.getInt(64);
		System.out.println(i);					// ?????????
	}

}
```