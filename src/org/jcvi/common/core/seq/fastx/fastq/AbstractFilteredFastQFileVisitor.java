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

import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * {@code AbstractFilteredFastQFileVisitor} is an implementation
 * of {@link AbstractFastQFileVisitor} that also takes a {@link FastXFilter}
 * to filter the records being visited.
 * 
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFilteredFastQFileVisitor extends AbstractFastQFileVisitor{
    private boolean accept;
    private final FastXFilter filter;
    
    /**
     * @param filter
     */
    public AbstractFilteredFastQFileVisitor(FastXFilter filter,FastQQualityCodec qualityCodec) {
        super(qualityCodec);
        this.filter = filter;
    }

    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        super.visitBeginBlock(id, optionalComment);
        accept= filter.accept(id);
        return accept;
    }
    /**
     * Visit the following {@link FastQRecord} which has been
     * accepted by the filter.
     * @param fastQ the fastQRecord being visited.
     */
    protected abstract boolean visitFastQRecord(FastQRecord fastQ);
    
    @Override
    public boolean visitEndBlock() {
        if(accept){
           super.visitEndBlock();
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     protected boolean visitFastQRecord(String id,
             NucleotideSequence nucleotides,
             QualitySequence qualities, String optionalComment) {
         return visitFastQRecord(new DefaultFastQRecord(id, nucleotides, qualities,optionalComment));
     }
   
}
