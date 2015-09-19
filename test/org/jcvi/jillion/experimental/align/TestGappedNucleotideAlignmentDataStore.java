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
package org.jcvi.jillion.experimental.align;

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
