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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.collections.api.block.procedure.primitive.IntProcedure;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import be.tarsos.mih.storage.MIHStorage;

/**
 * Implements storage based on eclipse collections. 
 * The eclipse collections should be optimized for speed and memory usage.
 * 
 * @author Joren Six
 *
 */
public class EclipseStorage implements MIHStorage {
	
	private final List<IntObjectHashMap<long[]>> hashtables;
	
	public EclipseStorage(int numberOfTables){		
		hashtables = new ArrayList<>();
		for(int i = 0 ; i < numberOfTables ; i++){
			IntObjectHashMap<long[]> map = new IntObjectHashMap<>();
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
	   HashSet<Integer> keySet = new HashSet<Integer>();
	   MutableIntSet set = hashtables.get(hashtableIndex).keySet();
	   set.each(new IntProcedure() {
		   private static final long serialVersionUID = 5298541587175606959L;
			@Override
			public void value(int arg0) {
				keySet.add(arg0);
			}
	   });
	   return keySet;
	}

	@Override
	public void close() {
		//after close the storage becomes
		//unavailable
		hashtables.clear();
	}
}
