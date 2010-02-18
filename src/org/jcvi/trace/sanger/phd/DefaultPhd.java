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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

public class DefaultPhd implements Phd {

    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    
    private final NucleotideEncodedGlyphs basecalls;
    private final EncodedGlyphs<PhredQuality> qualities;
    private final Peaks peaks;
    private final Properties comments;
    private final List<PhdTag> tags;
    
    public DefaultPhd(NucleotideEncodedGlyphs basecalls,
            EncodedGlyphs<PhredQuality> qualities,
            Peaks peaks, Properties comments,
            List<PhdTag> tags){
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.peaks = peaks;
        this.comments = comments;
        this.tags = tags;
    }
    
    public DefaultPhd(NucleotideEncodedGlyphs basecalls,
            EncodedGlyphs<PhredQuality> qualities,
            Peaks peaks){
        this(basecalls, qualities, peaks, new Properties(),Collections.<PhdTag>emptyList());
    }
    
    @Override
    public Properties getComments() {
        return comments;
    }

    @Override
    public Peaks getPeaks() {
        return peaks;
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return basecalls;
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + basecalls.decode().hashCode();
        result = prime * result
                + comments.hashCode();
        result = prime * result + peaks.hashCode();
        result = prime * result
                + qualities.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultPhd))
            return false;
        DefaultPhd other = (DefaultPhd) obj;
       if (!basecalls.decode().equals(other.basecalls.decode()))
            return false;
        if (!comments.equals(other.comments))
            return false;
        if (!peaks.getData().decode().equals(other.peaks.getData().decode()))
            return false;
        if (!qualities.decode().equals(other.qualities.decode()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return NucleotideGlyph.convertToString(basecalls.decode());
    }

    @Override
    public int getNumberOfTracePositions() {
        EncodedGlyphs<ShortGlyph> encodedPeaks= peaks.getData();        
        int lastIndex= (int)encodedPeaks.getLength() -1;
        return encodedPeaks.get(lastIndex).getNumber();
    }

    @Override
    public List<PhdTag> getTags() {
        return tags;
    }
    
    
    
}
