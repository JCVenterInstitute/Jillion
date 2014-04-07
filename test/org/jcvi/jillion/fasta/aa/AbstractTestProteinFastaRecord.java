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
package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.junit.Test;
import static org.junit.Assert.*;
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
