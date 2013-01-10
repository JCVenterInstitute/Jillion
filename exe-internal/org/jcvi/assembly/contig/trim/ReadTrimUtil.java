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
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;

public class ReadTrimUtil {

    public static ReadTrimMap readReadTrimsFromStream(InputStream in){
        Scanner scanner =   new Scanner(in);
        try{
        DefaultReadTrimMap.Builder builder = new DefaultReadTrimMap.Builder();
        while(scanner.hasNextLine()){
            //skip euid
            scanner.next();
            String seqName = scanner.next();
            Range clrRange = Range.of(CoordinateSystem.RESIDUE_BASED, 
                    scanner.nextLong(), scanner.nextLong());
            Range clvRange = Range.of(CoordinateSystem.RESIDUE_BASED, 
                    scanner.nextLong(), scanner.nextLong());
            Range clbRange = Range.of(CoordinateSystem.RESIDUE_BASED, 
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
