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

import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastQReadIdVisitor implements FastQFileVisitor{

    @Override
    public void visitLine(String line) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        visitRead(id, optionalComment);
        return false;
    }
    protected abstract void visitRead(String id, String optionalComment);

    @Override
    public boolean visitEndBlock() {
        // keep reading
        return true;
    }

    @Override
    public void visitNucleotides(NucleotideSequence nucleotides) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEncodedQualities(String encodedQualities) {
        // TODO Auto-generated method stub
        
    }

   
    
    
    

}
