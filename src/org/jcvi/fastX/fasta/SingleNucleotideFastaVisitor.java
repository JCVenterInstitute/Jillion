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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fastX.fasta;

import org.jcvi.fastX.fasta.seq.AbstractNucleotideFastaVisitor;
import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;

/**
 * {@code SingleNucleotideFastaVisitor} is a {@link FastaVisitor}
 * that only accepts at most one FastaRecord.
 * @author dkatzel
 *
 *
 */
public abstract class SingleNucleotideFastaVisitor extends AbstractNucleotideFastaVisitor{

    private NucleotideSequenceFastaRecord record=null;
    @Override
    protected synchronized boolean visitNucleotideFastaRecord(
            NucleotideSequenceFastaRecord fastaRecord) {
        //only accept first record
        record = fastaRecord;        
        return keepParsingFasta();
    }
    protected abstract boolean keepParsingFasta();
    public synchronized NucleotideSequenceFastaRecord getRecord() {
        return record;
    }
    
    

}
