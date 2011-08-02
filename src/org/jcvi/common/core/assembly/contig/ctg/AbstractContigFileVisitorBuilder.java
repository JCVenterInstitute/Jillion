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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ctg;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.DefaultContig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

public abstract class AbstractContigFileVisitorBuilder extends AbstractContigFileVisitor{

    private DefaultContig.Builder currentContigBuilder;
    
    protected abstract void  addContig(Contig<PlacedRead> contig);

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, Direction dir) {
       
        currentContigBuilder.addRead(readId, offset, validRange,basecalls,dir); 
        
        
    }

    @Override
    protected void visitEndOfContig() {
        addContig(currentContigBuilder.build());
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        currentContigBuilder = new DefaultContig.Builder(contigId,
                encodeBasecalls(consensus));
    }
    private DefaultNucleotideSequence encodeBasecalls(String basecalls) {
        return new DefaultNucleotideSequence(Nucleotides.parse(basecalls));
    }
}
