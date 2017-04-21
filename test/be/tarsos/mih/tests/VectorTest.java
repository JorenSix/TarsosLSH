/*
*      _______                       _        ____ _     _
*     |__   __|                     | |     / ____| |   | |
*        | | __ _ _ __ ___  ___  ___| |    | (___ | |___| |
*        | |/ _` | '__/ __|/ _ \/ __| |     \___ \|  ___  |
*        | | (_| | |  \__ \ (_) \__ \ |____ ____) | |   | |
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|   |_|
*                                                         
* -----------------------------------------------------------
*
*  TarsosLSH is developed by Joren Six.
*  
* -----------------------------------------------------------
*
*  Info    : http://0110.be/tag/TarsosLSH
*  Github  : https://github.com/JorenSix/TarsosLSH
*  Releases: http://0110.be/releases/TarsosLSH/
* 
*/
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
