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

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class AbstractLargeFastaRecordIterator<S extends Symbol, T extends Sequence<S>,F extends FastaRecord<S,T>> extends AbstractBlockingCloseableIterator<F>{

    private final File fastaFile;
    private final FastaRecordFactory<F> recordFactory;
    
    
    
    /**
     * @param fastaFile
     * @param recordFactory
     */
    public AbstractLargeFastaRecordIterator(File fastaFile,
            FastaRecordFactory<F> recordFactory) {
        this.fastaFile = fastaFile;
        this.recordFactory = recordFactory;
    }



    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        FastaVisitor visitor = new AbstractFastaRecordVisitor<S,T, F>(recordFactory) {

            @Override
            protected boolean visitFastaRecord(F fastaRecord) {
                blockingPut(fastaRecord);
                return !AbstractLargeFastaRecordIterator.this.isClosed();
            }

            @Override
            public void visitEndOfFile() {                
                super.visitEndOfFile();
                AbstractLargeFastaRecordIterator.this.finishedIterating();
            }
        };
        try {
            FastaParser.parseFasta(fastaFile, visitor);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
