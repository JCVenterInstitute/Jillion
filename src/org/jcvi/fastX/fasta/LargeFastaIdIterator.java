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

package org.jcvi.fastX.fasta;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.util.AbstractBlockingCloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class LargeFastaIdIterator extends AbstractBlockingCloseableIterator<String>{

    private final File fastaFile;
    
    
    /**
     * @param fastaFile
     */
    public LargeFastaIdIterator(File fastaFile) {
        this.fastaFile = fastaFile;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        AbstractFastaVisitor visitor = new AbstractFastaVisitor() {
            
            @Override
            public boolean visitRecord(String id, String comment, String entireBody) {
                LargeFastaIdIterator.this.blockingPut(id);
                return LargeFastaIdIterator.this.isClosed();
            }

            @Override
            public void visitEndOfFile() {
                super.visitEndOfFile();
                LargeFastaIdIterator.this.finishedIterating();
            }
        };
        try {
            FastaParser.parseFasta(fastaFile, visitor);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
