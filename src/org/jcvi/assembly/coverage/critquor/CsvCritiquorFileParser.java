/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jcvi.Range;

public class CsvCritiquorFileParser implements CritiquorFileParser{

    @Override
    public void parse(File critiquorFile, CritiquorFileVisitor visitor) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(critiquorFile));
        //skip header
        scanner.nextLine();
        visitor.visitStartOfFile();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            
            String[] columns = line.split(",");
            String id = columns[0];
            String region = columns[5];
            Range designedRange = Range.buildRange(Long.parseLong(columns[10]), 
                                                Long.parseLong(columns[12])-1);
            String forwardPrimer = columns[26];
            String reversePrimer = columns[27];
            visitor.visitAmplicon(id, region, designedRange, forwardPrimer, reversePrimer);
        }
        visitor.visitEndOfFile();
    }
}
