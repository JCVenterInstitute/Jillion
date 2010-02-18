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
 * Created on FeT 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncoder {

    NucleotideGlyphFactory glyphFactory = NucleotideGlyphFactory.getInstance();
    String BasesAsString = "AAAAAAAAAAAATAAAAAAAAAAAATTTAAAAAAAAAAAAAAAAAAAAAAAATAAAAAAAAAAAAAA";
    List<NucleotideGlyph> list = glyphFactory.getGlyphsFor(BasesAsString);
    List<RunLength<NucleotideGlyph>> expectedEncoding = Arrays.asList(
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Adenine,12),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Thymine,1),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Adenine,12),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Thymine,3),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Adenine,24),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Thymine,1),
            new RunLength<NucleotideGlyph>(NucleotideGlyph.Adenine,14)
    );
    @Test
    public void encode(){        
        List<RunLength<NucleotideGlyph>> actual =RunLengthEncoder.encode(list);
        assertEquals(expectedEncoding, actual);
    }
    
    @Test
    public void decode(){
        List<NucleotideGlyph> actual = RunLengthEncoder.decode(expectedEncoding);
        assertEquals(list, actual);
    }
    
    @Test
    public void enocdeEmptyList(){
        assertEquals(new ArrayList<RunLength<NucleotideGlyph>>(), 
                RunLengthEncoder.encode(Collections.<NucleotideGlyph>emptyList()));
    }
}
