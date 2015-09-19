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
package org.jcvi.jillion.align;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.BlosumMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.junit.Test;
public class TestBlosum62 extends AbstractBlosumTest{

	public TestBlosum62() {
		super(BlosumMatrices.blosum62());
	}

	@Test
	public void spotCheck(){
		
		AminoAcidSubstitutionMatrix blosum62 = getMatrix();
		assertEquals(4F,
				blosum62.getValue(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(7F,
				blosum62.getValue(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-2F,
				blosum62.getValue(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(0F,
				blosum62.getValue(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
		assertEquals(1F,
				blosum62.getValue(AminoAcid.STOP, AminoAcid.STOP),
				0F);
		assertEquals(-4F,
				blosum62.getValue(AminoAcid.STOP, AminoAcid.Alanine),
				0F);
	}
	
	
}
