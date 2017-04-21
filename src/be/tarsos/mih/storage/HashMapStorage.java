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

package be.tarsos.mih.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Implements storage using the standard hash map implementation of the java runtime.
 *  
 * @author Joren Six
 *
 */
public class HashMapStorage implements MIHStorage {
	
	private final List<HashMap<Integer, long[]>> hashtables;
	
	public HashMapStorage(int numberOfTables){		
		hashtables = new ArrayList<>();
		for(int i = 0 ; i < numberOfTables ; i++){
			HashMap<Integer, long[]> map = new HashMap<>();
			hashtables.add(map);
		}
	}

	@Override
	public boolean containsKey(int hashtableIndex, int key) {
		return hashtables.get(hashtableIndex).containsKey(key);
	}

	@Override
	public long[] put(int hashtableIndex, int key, long[] newList) {
		return hashtables.get(hashtableIndex).put(key,newList);
	}

	@Override
	public long[] get(int hashtableIndex, int key) {
		return hashtables.get(hashtableIndex).get(key);
	}

	@Override
	public int size(int hashtableIndex) {
		return hashtables.get(hashtableIndex).size();
	}

	@Override
	public Set<Integer> getKeys(int hashtableIndex) {
		return hashtables.get(hashtableIndex).keySet();
	}

	@Override
	public void close() {
		//after close the storage becomes
		//unavailable
		hashtables.clear();
	}
}
