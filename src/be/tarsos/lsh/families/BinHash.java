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

package be.tarsos.lsh.families;

import be.tarsos.lsh.Vector;

import java.util.Random;

public class BinHash implements HashFunction
{
    /**
     * Implements a random Binary Projection hash.
     * Hash returns 0 if dot product is negative/0, 1 if positive
     */
    private static final long serialVersionUID = -3784656820380622717L;
    private Vector randomProjection;

    public BinHash(int dimensions, int seed)
    {
        Random rand = new Random(seed);
        randomProjection = new Vector(dimensions);
        for (int d = 0; d < dimensions; d++)
        {
            //mean 0
            //standard deviation 1.0
            double val = rand.nextGaussian();
            randomProjection.set(d, val);
        }
    }

    /**
     *
     * @param vector
     *            The vector to hash. Can have any number of dimensions.
     * @return 0 if dot product is negative/0, 1 if positive
     */
    public int hash(Vector vector)
    {
        return ((vector.dot(randomProjection)<=0)?0:1);
    }
}
