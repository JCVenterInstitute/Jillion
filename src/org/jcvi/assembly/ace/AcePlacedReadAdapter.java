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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public class AcePlacedReadAdapter implements AcePlacedRead{

    private final PlacedRead placedRead;
    private final PhdInfo phdInfo;
    private final int ungappedFullLength;
    /**
     * @param placedRead
     */
    public AcePlacedReadAdapter(PlacedRead placedRead,Date phdDate, File traceFile, int ungappedFullLength) {
        this.placedRead = placedRead;
        String readId = placedRead.getId();
        final String id;
        if(traceFile !=null){
            final String extension = FilenameUtils.getExtension(traceFile.getName());
            if("sff".equals(extension)){        
                id="sff:"+traceFile.getName()+":"+readId;
            }
            else if("scf".equals(extension)){        
                id=traceFile.getName();
            }
            else{
                id= readId;
            }
        }else{
            id= readId;
        }
        this.phdInfo= new DefaultPhdInfo(id, readId+".phd.1", phdDate);
        this.ungappedFullLength = ungappedFullLength;
    }

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        return placedRead.convertReferenceIndexToValidRangeIndex(referenceIndex);
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        return placedRead.convertValidRangeIndexToReferenceIndex(validRangeIndex);
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return placedRead.getSequenceDirection();
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return placedRead.getSnps();
    }
    @Override
    public Range getValidRange() {
        return placedRead.getValidRange();
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return placedRead.getEncodedGlyphs();
    }
    @Override
    public String getId() {
        return placedRead.getId();
    }
    @Override
    public long getLength() {
        return placedRead.getLength();
    }
    @Override
    public long getEnd() {
        return placedRead.getEnd();
    }
    @Override
    public long getStart() {
        return placedRead.getStart();
    }
    @Override
    public String toString() {
        return "AcePlacedReadAdapter [placedRead="
                + placedRead + ", phdInfo=" + phdInfo +  "]";
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        return placedRead.compareTo(o);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedFullLength() {
        return ungappedFullLength;
    }
    
}
