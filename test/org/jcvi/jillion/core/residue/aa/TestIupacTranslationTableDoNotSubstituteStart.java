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
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestIupacTranslationTableDoNotSubstituteStart {

	@Test
	public void ctgShouldBeL(){
		assertEquals("L", IupacTranslationTables.STANDARD.translate(seq("CTG"),false).get(0).toString());
	}
	
	@Test
	public void ttgShouldBeL(){
		assertEquals("L", IupacTranslationTables.STANDARD.translate(seq("TTG"),false).get(0).toString());
	}
	
	@Test
	public void ctgShouldBeM(){
		assertEquals("M", IupacTranslationTables.STANDARD.translate(seq("CTG"),true).get(0).toString());
	}
	
	@Test
	public void ttgShouldBeM(){
		assertEquals("M", IupacTranslationTables.STANDARD.translate(seq("TTG"),true).get(0).toString());
	}
	
	private NucleotideSequence seq(String s){
		return new NucleotideSequenceBuilder(s).build();
	}
}
