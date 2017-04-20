package be.tarsos.mih.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.tarsos.mih.BitSetWithID;
import be.tarsos.mih.EclipseStorage;
import be.tarsos.mih.LinearSearch;
import be.tarsos.mih.MultiIndexHasher;

public class RandomDataSetTest {
	
	static int dataSetSize = 50000;
	static int numBits = 128;
	static int longs = numBits/64;
	static int maxHammingDistance = 14;

	static int numQueries = 1000;
	static int numNearNeighboursToGenerate = 10;
	static int numNearNeighbours = 10;//k
	
	static MultiIndexHasher mih;
	
	static List<BitSetWithID> datasetForLinearSearch;
	static List<BitSetWithID> queries = new ArrayList<BitSetWithID>(numQueries);
	
	@BeforeClass
	public static void oneTimeSetUp() {
		createOrReadDataset();
		datasetForLinearSearch = mih.getDataSet(numQueries);
		
		Random r = new Random();
		queries = new ArrayList<BitSetWithID>(numQueries);
		int genQueries = 0;
		for(int i = 0 ; genQueries < numQueries ; i++){
			for(int k = 0 ; k < r.nextInt(numNearNeighboursToGenerate) + 1 ; k++){
				BitSetWithID query = new BitSetWithID(datasetForLinearSearch.get(i));
				int hammingDistance = r.nextInt(maxHammingDistance);
				for(int j = 0 ; j < hammingDistance ; j++ ){
					int bitIndex = r.nextInt(numBits);
					query.flip(bitIndex);
				}
				queries.add(query);
				genQueries++;
			}
		}
		
		System.out.print(queries.size());
		
	}
	
	@AfterClass
	public static void oneTimeTearDown() {
		mih.close();
	}

	private static void  createOrReadDataset(){
		mih = new MultiIndexHasher(numBits, maxHammingDistance, 4,new EclipseStorage(4));
		if(mih.size() < dataSetSize ){
			Random r = new Random();
			int counter = 0;
			for(int i = 0 ; i < dataSetSize ; i++){
				
					long[] bits = new long[longs];
				for(int j = 0 ; j < longs ; j++ ){
					bits[j] = r.nextLong();
				}
				BitSet originalBitSet = BitSet.valueOf(bits);
				BitSetWithID v = new BitSetWithID(i,originalBitSet);
				mih.add(v);
				
				if(counter == 10000){
					System.out.println(i + " " + mih.size());
					counter = 0;
				}
				counter ++;
			}
		}
		System.out.println("Dataset size: " + mih.size());
	}
	
	
	public void testRandomDataSetLinear(){
				
		LinearSearch ls = new LinearSearch(datasetForLinearSearch,maxHammingDistance);
		
		for(int k =0 ; k < queries.size() ; k++){
			BitSetWithID query = queries.get(k);
			List<BitSetWithID> results = ls.query(query, numNearNeighbours);
			assertEquals(query.getIdentifier(),results.get(0).getIdentifier());
		}
	}
	
	@Test
	public void testRandomDataSetMIH(){		
		for(int k =0 ; k < queries.size() ; k++){
			BitSetWithID query = queries.get(k);
			Collection<BitSetWithID> results = mih.query(query);
			//results.sort(new HammingDistanceComparator());
			for(BitSetWithID result: results){
				assertEquals(query.hammingDistance(result) <= maxHammingDistance, true);
				assertEquals(query.getIdentifier(),result.getIdentifier());
				//System.out.println( " neigh" + k + " " + results.size());
			}
			if(results.isEmpty()){
				assertEquals(true,false);
				//System.out.println( " neigh" + k + " " + results.size());
			}
		}
	}
}
