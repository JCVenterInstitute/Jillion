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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;


public abstract class AbstractFastQFileVisitor <T extends FastQRecord> implements FastQFileVisitor{

    private boolean initialized=false;

    protected void checkNotYetInitialized(){
        if(initialized){
            throw new IllegalStateException("already initialized, can not visit more records");
        }
    }    


    @Override
    public void visitEndOfFile() {
        checkNotYetInitialized();
        initialized=true;        
    }

    @Override
    public void visitLine(String line) {
        checkNotYetInitialized();
    }
    
    @Override
    public void visitEncodedQualities(String encodedQualities) {
    }

    @Override
    public void visitEndBlock() {
    }

    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
    }

    @Override
    public void visitFile() {
        
    }
}
