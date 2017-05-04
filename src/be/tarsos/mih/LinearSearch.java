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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LinearSearch {
	
	private final List<BitSetWithID> dataset;
	
	private final int hammingRadius;
	
	public LinearSearch(List<BitSetWithID> dataset,int hammingRadius){
		this.dataset = dataset;
		this.hammingRadius = hammingRadius;
	}
	
	public List<BitSetWithID> query(final BitSetWithID query,int k){
		
		//compare every item
		List<BitSetWithID> results = new ArrayList<BitSetWithID>(k);
		for(int i = 0 ; i < dataset.size() ; i++){
			int d = query.hammingDistance(dataset.get(i));
			if(d<hammingRadius){
				results.add(dataset.get(i));
			}
		}
		
		//sort, closest in hamming space first
		Collections.sort(results,new Comparator<BitSetWithID>() {
			@Override
			public int compare(BitSetWithID o1, BitSetWithID o2) {
				int d1 = query.hammingDistance(o1);
				int d2 = query.hammingDistance(o2);
				return Integer.compare(d1, d2);
			}
		});
		
		//keep k nearest neighbors
		if(results.size() > k){	
			results = results.subList(0, k);
		}
		
		return results;
	}

}
