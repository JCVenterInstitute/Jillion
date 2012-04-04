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

import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
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
    public FastXFileVisitor.DeflineReturnCode visitDefline(String id, String optionalComment) {
        super.visitDefline(id, optionalComment);
        accept= filter.accept(id);
        return accept?FastXFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD : FastXFileVisitor.DeflineReturnCode.SKIP_CURRENT_RECORD;
    }
    /**
     * Visit the following {@link FastQRecord} which has been
     * accepted by the filter.
     * @param fastQ the fastQRecord being visited.
     */
    protected abstract FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord(FastQRecord fastQ);
    
    @Override
    public FastXFileVisitor.EndOfBodyReturnCode visitEndOfBody() {
        if(accept){
           super.visitEndOfBody();
        }
        return FastXFileVisitor.EndOfBodyReturnCode.KEEP_PARSING;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     protected FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord(String id,
             NucleotideSequence nucleotides,
             QualitySequence qualities, String optionalComment) {
         return visitFastQRecord(new DefaultFastQRecord(id, nucleotides, qualities,optionalComment));
     }
   
}
