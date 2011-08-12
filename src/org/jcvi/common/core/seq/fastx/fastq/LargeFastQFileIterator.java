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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code LargeFastQFileIterator} is an Iterator of FastQRecords meant for large
 * fastq files (although small fastqs will work too).
 * @author dkatzel
 *
 *
 */
public class LargeFastQFileIterator extends AbstractBlockingCloseableIterator<FastQRecord> implements CloseableIterator<FastQRecord>{

   
    private final File fastQFile;
    private final FastQQualityCodec qualityCodec;
    
    public static LargeFastQFileIterator createNewIteratorFor(File fastQFile,FastQQualityCodec qualityCodec){
    	LargeFastQFileIterator iter = new LargeFastQFileIterator(fastQFile, qualityCodec);
    	iter.start();
    	
    	return iter;
    }
    private LargeFastQFileIterator(File fastQFile,FastQQualityCodec qualityCodec){
        this.fastQFile = fastQFile;
        this.qualityCodec = qualityCodec;
    }

	@Override
	protected void backgroundThreadRunMethod() {
		try {
        	FastQFileVisitor visitor = new AbstractFastQFileVisitor(qualityCodec) {
				
        		 @Override
        	     protected boolean visitFastQRecord(String id,
        	             NucleotideSequence nucleotides,
        	             QualitySequence qualities, String optionalComment) {
        	         FastQRecord record = new DefaultFastQRecord(id,nucleotides, qualities,optionalComment);
        	         blockingPut(record);
        	         return !LargeFastQFileIterator.this.isClosed();
        	     }
        		 @Override
        		    public boolean visitBeginBlock(String id, String optionalComment) {
        		        super.visitBeginBlock(id, optionalComment);
        		        return true;
        		    }
        		    @Override
        		    public void visitEndOfFile() {
        		       LargeFastQFileIterator.this.finishedIterating();
        		    }
			};
            FastQFileParser.parse(fastQFile, visitor);
       } catch (IOException e) {
            
            //should never happen
            throw new RuntimeException(e);
        }
		
	}
    
    
    
}
