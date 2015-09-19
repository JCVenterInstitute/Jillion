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
package org.jcvi.jillion.fasta.aa;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public abstract class AbstractTestProteinFastaRecord {

	private final ProteinSequence seq = new ProteinSequenceBuilder("ILMFTWVC").build();
	private final String id = "id";
	protected final ProteinFastaRecord sut;
	private final String comments = "a comment";
	public AbstractTestProteinFastaRecord(){
		sut = createRecord(id, seq,comments);
	}
	protected abstract ProteinFastaRecord createRecord(String id, ProteinSequence seq, String optionalComment);
	
	@Test
	public void getters(){
		assertEquals(id, sut.getId());
		assertEquals(seq, sut.getSequence());
	}
	
	@Test
	public void length(){
		assertEquals(sut.getSequence().getLength(), sut.getLength());
	}
	
	@Test
	public void sameReferenceShouldBeEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void sameValuesShouldBeEqual(){
		ProteinFastaRecord same = createRecord(id, seq,comments);
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	/**
	 * Comments don't take part in equals/hashcode computation
	 */
	@Test
	public void differentCommentsValuesShouldBeEqual(){
		ProteinFastaRecord same = createRecord(id, seq,"different"+comments);
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	@Test
	public void differentIdShouldNotBeEqual(){
		ProteinFastaRecord different = createRecord("different"+id, seq,comments);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	@Test
	public void differentSeqShouldNotBeEqual(){
		ProteinFastaRecord different = createRecord(id, new ProteinSequenceBuilder(seq)
																	.append(AminoAcid.Histidine)
																	.build()
																	,comments);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
}
