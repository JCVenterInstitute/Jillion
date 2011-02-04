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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestPhdCodecDecode extends  AbstractTestPhd{

    PhdCodec sut = new PhdCodec();
  
    @Test
    public void decode() throws TraceDecoderException, IOException{
        Phd actual = sut.decode(RESOURCE.getFileAsStream(PHD_FILE));
        assertEquals(expectedQualities, actual.getQualities().decode());        
        assertEquals(expectedPositions, actual.getPeaks().getData().decode());      
        assertEquals(expectedBasecalls, NucleotideGlyph.convertToString(actual.getBasecalls().decode()));
        assertEquals(expectedProperties, actual.getComments());
    }
    
    @Test
    public void encode() throws IOException, TraceDecoderException{
        Phd phd = new DefaultPhd(new DefaultNucleotideEncodedGlyphs(expectedBasecalls), 
                new DefaultQualityEncodedGlyphs(new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE),expectedQualities), 
                new Peaks(ShortGlyph.toArray(expectedPositions)), 
                        expectedProperties,Collections.<PhdTag>emptyList());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        sut.encode(phd, out);
        Phd decodedPhd =sut.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        assertEquals(phd, decodedPhd);
    }
}
