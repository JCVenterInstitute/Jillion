/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

/**
 * Casava 1.8 changes the fastq mated read names
 * to have the mate pairs have the same read name
 * and the optional comment to be the mate info.
 * Since the mates are in different files this usually isn't 
 * a problem unless you are combining reads from many files (like an assembler).
 * 
 * @author dkatzel
 *
 *
 */
public class TestCasava18 {
    
    @Test
    public void parseMateInfoCorrectly() throws FileNotFoundException, IOException{
    	
    	
        FastqVisitor visitor = new FastqVisitor() {
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			@Override
			public void halted(){
				//no-op
	    	}
			@Override
			public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
					String id, String optionalComment) {
				assertEquals("EAS139:136:FC706VJ:2:5:1000:12850 1:Y:18:ATCACG", id);
				return null;
			}
		};

        ResourceHelper resources = new ResourceHelper(TestCasava18.class);
        FastqFileParser.create(resources.getFile("files/casava1.8.fastq")).parse(visitor);
    }
}
