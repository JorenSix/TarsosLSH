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
*  Info    : http://tarsos.0110.be/tag/TarsosLSH
*  Github  : https://github.com/JorenSix/TarsosLSH
*  Releases: http://tarsos.0110.be/releases/TarsosLSH/
* 
*/
package be.tarsos.mih.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * Storage engine that uses MapDB. 
 * 
 * It stores the data to a file and allows to store more elements that would fit in memory. 
 * 
 * @author Joren Six
 */
public class MapDBStorage implements MIHStorage{
	
	private final DB db;
	private final List<HTreeMap<Integer, long[]>> hashtables;
	
	public MapDBStorage(int numberOfTables,String fileName){
		
		hashtables = new ArrayList<>();
		
		db = DBMaker.fileDB(fileName).fileMmapEnable().closeOnJvmShutdown().make();
		
		for(int i = 0 ; i < numberOfTables ; i++){
			HTreeMap<Integer, long[]> map = db.hashMap("map_"+i)
					.keySerializer(Serializer.INTEGER)
					.valueSerializer(Serializer.LONG_ARRAY)
					.counterEnable()
					.createOrOpen();
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
		return hashtables.get(hashtableIndex).getKeys();
	}
	
	public void close() {
		db.close();
	}
	
	public void commit() {
		db.commit();
	}
}
