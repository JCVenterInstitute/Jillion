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
package org.jcvi.jillion.trace.sanger.phd;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdWriter extends AbstractTestPhd{
    private String id = "1095595674585";
    @Test
    public void write() throws IOException, DataStoreException{
        Phd phd = new DefaultPhd(
        		id,
        		new NucleotideSequenceBuilder(expectedBasecalls).build(), 
        		expectedQualities, 
                expectedPositions,
                expectedProperties);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PhdWriter.writePhd(phd, out);
        PhdDataStore expected =  new PhdFileDataStoreBuilder(RESOURCE.getFile(PHD_FILE)).build();
        
        SinglePhdVisitor visitor = new SinglePhdVisitor();
        PhdBallParser.create(new ByteArrayInputStream(out.toByteArray())).accept(visitor);

        assertEquals(expected.get(id),visitor.phd);
    }
    
    private static class SinglePhdVisitor extends AbstractPhdBallVisitor{
    	private Phd phd;

		@Override
		public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id,
				Integer version) {
			return new AbstractPhdVisitor(id, version) {
				
				@Override
				protected void visitPhd(String id, Integer version,
						NucleotideSequence basecalls, QualitySequence qualities,
						PositionSequence positions, Map<String, String> comments,
						List<PhdWholeReadItem> wholeReadItems, List<PhdReadTag> readTags) {
					phd = new DefaultPhd(id, basecalls, qualities, positions, comments,wholeReadItems, readTags);
					
				}
			};
		}
		
    	
    }
}
