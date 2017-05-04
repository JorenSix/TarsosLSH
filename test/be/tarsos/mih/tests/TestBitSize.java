package be.tarsos.mih.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import be.tarsos.mih.BitSetWithID;
import be.tarsos.mih.EclipseStorage;
import be.tarsos.mih.MultiIndexHasher;

public class TestBitSize {
	
	static int dataSetSize = 50000;
	static int numQueries = 5000;
	static int numBits = 32;
	static int maxHammingDistance = 4;
	static int maxNumNeighbors = 7;
	static MultiIndexHasher mih;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		mih = new MultiIndexHasher(numBits, maxHammingDistance, 2,new EclipseStorage(2));
		Random r = new Random(1);
		int counter = 0;
		for(int i = 0 ; i < dataSetSize ; i++){
			long[] bits = new long[1];
			bits[0] = r.nextInt();
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
	
	@Test
	public void testQuery(){
		List<BitSetWithID> datasetForLinearSearch;
		datasetForLinearSearch = mih.getDataSet(numQueries);
		
		Random r = new Random(1);
		ArrayList<BitSetWithID> queries = new ArrayList<BitSetWithID>(numQueries);
		int genQueries = 0;
		for(int i = 0 ; genQueries < numQueries ; i++){
			for(int k = 0 ; k < r.nextInt(maxNumNeighbors) + 1 ; k++){
				BitSetWithID orig = datasetForLinearSearch.get(i);
				BitSetWithID query = new BitSetWithID(orig);
				assertEquals(orig.getIdentifier(), query.getIdentifier());
				
				//flip some bits
				int hammingDistance = r.nextInt(maxHammingDistance-2)+2;
				for(int j = 0 ; j < hammingDistance ; j++ ){
					int bitIndex = r.nextInt(numBits);
					query.flip(bitIndex);
				}
				queries.add(query);
				genQueries++;
			}
		}
		
		for(BitSetWithID q :queries){
			Collection<BitSetWithID>  nn = mih.query(q,maxNumNeighbors);
			System.out.println(q);
			for(BitSetWithID result: nn){
				assertEquals(q.hammingDistance(result) <= maxHammingDistance, true);
				System.out.println(result);
			}
			if(nn.isEmpty()){
				assertEquals(true,false);
			}
			
			
			System.out.println();
		}
	}

}
