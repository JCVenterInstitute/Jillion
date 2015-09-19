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

import org.junit.Test;


public class TestCompactProteinSequence extends AbstractTestProteinSequence{

	@Override
	protected ProteinSequence encode(AminoAcid[] aminoAcids) {
		return new CompactProteinSequence(aminoAcids);
	}

	@Test
	public void gappedSequence(){
		ProteinSequence seq = encode(AminoAcidUtil.parse("I-LKM-FDEX").toArray(new AminoAcid[0]));
		assertEquals("I-LKM-FDEX", AminoAcidUtil.asString(seq));
		assertEquals(2, seq.getNumberOfGaps());
		assertEquals(8, seq.getUngappedLength());
		assertEquals(1, seq.getUngappedOffsetFor(2));
	}

}
