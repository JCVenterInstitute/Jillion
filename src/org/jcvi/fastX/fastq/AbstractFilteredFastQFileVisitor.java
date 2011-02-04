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

package org.jcvi.fastX.fastq;

import org.jcvi.fastX.FastXFilter;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

/**
 * {@code AbstractFilteredFastQFileVisitor} is an implementation
 * of {@link AbstractFastQFileVisitor} that also takes a {@link FastXFilter}
 * to filter the records being visited.
 * 
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFilteredFastQFileVisitor<T extends FastQRecord> extends AbstractFastQFileVisitor<T>{
    private String currentId;
    private String currentComment;
    private EncodedGlyphs<PhredQuality> currentQualities;
    private NucleotideEncodedGlyphs currentBases;
    private boolean accept;
    private final FastXFilter filter;
    private final FastQQualityCodec qualityCodec;
    
    /**
     * @param filter
     */
    public AbstractFilteredFastQFileVisitor(FastXFilter filter,FastQQualityCodec qualityCodec) {
        this.filter = filter;
        this.qualityCodec = qualityCodec;
    }

    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        currentId = id;
        currentComment = optionalComment;
        accept= filter.accept(id);
        return accept;
    }
    /**
     * Visit the following {@link FastQRecord} which has been
     * accepted by the filter.
     * @param fastQ the fastQRecord being visited.
     */
    protected abstract void visitFastQRecord(FastQRecord fastQ);
    
    @Override
    public boolean visitEndBlock() {
        if(accept){
            visitFastQRecord( new DefaultFastQRecord(
                    currentId, currentBases, 
                    currentQualities
                    ,currentComment));
        }
        return true;
    }

    @Override
    public void visitEncodedQualities(String encodedQualities) {
       currentQualities = qualityCodec.decode(encodedQualities);
    }

    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        currentBases = nucleotides;
    }
}
