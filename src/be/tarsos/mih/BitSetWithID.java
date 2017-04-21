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

package be.tarsos.mih;

import java.util.BitSet;

/**
 * A set of bits with an identifier. The identifier is a long while the value is a Java BitSet backed by long[]. 
 * 
 * Memory consumption is in steps of the size of a long in the Java world this means steps of 64 bits.
 *
 */
public class BitSetWithID {
	
	/**
	 * 
	 * Bit values are stored in a bit set which is backed by an array of longs.
	 */
	private final BitSet bitSet;
	
	/**
	 * The identifier to use, 64bits.
	 */
	private final long identifier;
	
	/**
	 * A representation of this vector used for storage.
	 */
	private long[] longRepresentation = null;
	
	/**
	 * Creates a new vector with the requested number of dimensions.
	 * @param bytes The number of bytes.
	 */	
	public BitSetWithID(long id, BitSet bitSet) {
		this.bitSet = bitSet;
		this.identifier = id;
	}
	
	/**
	 * A copy constructor.
	 * @param original The original bit set.
	 */
	public BitSetWithID(BitSetWithID original) {
		this.bitSet = (BitSet) original.bitSet.clone();
		this.identifier = original.identifier;
	}

	public String toString(){
		StringBuilder sb= new StringBuilder();
		
		int actualBits = (int) Math.ceil(bitSet.length() / 64.0) * 64;
		for(int d=0; d < actualBits ; d++) {
			sb.append(bitSet.get(d)? "1" : "0");
		}
		sb.append(";");
		sb.append(String.format("%019d", identifier));
		return sb.toString();
	}

	/**
	 * @return the number of bits actually in use.
	 */
	public int getNumberOfBits() {
		return bitSet.size();
	}
	
	/**
	 * Calculates the hamming distance between this and the other.
	 * This is not the most efficient implementation: values are copied.
	 * @param other the other vector.
	 * @return  The hamming distance between this and the other.
	 */
	public int hammingDistance(BitSetWithID other) {
		//clone the bit set of this object
		BitSet xored = (BitSet) bitSet.clone();
		
		//xor (modify) the bit set
		xored.xor(other.bitSet);
		//return the number of 1's
		return xored.cardinality();
	}

	/**
	 * Set a bit at the specified index for the underlying bit set.
	 * @param bitIndex The index.
	 * @param value The value.
	 */
	public void set(int bitIndex, boolean value) {
		bitSet.set(bitIndex, value);
	}
	
	/**
	 * Get a bit value at the specified index for the underlying bit set.
	 * @param bitIndex The index.
	 * @return the value of the bit.
	 */
	public boolean get(int bitIndex) {
		return bitSet.get(bitIndex);
	}
	
	/**
	 * Flip a bit at the specified index for the underlying bit set.
	 * @param bitIndex The index.
	 */
	public void flip(int bitIndex){
		bitSet.flip(bitIndex);
	}
	
	
	/**
	 * @return the underlying bit set. For performance reasons no clone is done, so please do not modify.
	 */
	public BitSet getBitSet() {
		return bitSet;
	}
	
	/**
	 * @return The identifier
	 */
	public long getIdentifier(){
		return identifier;
	}
	
	
	/**
	 * For storage map this vector to an array of longs.
	 * @return a long[] representation of this vector
	 */
	public long[] toLongArray(){
		if(longRepresentation == null){
			long[] data = bitSet.toLongArray();
			
			longRepresentation = new long[1 + data.length];
			longRepresentation[0] = identifier;
			for(int i = 0 ; i < data.length ;i++){
				longRepresentation[i+1] = data[i];
			}
		}
		return longRepresentation;
	}
	
	/**
	 * Creates a bit set with an identifier from a long array representation.
	 * @param source The source array.
	 * @return A bit set with an identifier and values according to the long array values.
	 */
	public static BitSetWithID fromLongArray(long[] source){
		long[] bits = new long[source.length-1];
		for(int i = 0 ; i < bits.length ; i++){
			bits[i] = source[i+1];
		}
		return new BitSetWithID((int) source[0],BitSet.valueOf(bits));
	}
}
