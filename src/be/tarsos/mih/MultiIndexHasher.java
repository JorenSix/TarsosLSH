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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import be.tarsos.mih.storage.MIHStorage;


/**
 * Implements a multi-index hash algorithm as 
 * described in <a href="http://www.cs.toronto.edu/~norouzi/research/papers/multi_index_hashing.pdf">Fast 
 * Search in Hamming Space with Multi-Index Hashing</a> by Mohammad Norouzi, Ali Punjani, David Fleet - IEEE
 * Computer Vision and Pattern Recognition (CVPR) 2012
 *
 * 
 * @author Joren Six
 */
public class MultiIndexHasher {
	
	private final int numBits;//B in the paper
	private final int hammingSearchRadius;//D
	private final int hammingSearchRadiusPerSubstring;//d
	private final int numberOfChunks;//m
	private final int bitsPerChunk;//b
	
	private static Logger LOG = Logger.getLogger(MultiIndexHasher.class.getName());
	private final MIHStorage storage;
	
	/**
	 * Initialize a new hasher.
	 * @param numBits The number of bits in the vectors to search. Any number is possible but for memory efficiency multiples of 64 are advised. 
	 * @param hammingSearchRadius The hamming search radius, the max allowed Hamming distance between vectors considered neighbors.
	 * @param numberOfChunks The number of chunks to use (hash tables). 
	 * @param storage The in memory or persistent storage to use.
	 */
	public MultiIndexHasher(int numBits, int hammingSearchRadius,int numberOfChunks,MIHStorage storage){
		this.numBits = numBits;
		this.hammingSearchRadius = hammingSearchRadius;
		this.numberOfChunks = numberOfChunks;
		this.bitsPerChunk = (int) Math.ceil(numBits/(float)numberOfChunks);		
		this.hammingSearchRadiusPerSubstring = (int) Math.floor( hammingSearchRadius / (float)numberOfChunks );		
		this.storage = storage;
	}
	
	/**
	 * Adds an item to the data set.
	 * @param item The item to add.
	 */
	public void add(BitSetWithID item){
		for(int i = 0 ; i < numberOfChunks ; i++){
			int key = getChunkHash(item, i);
			addItemToHashTable(i, key, item);
		}	
	}
	
	/**
	 * Adds an item to a hash table.
	 * @param hashtableIndex  The hash table index.
	 * @param key The (chuked) key.
	 * @param value The value to store.
	 */
	private void addItemToHashTable(int hashtableIndex,int key,BitSetWithID value){
		long[] data = value.toLongArray();
		final long[] newList;
		if(storage.containsKey(hashtableIndex,key)){
			long[] oldList = storage.get(hashtableIndex,key);
			newList = new long[oldList.length+data.length];
			//copy the old data
			for(int i = 0 ; i < oldList.length ; i++){
				newList[i] = oldList[i];
			}
			//copy the new data
			for(int i = oldList.length  ; i < newList.length  ; i++){
				newList[i] = data[i-oldList.length];
			}
		}else{
			newList = data;		
		}
		storage.put(hashtableIndex,key, newList);
	}
	
	/**
	 * 
	 * @param v The bit set to chunk
	 * @param chunkIndex The index of the chunk
	 * @return 
	 */
	private int getChunkHash(BitSetWithID v,int chunkIndex){
		int hash = 1;
		for(int i = 0 ; i < bitsPerChunk ;i ++){
			if(v.get(chunkIndex * bitsPerChunk + i)){
				int mask = 1;
				mask = mask << i;
				hash = (hash | mask);
			}
		}
		return hash;
	}
	
	/**
	 * Query the data set for query and return the nearest neighbor.
	 */
	public Collection<BitSetWithID> query(final BitSetWithID query){
		return query(query,1);
	}
	
	/**
	 * Query the data set for query and return the neighbors.
	 * @param query The query bit set.
	 * @param maxNumNeighbors The maximum amount of neighbors returned.
	 * @return A collection of neighbors that is either empty or a set of true neighbors in 
	 * Hamming space. The number of neighbors is smaller or equal to maxNumNeigbors.
	 */
	public Collection<BitSetWithID> query(final BitSetWithID query,final int maxNumNeighbors){
		HashMap<Long,BitSetWithID> results = new HashMap<Long, BitSetWithID>();
		int increment = Math.max(2, numBits/64 + 1); //number of longs = data + identifier
		
		//for each chunk query the respective hash map
		for(int i = 0 ; i < numberOfChunks ; i++){
			int key = getChunkHash(query, i);
			//Create a set of modified keys
			HashSet<Integer> keys = new HashSet<>();
			keys.add(key);
			//Here serious optimization is possible by limiting the search radius
			//in some edge cases, check the papers for suggestions
			addModifiedKeysToSet(key,keys,0,hammingSearchRadiusPerSubstring,bitsPerChunk);
			//warn if the number of keys is big
			if(keys.size()>10000){
				LOG.warning("Query with more than 10k keys ("  + keys.size() + "). Is parameter optimization possible?");
			}
		
			//For each (modified) key
			for(int k : keys){
				//if the map contains the key
				if(storage.containsKey(i,k)){
					//get the element
					long[] data = storage.get(i,k);
					//for each element
					for(int j = 0 ; j < data.length ; j+=increment ){
						
						long identifier = data[j];
						//if the result hash map already contains the 
						//neighbor, skip it
						//else
						if(!results.containsKey(identifier)){
							long[] dataItem = new long[increment];
							for(int t = 0 ; t < increment ; t++){
								dataItem[t] = data[j+t];
							}
							//check if it is a real neighbor, by calculating the hamming distance
							BitSetWithID neighbor = BitSetWithID.fromLongArray(dataItem);
							boolean isRealNeighbor = neighbor.hammingDistance(query) <= hammingSearchRadius;
							//if it is a real neighbor, place it in the result map
							if(isRealNeighbor) 
								results.put(identifier,neighbor);
						}
					}
				}
			}
		}
		
		//Limit the number of neighbors to the maximum allowed
		
		List<BitSetWithID> neighbors = new ArrayList<BitSetWithID>(results.values());
		
		if(neighbors.size()>maxNumNeighbors * 10){
			LOG.warning("More than 10 times the requested neighbors found.");
		}
		
		//Sort neighbors, closest to query (in Hamming space) first
		Collections.sort(neighbors,new Comparator<BitSetWithID>() {
				@Override
				public int compare(BitSetWithID o1, BitSetWithID o2) {
					int d1 = query.hammingDistance(o1);
					int d2 = query.hammingDistance(o2);
					return Integer.compare(d1, d2);
				}
			});
			
		//keep only the requested amount of nearest neighbors
		if(neighbors.size() > maxNumNeighbors){	
			neighbors = neighbors.subList(0, maxNumNeighbors);
		}
				
		return neighbors;
	}
	
	
	/**
	 * Accumulates a set of keys by modifying the original bit by bit.
	 * 
	 * @param original The original key
	 * @param set The accumulator set. A set since duplicates are unwanted.
	 * @param currentLevel The current level with respect to the first key, needed for recursion.
	 * @param maxLevel The max level with respect to the first key, the stop condition for recursion.
	 */
	private static  void addModifiedKeysToSet(int original,HashSet<Integer> set,int currentLevel,int maxLevel, int bitsPerChunk){
		//for each bit
		for(int i = 0 ; i < bitsPerChunk ;i ++){
			//flip bit i and place it in 
			//results[i]
			int mask = 1;
			mask = mask << i;
			final int newKey;
			//bit i is set (1)
			if(0 < (original & mask)){
				//make it zero
				int inverseMask = (Integer.MAX_VALUE ^ mask);
				newKey = (original & inverseMask);					
			}else{
				//bit i is zero, set it
				newKey = (original | mask);
			}
			//add the new key
			set.add(newKey);
			//go one level deeper, if needed (depth first).
			if(maxLevel != currentLevel+1){
				addModifiedKeysToSet(newKey, set, currentLevel+1, maxLevel, bitsPerChunk);	
			}
		}
	}

	/**
	 * The amount of memory needed is multiplied by the number of hash tables in use and overhead.
	 * @return the number of items in the set.
	 */
	public int size() {
		return storage.size(0);
	}

	/**
	 * Return either the complete data set or limited to numItems.
	 * Do not call this method for sets that do not fit into memory.
	 * 
	 * @param numItems the number of items to return. Use Integer.MAX_INTEGER for the max amount.
	 * @return
	 */
	public List<BitSetWithID> getDataSet(int numItems) {
		List<BitSetWithID> dataset = new ArrayList<BitSetWithID>();
		Set<Integer> keys = storage.getKeys(0);
		int increment = Math.max(2, numBits/64 + 1); //number of longs = data + identifier
		for(Integer key : keys){
			long[] data = storage.get(0,key);
			for(int j = 0 ; j < data.length ; j+=increment ){
				long[] dataItem = new long[increment];
				for(int t = 0 ; t < increment ; t++){
					dataItem[t] = data[j+t];
				}
				BitSetWithID item = BitSetWithID.fromLongArray(dataItem);
				dataset.add(item);
			}
			if(dataset.size() > numItems)
				break;
		}
		return dataset;
	}
	
	public void close(){
		storage.close();
	}
	
	public static void main(String... args){
		int key = 0;
		for(int i = 1;i < 6 ; i++){
			HashSet<Integer> keys = new HashSet<>();
			keys.add(key);
			addModifiedKeysToSet(key,keys,0,i,32);
			System.out.println(i + " " + keys.size());
		}
	} 
}
