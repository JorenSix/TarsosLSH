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
*  TarsosLSH is developed by Joren Six at 
*  The School of Arts,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
* -----------------------------------------------------------
*
*  Info    : http://tarsos.0110.be/tag/TarsosLSH
*  Github  : https://github.com/JorenSix/TarsosLSH
*  Releases: http://tarsos.0110.be/releases/TarsosLSH/
* 
*/

package be.hogent.tarsos.lsh;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import be.hogent.tarsos.lsh.families.HashFamily;
import be.hogent.tarsos.lsh.families.HashFunction;
import be.hogent.tarsos.lsh.util.FileUtils;

/**
 * An index contains one or more locality sensitive hash tables. These hash
 * tables contain the mapping between a combination of a number of hashes
 * (encoded using an integer) and a list of possible nearest neighbours.
 * 
 * @author Joren Six
 */
class MemoryHashTable implements HashTable, Serializable {
	
	
	private static final long serialVersionUID = -5410017645908038641L;
	private final static Logger LOG = Logger.getLogger(MemoryHashTable.class.getName()); 
	
	/**
	 * Contains the mapping between a combination of a number of hashes (encoded
	 * using an integer) and a list of possible nearest neighbours
	 */
	private HashMap<Integer,List<Vector>> hashTable;
	private HashFunction[] hashFunctions;
	private HashFamily family;
	
	/**
	 * Initialize a new hash table, it needs a hash family and a number of hash
	 * functions that should be used.
	 * 
	 * @param numberOfHashes
	 *            The number of hash functions that should be used.
	 * @param family
	 *            The hash function family knows how to create new hash
	 *            functions, and is used therefore.
	 */
	public MemoryHashTable(int numberOfHashes,HashFamily family){
		hashTable = new HashMap<Integer, List<Vector>>();
		this.hashFunctions = new HashFunction[numberOfHashes];
		for(int i=0;i<numberOfHashes;i++){
			hashFunctions[i] = family.createHashFunction();
		}
		this.family = family;
	}

	/**
	 * Query the hash table for a vector. It calculates the hash for the vector,
	 * and does a lookup in the hash table. If no candidates are found, an empty
	 * list is returned, otherwise, the list of candidates is returned.
	 * 
	 * @param query
	 *            The query vector.
	 * @return Does a lookup in the table for a query using its hash. If no
	 *         candidates are found, an empty list is returned, otherwise, the
	 *         list of candidates is returned.
	 */
	public List<Vector> query(Vector query) {
		Integer combinedHash = hash(query);
		if(hashTable.containsKey(combinedHash))
			return hashTable.get(combinedHash);
		else
			return new ArrayList<Vector>();
	}

	/**
	 * Add a vector to the index.
	 * @param vector
	 */
	public void add(Vector vector) {
		Integer combinedHash = hash(vector);
		if(! hashTable.containsKey(combinedHash)){
			hashTable.put(combinedHash, new ArrayList<Vector>());
		}
		hashTable.get(combinedHash).add(vector);
	}
	
	/**
	 * Calculate the combined hash for a vector.
	 * @param vector The vector to calculate the combined hash for.
	 * @return An integer representing a combined hash.
	 */
	private Integer hash(Vector vector){
		int hashes[] = new int[hashFunctions.length];
		for(int i = 0 ; i < hashFunctions.length ; i++){
			hashes[i] = hashFunctions[i].hash(vector);
		}
		Integer combinedHash = family.combine(hashes);
		return combinedHash;
	}

	/**
	 * Return the number of hash functions used in the hash table.
	 * @return The number of hash functions used in the hash table.
	 */
	public int getNumberOfHashes() {
		return hashFunctions.length;
	}
	
	
	/**
	 * Serializes the memory to disk.
	 * @param hashTable the storage object.
	 */
	public static void serialize(MemoryHashTable hashTable){
		try {
			
			String serializationFile = serializationName(hashTable);;
			OutputStream file = new FileOutputStream(serializationFile);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(hashTable);
			} finally {
				output.close();
			}
		} catch (IOException ex) {

		}
	}
	
	/**
	 * Return a unique name for a hash table wit a family and number of hashes. 
	 * @param hashtable the hash table.
	 * @return e.g. "be.hogent.tarsos.lsh.CosineHashfamily_16.bin"
	 */
	private static String serializationName(MemoryHashTable hashtable){
		String name = hashtable.family.getClass().getName();
		int numberOfHashes = hashtable.getNumberOfHashes();
		return name + "_" + numberOfHashes + ".bin";
	}
	

	/**
	 * Deserializes the hash table from disk. If deserialization fails, 
	 * a new hash table object is created.
	 * @param numberOfHashes the number of hashes.
	 * @param family The family.
	 * @return a new, or deserialized object.
	 */
	public static MemoryHashTable deserialize(int numberOfHashes,HashFamily family){
		MemoryHashTable hashTable = new MemoryHashTable(numberOfHashes,family);
		String serializationFile = serializationName(hashTable);
		if(FileUtils.exists(serializationFile)){
			try {
				
				InputStream file = new FileInputStream(serializationFile);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);
				try {
					hashTable = (MemoryHashTable) input.readObject();
				} finally {
					input.close();
				}
			} catch (ClassNotFoundException ex) {
				LOG.severe("Could not find class during deserialization: " + ex.getMessage());
			} catch (IOException ex) {
				LOG.severe("IO exeption during during deserialization: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		return hashTable;
	}
}
