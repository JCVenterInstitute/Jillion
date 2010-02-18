/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.io.IOUtil;

public class ReadTrimUtil {

    public static ReadTrimMap readReadTrimsFromStream(InputStream in){
        Scanner scanner =   new Scanner(in);
        try{
        DefaultReadTrimMap.Builder builder = new DefaultReadTrimMap.Builder();
        while(scanner.hasNextLine()){
            
            String euid = scanner.next();
            String seqName = scanner.next();
            Range clrRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                    scanner.nextLong(), scanner.nextLong());
            Range clvRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                    scanner.nextLong(), scanner.nextLong());
            Range clbRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                    scanner.nextLong(), scanner.nextLong());
            Map<TrimType, Range> trimMap =new EnumMap<TrimType, Range>(TrimType.class);
            trimMap.put(TrimType.CLR, clrRange);
            trimMap.put(TrimType.CLV, clvRange);
            trimMap.put(TrimType.CLB, clbRange);
            builder.addReadTrim(seqName,new DefaultReadTrim(seqName, trimMap));  
            scanner.nextLine();
        }
        return builder.build();
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }
    public static ReadTrimMap readReadTrimsFromFile(File file) throws FileNotFoundException{
        return readReadTrimsFromStream(new FileInputStream(file));
    }
    
  
}
