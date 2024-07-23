package simpledb.unittests;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simpledb.file.*;
import java.nio.file.Files;
import java.time.LocalDate;


class FileMgrTest {
	static final int BLOCK_SIZE = 16;
	FileMgr fm;
	Page p;

	@BeforeEach
	void setUp() throws Exception {
		fm = new FileMgr(Files.createTempDirectory("simpledb").toFile(), BLOCK_SIZE);
		p = new Page(BLOCK_SIZE);
	}

	@Test
	void testStatsCount() {
		assertEquals(0, fm.blockReadCount());
		assertEquals(0, fm.blockWriteCount());
		
		fm.read(new BlockId("setup", 1), p);
		fm.write(new BlockId("setup", 0), p);
		fm.read(new BlockId("setup", 1), p);
		assertEquals(2, fm.blockReadCount());
		assertEquals(1, fm.blockWriteCount());
		
		// after reading the count, it is reset to 0
		assertEquals(0, fm.blockReadCount());
		assertEquals(0, fm.blockWriteCount());
		
		fm.append("setup");
		fm.append("setup");
		fm.read(new BlockId("setup", 2), p);
		fm.append("setup");
		assertEquals(1, fm.blockReadCount());
		assertEquals(3, fm.blockWriteCount());
	}
	
	@Test
	void testReadWrite() {
		Page q = new Page(BLOCK_SIZE);
		p.setInt(0, 1234);
		p.setInt(4, 8765);
		
		fm.write(new BlockId("setup", 0), p);
		
		fm.read(new BlockId("setup", 0), q);
		assertEquals(8765, q.getInt(4));
		assertEquals(1234, q.getInt(0));

		BlockId b = fm.append("empty");
		fm.read(b,  q);
		assertEquals(0, q.getInt(0));
		
		assertEquals(6, Page.maxLength(2));
		q.setString(1, "ab");
		fm.write(b, q);
		fm.read(b, p);
		assertEquals("ab", p.getString(1));
	}
	
	@Test
	void testReadWriteOutofbounds() {
		p.setInt(BLOCK_SIZE - Integer.BYTES, 1234);  // should be ok
		System.out.println("Should get an error message:");
		p.setInt(BLOCK_SIZE - 1, 8765);				// should not throw an exception
		
		assertEquals(1234, p.getInt(BLOCK_SIZE - Integer.BYTES));  // this should be as it was
		
		System.out.println("Should get an error message:");
		p.setString(BLOCK_SIZE - 5, "abcdefghijkl"); // should not throw an exception
		
		byte[] bs = { 1, 2, 3, 4, 5 };
		p.setBytes(BLOCK_SIZE - 9, bs);					// should be ok
		System.out.println("Should get an error message:");
		p.setBytes(BLOCK_SIZE - 7, bs);					// should not throw an exception
	}

	@Test
	void testDate() {
		Page q = new Page(BLOCK_SIZE);

		p.setDate(3, LocalDate.of(2024, 7, 14));
		fm.write(new BlockId("setup", 0), p);
		fm.read(new BlockId("setup", 1), q);  // bogus
		fm.read(new BlockId("setup", 0), q);  // bogus
		
		LocalDate dt = q.getDate(3);
		assertEquals(2024, dt.getYear());
		assertEquals(7, dt.getMonthValue());
		assertEquals(14, dt.getDayOfMonth());		
	}
	
}
