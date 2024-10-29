package simpledb.query;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import simpledb.record.Layout;
import simpledb.record.Schema;
import simpledb.record.TableScan;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

class Project9UnitTest {

	SimpleDB db = new SimpleDB("project9");
	Transaction tx = db.newTx();


	@Test
	void test() {
		File f = new File("project9/T.tbl");    // delete the old table file if it exists
		if (f.isFile()) { f.delete(); }        // from a previous run of this test
		
		Schema sch1 = new Schema();
		sch1.addIntField("A");
		sch1.addStringField("B", 15);
		Layout layout = new Layout(sch1);
		UpdateScan s1 = new TableScan(tx, "T", layout);

		s1.beforeFirst();
		int n = 200;
		for (int i=0; i<n; i++) {
			System.out.println("Adding " + (i+1) + ": " + fishSpecies[ i % fishSpecies.length ]);
			s1.insert();
			s1.setInt("A", i+1);			 // 1 ... 200
			s1.setString("B", fishSpecies[ i % fishSpecies.length ]);
		}
		s1.close();

		// test a select scan on the table
		Scan s = new TableScan(tx, "T", layout);
		testSelect(s);

		// test a rename scan
		testRename(new TableScan(tx, "T", layout));

		// test a union scan
		testUnion(new TableScan(tx, "T", layout));
	}

	void testSelect(Scan s) {
		// select A, B from ... WHERE B = "Betta"
		Constant c = new Constant("Betta");
		Term t = new Term(new Expression("B"), new Expression(c));
		Predicate p = new Predicate(t);
		Scan s2 = new SelectScan(s, p);

		int i = 0;
		while (s2.next()) {
			assertEquals( i * fishSpecies.length + 2,  s2.getInt("A"));			
			assertEquals( "Betta",  s2.getString("B"));			
			i++;
		}				

	}



	void testRename(Scan s) {
		// select A, B as fishname from ... WHERE B = "Betta"
		Constant c = new Constant("Betta");
		Term t = new Term(new Expression("B"), new Expression(c));
		Predicate p = new Predicate(t);
		Scan s2 = new SelectScan(s, p);

		Scan s3 = new RenameScan(s2, "B", "fishname");

		assertTrue(s2.hasField("B"));
		assertFalse(s2.hasField("fishname"));

		assertFalse(s3.hasField("B"));
		assertTrue(s3.hasField("fishname"));
		assertTrue(s3.hasField("A"));

		int i = 0;
		while (s3.next()) {
			assertEquals( i * fishSpecies.length + 2,  s3.getInt("A"));			
			assertEquals( "Betta",  s3.getString("fishname"));			
			i++;
		}				
	}



	void testUnion(Scan s) {
		// select A, B from ... WHERE B = "Betta"
		//   union
		// select A, B from ... WHERE B = "Tuna"

		Constant c = new Constant("Betta");
		Term t = new Term(new Expression("B"), new Expression(c));
		Predicate p = new Predicate(t);
		Scan s2 = new SelectScan(s, p);

		Constant c2 = new Constant("Tuna");
		Term t2 = new Term(new Expression("B"), new Expression(c2));
		Predicate p2 = new Predicate(t2);
		Scan s3 = new SelectScan(s, p2);

		Scan u = new UnionScan(s2, s3);


		int i = 0;
		while (u.next()) {
			int a = u.getInt("A")%fishSpecies.length;
			assertTrue(a == 19 || a == 2);			
			assertTrue(u.getString("B").equals("Betta") || u.getString("B").equals("Tuna"));
			i++;
		}	

		assertEquals(19, i);

	}




	String[] fishSpecies = {
			"Goldfish",
			"Betta",
			"Guppy",
			"Clownfish",
			"Angelfish",
			"Tetra",
			"Catfish",
			"Mollies",
			"Swordtail",
			"Cichlid",
			"Zebra Danio",
			"Rainbowfish",
			"Koi",
			"Pufferfish",
			"Triggerfish",
			"Grouper",
			"Snapper",
			"Salmon",
			"Tuna",
			"Shark",
			"Swordfish"
	};
}
