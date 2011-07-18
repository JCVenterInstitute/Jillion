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

import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.qual.QualitySequence;


public abstract class AbstractFastQFileVisitor implements FastQFileVisitor{
    private String currentId, currentComment;

    private NucleotideSequence nucleotides;
    private QualitySequence qualities;
    protected final FastQQualityCodec qualityCodec;

    
   
    public AbstractFastQFileVisitor(FastQQualityCodec qualityCodec){
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
    protected FastQQualityCodec getQualityCodec() {
        return qualityCodec;
    }
    
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        currentId = id;
        currentComment = optionalComment;
        return true;
    }
    
    @Override
    public void visitNucleotides(NucleotideSequence nucleotides) {
        this.nucleotides = nucleotides;
        
    }
    
    
    @Override
    public boolean visitEndBlock() {
        return visitFastQRecord(currentId, nucleotides, qualities, currentComment);
        
        
    }
    /**
     * Visit the current {@link FastQRecord}.
     * @param id
     * @param nucleotides
     * @param qualities
     * @param optionalComment
     * @return {@code true} if the fastQ file should continue
     * to be parsed; {@code false} if parsing should stop.
     */
    protected abstract boolean visitFastQRecord(
            String id, 
            NucleotideSequence nucleotides,
            QualitySequence qualities,
            String optionalComment);
}
