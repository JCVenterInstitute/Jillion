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
public class TestBlosum90 extends AbstractBlosumTest{

	public TestBlosum90() {
		super(BlosumMatrices.blosum90());
	}

	@Test
	public void spotCheck(){
		
		AminoAcidSubstitutionMatrix blosum90 = getMatrix();
		assertEquals(5F,
				blosum90.getValue(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(8F,
				blosum90.getValue(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-3F,
				blosum90.getValue(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(-1F,
				blosum90.getValue(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
		assertEquals(1F,
				blosum90.getValue(AminoAcid.STOP, AminoAcid.STOP),
				0F);
		assertEquals(-6F,
				blosum90.getValue(AminoAcid.STOP, AminoAcid.Alanine),
				0F);
	}
	
	
}
