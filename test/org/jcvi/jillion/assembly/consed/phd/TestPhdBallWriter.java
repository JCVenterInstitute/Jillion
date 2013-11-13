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
package org.jcvi.jillion.assembly.consed.phd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdBallWriter extends AbstractTestPhd{
    private String id = "1095595674585";
    
    @Test
    public void nullCommentShouldNotBeWritten() throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	PhdWriter writer = new PhdBallWriter(out);
    	writer.close();
    	FileCommentVisitor visitor = new FileCommentVisitor();
    	 PhdBallFileParser.create(new ByteArrayInputStream(out.toByteArray())).accept(visitor);

 		assertNull(visitor.fileComment);
 		
 		assertFalse(new String(out.toByteArray(), IOUtil.UTF_8).startsWith("#"));
    }
    @Test
    public void writeComment() throws IOException{
    	String fileComment = "path to fastq = /path/to/fastq";
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	PhdWriter writer = new PhdBallWriter(out, fileComment);
    	writer.close();
    	FileCommentVisitor visitor = new FileCommentVisitor();
    	 PhdBallFileParser.create(new ByteArrayInputStream(out.toByteArray())).accept(visitor);

 		assertEquals(fileComment,visitor.fileComment);
    }
    
    @Test
    public void writeMultiplePhds() throws IOException, DataStoreException{
    	Phd phd1 = new PhdBuilder(expectedId, 
				new NucleotideSequenceBuilder(expectedBasecalls).build(), expectedQualities)
				.peaks(expectedPositions)
				.comments(expectedProperties)
				.build();
    	
    	Range trimRange = Range.of(300,600);
    	
    	Phd phd2 = new PhdBuilder(expectedId+"trimmed", 
				new NucleotideSequenceBuilder(expectedBasecalls)
    						.trim(trimRange).build(),
    			new QualitySequenceBuilder(expectedQualities)
    						.trim(trimRange).build())
				.peaks(new PositionSequenceBuilder(expectedPositions)
							.trim(trimRange).build())
				.comments(expectedProperties)
				.build();
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	PhdWriter writer = new PhdBallWriter(out);
    	writer.write(phd1);
    	writer.write(phd2);
    	writer.close();
    	
    	PhdDataStore datastore = new PhdFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
									.build();
    	try{
    	assertEquals(2, datastore.getNumberOfRecords());
    	assertEquals(phd1, datastore.get(phd1.getId()));
    	assertEquals(phd2, datastore.get(phd2.getId()));
    	}finally{
    		IOUtil.closeAndIgnoreErrors(datastore);
    	}
    	
    }
    
    @Test
    public void write() throws IOException, DataStoreException{
       
      
        PhdDataStore expected =  new PhdFileDataStoreBuilder(RESOURCE.getFile(PHD_FILE)).build();
        
       
        Phd expectedPhd = expected.get(id);
        
        Phd phd = new PhdBuilder(expectedPhd).build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PhdWriter writer = new SinglePhdWriter(out);
        writer.write(phd);
        writer.close();
        
        SinglePhdVisitor visitor = new SinglePhdVisitor();
        PhdBallFileParser.create(new ByteArrayInputStream(out.toByteArray())).accept(visitor);
		assertEquals(expectedPhd,visitor.phd);
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
    
    private static class FileCommentVisitor extends AbstractPhdBallVisitor{
    	private String fileComment =null;

		@Override
		public void visitFileComment(String comment) {
			this.fileComment = comment;
		}
    	
    	
    }
}
