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

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;


/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaRecordVisitor<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S,T>> extends AbstractFastaVisitor {

    private final FastaRecordFactory<S,T,F> recordFactory;
    
    
    /**
     * @param recordFactory
     */
    public AbstractFastaRecordVisitor(FastaRecordFactory<S,T,F> recordFactory) {
        this.recordFactory = recordFactory;
    }

    @Override
    public boolean visitRecord(String id, String comment, String sequence) {
        return visitFastaRecord(
                recordFactory.createFastaRecord(id, comment, sequence));
    }
    /**
     * Visit the current FastaRecord.
     * @param fastaRecord the built fasta record being visited.
     * @return {@code true} if the parser should keep parsing
     * {@code false} if it should stop parsing.
     */
    protected abstract boolean visitFastaRecord(F fastaRecord);

}
