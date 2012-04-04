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

package org.jcvi.common.core.seq.fastx.fasta;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class LargeFastaIdIterator extends AbstractBlockingCloseableIterator<String>{

    private final File fastaFile;
    
    public static LargeFastaIdIterator createNewIteratorFor(File fastaFile){
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(fastaFile);
		iter.start();
    	
    	return iter;
    }
	
    /**
     * @param fastaFile
     */
    private LargeFastaIdIterator(File fastaFile) {
        this.fastaFile = fastaFile;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
    	FastaVisitor visitor = new FastaVisitor() {
    		@Override
			public DeflineReturnCode visitDefline(String defline) {
				String id =FastaUtil.parseIdentifierFromIdLine(defline);
				LargeFastaIdIterator.this.blockingPut(id);
				return DeflineReturnCode.SKIP_CURRENT_RECORD;
			}

			@Override
			public void visitLine(String line) {}

			@Override
			public void visitFile() {}

			@Override
			public void visitEndOfFile() {}

			
			@Override
			public void visitBodyLine(String bodyLine) {}

			@Override
			public EndOfBodyReturnCode visitEndOfBody() {
				return EndOfBodyReturnCode.KEEP_PARSING;
			}
            
            
        };
        try {
            FastaParser.parseFasta(fastaFile, visitor);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
