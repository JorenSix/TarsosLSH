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
package be.tarsos.mih;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import be.tarsos.mih.storage.MapDBStorage;

public class TarsosMIH {

	public static void main(String[] args) throws IOException {
		
		MultiIndexHasher mih = new MultiIndexHasher(64, 8, 4, new MapDBStorage(4,"test.db"));    
        Iterator<String> iterator = Files.lines(Paths.get(args[0])).iterator();

        int lineNumber = 0;
        while (iterator.hasNext()) {
            String s = iterator.next();
            long[] l = {Long.parseLong(s)};
        	mih.add(new BitSetWithID(lineNumber, BitSet.valueOf(l)));
            lineNumber++;
        }
        
        System.out.println(lineNumber);
        System.out.println(mih.size());
        
        Path path = Paths.get(args[1]);
        Stream<String> lines = Files.lines(path);
        long start = System.currentTimeMillis();
        lines.forEach(s -> {
        	long[] l = {Long.parseLong(s)};
        	BitSetWithID q = new BitSetWithID(mih.size(), BitSet.valueOf(l));
        	
        	Collection<BitSetWithID> knn = mih.query(q,100);
        	/*
        	int i = 0;
        	
        	System.out.printf("%03d;",i);
        	System.out.print(q);
        	System.out.printf(";%02d\n",q.hammingDistance(q));
        	for(BitSetWithID n: knn){
        		i++;
        		System.out.printf("%03d;",i);
        		System.out.print(n);
        		System.out.printf(";%02d\n",q.hammingDistance(n));
        	}
        	System.out.println("\n");
        	*/
        });
        System.out.printf("Took %d ms per query\n",(System.currentTimeMillis()-start)/10000);
        lines.close();
	}
}
