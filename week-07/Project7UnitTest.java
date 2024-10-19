package simpledb.record;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

class Project7UnitTest {

	@Test
	void test() {
		File f = new File("hw7test/T.tbl");    // delete the old table file if it exists
		if (f.isFile()) { f.delete(); }        // from a previous run of this test

		SimpleDB db = new SimpleDB("hw7test", 400, 8);
		Transaction tx = db.newTx();

		Schema sch = new Schema();
		sch.addIntField("A");
		sch.addStringField("B", 50);
		Layout layout = new Layout(sch);
		TableScan ts = new TableScan(tx, "T", layout);
		ts.beforeFirst();

		populateTable(ts);

		System.out.println("Here are the first 10 records");
		ts.beforeFirst();
		for (int i=0; i<10; i++) {
			ts.next();
			printAndCheckCurrentRecord(ts, i+1, i%2==0 ? "record"+(i+1) : "null");
		}

		System.out.println("\nHere are the 5 recent records backwards");
		for (int i=0; i<5; i++) {
			ts.previous();
			printAndCheckCurrentRecord(ts, 9-i, (9-i)%2==1 ? "record"+(9-i) : "null");
		}

		System.out.println("\nHere is the first record again");
		while(ts.previous())
			; // do nothing
		ts.next();
		ts.setString("B", "not null");
		printAndCheckCurrentRecord(ts, 1, "not null");


		System.out.println("\nAnd here are the last 10 records");
		ts.afterLast();
		for (int i=0; i<10; i++) 
			ts.previous();
		for (int i=0; i<10; i++) {
			printAndCheckCurrentRecord(ts, 491+i, (491+i)%2==1 ? "record"+(491+i) : "null");
			ts.next();
		}

		ts.close();
		tx.commit();
	}

	// Create 500 records having A-values from 1 to 500.
	// Every even record will have a B-value of null.
	private static void populateTable(TableScan ts) {
		for (int i=1; i<=500; i++) {
			ts.insert();
			ts.setInt("A", i);
			if (i%2 == 0)
				ts.setNull("B");
			else
				ts.setString("B", "record"+i);
		}
		ts.beforeFirst();
		int i = 1;
		while (ts.next()) {
			printAndCheckCurrentRecord(ts, i, i%2==1 ? "record"+i : "null");
			i++;
		}
		System.out.println("done");
	}

	private static void printAndCheckCurrentRecord(TableScan ts, int expVal, String expData) {
		int aval = ts.getInt("A");
		String bval = ts.isNull("B") ? "null" : ts.getString("B");
		System.out.println(aval + "\t" + bval);
		assertEquals( expVal, aval );
		assertEquals( expData, bval );
	}

}
