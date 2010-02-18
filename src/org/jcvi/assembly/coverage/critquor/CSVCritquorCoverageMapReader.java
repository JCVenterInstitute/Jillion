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
