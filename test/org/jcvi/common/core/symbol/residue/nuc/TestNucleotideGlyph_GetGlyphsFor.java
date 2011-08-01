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
 * Created on Nov 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideGlyph_GetGlyphsFor {

    @Test
    public void convertGlyph(){
        for(Nucleotide g: Nucleotide.values()){
            final Character uppercase = g.getCharacter();
            assertEquals(g, Nucleotide.parse(uppercase));
            assertEquals(g, Nucleotide.parse(Character.toLowerCase(uppercase)));
        }
    }
    @Test
    public void convertXToN(){
        assertEquals(Nucleotide.Unknown, Nucleotide.parse('X'));
        assertEquals(Nucleotide.Unknown, Nucleotide.parse('x'));
    }
}
