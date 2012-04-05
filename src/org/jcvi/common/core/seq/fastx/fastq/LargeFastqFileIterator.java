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

import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code LargeFastQFileIterator} is an Iterator of {@link FastqRecord}s meant for large
 * fastq files (although small fastqs will work too).
 * @author dkatzel
 *
 *
 */
public final class LargeFastqFileIterator extends AbstractBlockingCloseableIterator<FastqRecord> implements CloseableIterator<FastqRecord>{

   
    private final File fastQFile;
    private final FastqQualityCodec qualityCodec;
    
    public static LargeFastqFileIterator createNewIteratorFor(File fastQFile,FastqQualityCodec qualityCodec){
    	LargeFastqFileIterator iter = new LargeFastqFileIterator(fastQFile, qualityCodec);
    	iter.start();
    	
    	return iter;
    }
    private LargeFastqFileIterator(File fastQFile,FastqQualityCodec qualityCodec){
        this.fastQFile = fastQFile;
        this.qualityCodec = qualityCodec;
    }

	@Override
	protected void backgroundThreadRunMethod() {
		try {
        	FastqFileVisitor visitor = new AbstractFastqFileVisitor(qualityCodec) {
				
        		 @Override
        	     protected FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord(String id,
        	             NucleotideSequence nucleotides,
        	             QualitySequence qualities, String optionalComment) {
        	         FastqRecord record = new DefaultFastqRecord(id,nucleotides, qualities,optionalComment);
        	         blockingPut(record);
        	         return LargeFastqFileIterator.this.isClosed() ? FastXFileVisitor.EndOfBodyReturnCode.STOP_PARSING : FastXFileVisitor.EndOfBodyReturnCode.KEEP_PARSING;
        	     }
        		 @Override
        		    public FastXFileVisitor.DeflineReturnCode visitDefline(String id, String optionalComment) {
        		        super.visitDefline(id, optionalComment);
        		        return FastXFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD;
        		    }
			};
            FastqFileParser.parse(fastQFile, visitor);
       } catch (IOException e) {
            
            //should never happen
            throw new RuntimeException(e);
        }
		
	}
    
    
    
}
