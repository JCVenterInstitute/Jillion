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
package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;


public abstract class AbstractFastqFileVisitor implements FastqFileVisitor{
    private String currentId, currentComment;

    private NucleotideSequence nucleotides;
    private QualitySequence qualities;
    protected final FastqQualityCodec qualityCodec;

    
   
    public AbstractFastqFileVisitor(FastqQualityCodec qualityCodec){
        this.qualityCodec = qualityCodec;
    }
    @Override
    public void visitEndOfFile() {   
    }

    @Override
    public void visitLine(String line) {
    }
    
    @Override
    public void visitEncodedQualities(String encodedQualities) {
        this.qualities = qualityCodec.decode(encodedQualities);
    }

    @Override
    public void visitFile() {       
       
    }
    protected FastqQualityCodec getQualityCodec() {
        return qualityCodec;
    }
    
    @Override
    public FastXFileVisitor.DeflineReturnCode visitDefline(String id, String optionalComment) {
        currentId = id;
        currentComment = optionalComment;
        return FastXFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD;
    }
    
    @Override
    public void visitNucleotides(NucleotideSequence nucleotides) {
        this.nucleotides = nucleotides;
        
    }
    
    
    @Override
    public FastXFileVisitor.EndOfBodyReturnCode visitEndOfBody() {
        return visitFastQRecord(currentId, nucleotides, qualities, currentComment);
        
        
    }
    /**
     * Visit the current {@link FastqRecord}.
     * @param id
     * @param nucleotides
     * @param qualities
     * @param optionalComment
     * @return FastXFileVisitor.EndOfBodyReturnCode
     */
    protected abstract FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord(
            String id, 
            NucleotideSequence nucleotides,
            QualitySequence qualities,
            String optionalComment);
}
