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

import java.util.Random;

import be.hogent.tarsos.lsh.Vector;

public class EuclideanHash implements HashFunction{
	private Vector randomProjection;
	private int offset;
	private int w;
	
	public EuclideanHash(int dimensions,int w){
		Random rand = new Random();
		this.w = w;
		this.offset = rand.nextInt(w);
		
		randomProjection = new Vector(dimensions);
		for(int d=0; d<dimensions; d++) {
			//mean 0
			//standard deviation 1.0
			double val = rand.nextGaussian();
			randomProjection.set(d, val);
		}
	}
	
	public int hash(Vector vector){
		double hashValue = (vector.dot(randomProjection)+offset)/Double.valueOf(w);
		return (int) Math.round(hashValue);
	}
}
