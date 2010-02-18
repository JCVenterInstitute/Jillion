/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.jcvi.Range;


public class CSVCritquorCoverageMapReader implements CritquorCoverageMapReader {

    @Override
    public CritiquorCovereageMap read(InputStream inputStream)
            throws IOException {
        DefaultCritiquorCoverageMap.Builder builder = new DefaultCritiquorCoverageMap.Builder();
        
        Scanner scanner = new Scanner(inputStream);
        //skip header
        scanner.nextLine();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            String key = columns[5];
            Range targetRange = Range.buildRange(Long.parseLong(columns[10]), 
                                                Long.parseLong(columns[12])-1);
            builder.addTargetRange(key, targetRange);
        }
        return builder.build();
        
    }

}
