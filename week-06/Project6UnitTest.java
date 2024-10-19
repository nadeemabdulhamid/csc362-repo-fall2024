package simpledb.record;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simpledb.file.BlockId;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

class Project6UnitTest {
	

	@Test
	void test() {
		SimpleDB db = new SimpleDB("proj6test", 400, 8);
		Transaction tx = db.newTx();

		Schema sch = new Schema();
		sch.addIntField("A");
		sch.addStringField("B", 9);
		Layout layout = new Layout(sch);
		System.out.println("Slot size = " + layout.slotSize());
		
		assertEquals( 21, layout.slotSize() );
		
		for (String fldname : layout.schema().fields()) {
			int offset = layout.offset(fldname);
			int bitpos = layout.bitPosition(fldname);
			System.out.println(fldname + " has offset " + offset + " and bitpos " + bitpos);
		}
		assertEquals( 4, layout.offset("A") );
		assertEquals( 8, layout.offset("B") );
		assertEquals( 1, layout.bitPosition("A") );
		assertEquals( 2, layout.bitPosition("B") );


		BlockId blk = tx.append("testfile");
		tx.pin(blk);
		RecordPage rp = new RecordPage(tx, blk, layout);
		rp.format();

		System.out.println("Filling the page with records.");
		int slot = rp.insertAfter(-1);
		while (slot >= 0) {  
			rp.setInt(slot, "A", slot);
			rp.setString(slot, "B", "rec"+slot);
			System.out.println("[" + slot + ", " + "rec"+slot + "]");
			slot = rp.insertAfter(slot);
		}

		slot = rp.nextAfter(-1);
		while (slot >= 0) {
			int a = rp.getInt(slot, "A");
			if (a%2 == 0) 
				rp.setNull(slot, "B");
			slot = rp.nextAfter(slot);
		}
		rp.setNull(1,  "B");
		rp.setString(1, "B", "newrec1"); //B is no longer null
		rp.delete(4);
		slot = rp.insertAfter(-1); 
		System.out.println("new slot is " + slot); // should be 4
		
		assertEquals( 4, slot );
		
		String status = rp.isNull(slot, "B") ? "null" : "not null";
		System.out.println("slot's B value is " + status); // should be not null
		
		assertEquals( "not null", status );
		
		rp.setInt(slot,  "A", 99);
		
		System.out.println("After setting the B-value of even slots to null:");
		slot = rp.nextAfter(-1);
		int i = 0;
		while (slot >= 0) {
			int a = rp.getInt(slot, "A");
			String b = rp.isNull(slot,  "B") ? "null" : rp.getString(slot, "B");
			System.out.println("[" + a + ", " + b + "]");
			
			if (i == 4) {
				assertEquals( 99, a );
				assertEquals( "rec4", b );

			} else if (i % 2 == 0) { 
				assertEquals( i, a );
				assertEquals( "null", b );
			} else {
				assertEquals( i, a );
				assertEquals( i == 1 ? "newrec"+i : "rec"+i, b );
			}
			
			slot = rp.nextAfter(slot);
			i++;
		}
		
		tx.unpin(blk);
		tx.commit();
	}

}
