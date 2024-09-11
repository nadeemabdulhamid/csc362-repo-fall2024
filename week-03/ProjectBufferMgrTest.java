package simpledb.unittests;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.BlockId;
import simpledb.server.SimpleDB;

class ProjectBufferMgrTest {
	SimpleDB db;
	BufferMgr bm;
	Map<BlockId,Buffer> buffs = new HashMap<>();

	@BeforeEach
	void setUp() throws Exception {
		db = new SimpleDB(Files.createTempDirectory("test").toFile().getAbsolutePath(), 400, 4); 
		bm = db.bufferMgr();
	}

	@Test
	void testLRU() {
		pin(0); pin(1); pin(2); pin(3);
		check("[0+, 1+, 2+, 3+]", "");
		
		unpin(2);	check("[0+, 1+, 2-, 3+]", "2");
		unpin(0);   check("[0-, 1+, 2-, 3+]", "2, 0");
		unpin(1);   check("[0-, 1-, 2-, 3+]", "2, 0, 1");
		pin(4);		check("[0-, 1-, 4+, 3+]", "0, 1");		// replaces the earliest unpinned
		pin(1); 	check("[0-, 1+, 4+, 3+]", "0");			// re-pin
		
		pin(2);     check("[2+, 1+, 4+, 3+]", "");			// 2 was previous in [.., .., 2, ..] but that should not be 
															// relevant once its unpinned page is overwritten
		unpin(4);	check("[2+, 1+, 4-, 3+]", "2");
		unpin(3);	check("[2+, 1+, 4-, 3-]", "2, 3");
		unpin(2);	check("[2-, 1+, 4-, 3-]", "2, 3, 0");
		unpin(1);	check("[2-, 1-, 4-, 3-]", "2, 3, 0, 1");
		
		pin(5);		check("[2-, 1-, 5+, 3-]", "3, 0, 1");
		pin(6);		check("[2-, 1-, 5+, 6+]", "0, 1");
		pin(7); 	check("[7+, 1-, 5+, 6+]", "1");
		pin(8);		check("[7+, 8+, 5+, 6+]", "");
		
		unpin(6); unpin(5);	unpin(8); unpin(7);
		pin(0); pin(1); pin(2); pin(3);
		check("[3+, 2+, 1+, 0+]", "");	
		
		unpin(2); unpin(1);	unpin(0); unpin(3);
		pin(0); pin(1); pin(2); pin(3);
		check("[3+, 2+, 1+, 0+]", "");	
	}

	private void check(String bpstr, String lrustr) {
		assertEquals( bpstr, bm.bufferPoolString() );
		assertEquals( lrustr, bm.availableLRUString() );
	}
	
	private void pin(int i) {
		BlockId blk = new BlockId("test", i);
		Buffer buff = bm.pin(blk);
		buffs.put(blk, buff);
	}

	private void unpin(int i) {
		BlockId blk = new BlockId("test", i);
		Buffer buff = buffs.remove(blk);
		bm.unpin(buff);
	}


}
