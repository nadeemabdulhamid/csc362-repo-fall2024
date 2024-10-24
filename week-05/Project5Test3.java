package simpledb.unittests;

import simpledb.buffer.BufferMgr;
import simpledb.file.*;
import simpledb.log.LogMgr;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;
import simpledb.tx.concurrency.LockAbortException;

public class Project5Test3 {
   private static FileMgr fm;
   private static LogMgr lm;
   private static BufferMgr bm;

   public static void main(String[] args) {
      //initialize the database system
      SimpleDB db = new SimpleDB("concurrencytest", 400, 8); 
      fm = db.fileMgr();
      lm = db.logMgr();
      bm = db.bufferMgr();
      A a = new A(); new Thread(a).start();
      B b = new B(); new Thread(b).start();
      C c = new C(); new Thread(c).start();
   }

   static class A implements Runnable { 
      public void run() {
         Transaction txA = null;
         try {
            txA = new Transaction(fm, lm, bm);
            System.out.println("Transaction A starts");
            BlockId blk1 = new BlockId("testfile", 1);
            txA.pin(blk1);
            System.out.println("Tx A: request slock block 1");
            txA.getInt(blk1, 0);
            System.out.println("Tx A: receive slock block 1");
            Thread.sleep(1000);
            

            
            txA.commit();
            System.out.println("Transaction A commits");
         }
         catch(InterruptedException e) {}
         catch(LockAbortException e) {
            System.out.println("Transaction A aborts");
            txA.rollback();
         }
      }
   }

   static class B implements Runnable {
      public void run() {
         Transaction txB = null;
         try {
            Thread.sleep(300);
            txB = new Transaction(fm, lm, bm);
            System.out.println("Transaction B starts");
            BlockId blk1 = new BlockId("testfile", 1);
            txB.pin(blk1);
            
            System.out.println("Tx B: request slock block 1");
            txB.getInt(blk1, 0);
            System.out.println("Tx B: receive slock block 1");
            Thread.sleep(1000);

            
            
            
            
            txB.commit();
            System.out.println("Transaction B commits");
         }
         catch(InterruptedException e) {}
         catch(LockAbortException e) {
            System.out.println("Transaction B aborts");
            txB.rollback();
         }
      }
   }

   static class C implements Runnable {
      public void run() {
         Transaction txC = null;
         try {
            txC = new Transaction(fm, lm, bm);
            System.out.println("Transaction C starts");
            BlockId blk1 = new BlockId("testfile", 1);
            txC.pin(blk1);

            
            System.out.println("Tx C: request slock block 1");
            txC.getInt(blk1, 0);
            System.out.println("Tx C: receive slock block 1");
            Thread.sleep(500);
            

            System.out.println("Tx C: request xlock block 1");
            txC.setInt(blk1, 0, 0, false);
            System.out.println("Tx C: receive xlock block 1");
            
            // **** C should abort because older transacdtions already have locks on this block


            

            txC.commit();
            System.out.println("Transaction C commits");
         }
         catch(InterruptedException e) {}
         catch(LockAbortException e) {
            System.out.println("Transaction C aborts");
            txC.rollback();
         }
      }
   }
}