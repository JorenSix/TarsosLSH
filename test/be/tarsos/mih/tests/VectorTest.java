package be.tarsos.mih.tests;



import static org.junit.Assert.assertEquals;

import java.util.BitSet;
import java.util.Random;

import org.junit.Test;

import be.tarsos.mih.BitSetWithID;


public class VectorTest {
	
	@Test
	public void testVectorToLongEncoding(){
		Random r = new Random();
		long[] bits = {r.nextLong(),r.nextLong()};
		BitSet originalBitSet = BitSet.valueOf(bits);
		int identifier = r.nextInt();
		BitSetWithID originalVector = new BitSetWithID(identifier,originalBitSet);
		long[] originalLongArray = originalVector.toLongArray();
		BitSetWithID decodedVector = BitSetWithID.fromLongArray(originalLongArray);
		for(int i = 0 ; i < decodedVector.getNumberOfBits() ; i ++ ){
			assertEquals(originalVector.get(i),decodedVector.get(i));
		}
		assertEquals(originalVector.getIdentifier(),decodedVector.getIdentifier());
	}

}
