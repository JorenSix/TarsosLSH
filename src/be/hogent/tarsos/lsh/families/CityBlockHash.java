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

package be.hogent.tarsos.lsh.families;

import java.util.Arrays;
import java.util.Random;

import be.hogent.tarsos.lsh.Vector;

public class CityBlockHash implements HashFunction {
	private int w;
	private Vector randomPartition;
	
	public CityBlockHash(int dimensions,int w){
		Random rand = new Random();
		this.w = w;
		
		randomPartition = new Vector(dimensions);
		for(int d=0; d<dimensions; d++) {
			//mean 0
			//standard deviation 1.0
			double val = rand.nextInt(w);
			randomPartition.set(d, val);
		}
	}
	
	public int hash(Vector vector){
		int hash[] = new int[randomPartition.getDimensions()];
		for(int d=0; d<randomPartition.getDimensions(); d++) {
			hash[d] =  (int) (vector.get(d)-randomPartition.get(d) / Double.valueOf(w));
		}
		return Arrays.hashCode(hash);
	}
	
	public String toString(){
		return String.format("w:%d\nrandomPartition:%s",w,randomPartition); 
	}
}
