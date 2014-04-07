/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestNucleotideGlyph_GetGlyphsFor {

    @Test
    public void convertGlyph(){
        for(Nucleotide g: Nucleotide.VALUES){
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
