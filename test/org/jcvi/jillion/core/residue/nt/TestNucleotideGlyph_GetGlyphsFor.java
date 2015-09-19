/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
