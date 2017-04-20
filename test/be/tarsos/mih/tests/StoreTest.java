package be.tarsos.mih.tests;

import static org.junit.Assert.assertEquals;


import java.util.NavigableSet;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;


public class StoreTest {

	@Test
	public void testStore(){
		DB db = DBMaker.memoryDB().make();
		NavigableSet<long[]>  treeSet = db.treeSet("tree", Serializer.LONG_ARRAY ).createOrOpen();
		long[] e = {7,78,78};
		long[] f = {7,12,78};
		long[] g = {9,78,78};
		long[] h = {6,78,78};
		long[] i = {7,1,78};
		
		treeSet.add(e);
		treeSet.add(f);
		treeSet.add(g);
		treeSet.add(h);
		treeSet.add(i);
		
		long[] k = {7,0,0};
		long[] l = {7,Integer.MAX_VALUE,Integer.MAX_VALUE};
		
		assertEquals(3,treeSet.subSet(k, l).size());
		
		for(long[] element : treeSet.subSet(k, l)){
			System.out.println(element[1]);
		}
	}
}
