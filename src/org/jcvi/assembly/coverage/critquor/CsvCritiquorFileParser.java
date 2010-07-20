/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
        scanner.close();
    }
}
