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

import java.util.Random;

public class BinHashFamily implements HashFamily
{
    /**
     *
     */
    private static final long serialVersionUID = 3406464542795652263L;
    private final int dimensions;
    private int seed;
    private Random rand;

    public BinHashFamily(int seed, int dimensions)
    {
        this.dimensions = dimensions;
        this.rand = new Random(seed);
    }

    @Override
    public HashFunction createHashFunction()
    {
        return new BinHash(dimensions, this.rand.nextInt());
    }

    /**
     * @param hashes The raw binary hashes that need to be combined.
     * @return An Integer representing the binary array of hashes
     */
    @Override
    public Integer combine(int[] hashes)
    {
        int ret = 0;
        for (int hash : hashes)
        {
            ret = (ret << 1) + ((hash == 0) ? 0 : 1);
        }
        return ret;
    }

    @Override
    public DistanceMeasure createDistanceMeasure()
    {
        return new CityBlockDistance();
    }
}
