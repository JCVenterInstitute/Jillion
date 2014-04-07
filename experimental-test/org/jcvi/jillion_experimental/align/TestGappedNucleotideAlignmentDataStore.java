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
package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestGappedNucleotideAlignmentDataStore {
	
	private final NucleotideSequenceDataStore sut;
	
	public TestGappedNucleotideAlignmentDataStore() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestGappedNucleotideAlignmentDataStore.class);
	    sut = GappedNucleotideAlignmentDataStore.createFromAlnFile(resources.getFile("files/example.aln"));
	}
	@Test
	public void getNumberOfRecords() throws DataStoreException{
		assertEquals(7, sut.getNumberOfRecords());
	}
	
	@Test
	public void getFirstRecord() throws DataStoreException{
		String id = "gi|304633245|gb|HQ003817.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTATTGATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
	
	@Test
	public void getLastRecord() throws DataStoreException{
		String id = "gi|58177684|gb|AY601635.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTATTGATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
	
	@Test
	public void getMiddleRecordWithSNPs() throws DataStoreException{
		String id = "gi|9626158|ref|NC_001405.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTAT-GATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
}
