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

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;
public class TestAlnFileParserLowercaseBases {

	@Test
	public void lowercase() throws IOException, DataStoreException{
		File in =new ResourceHelper(TestAlnFileParserLowercaseBases.class).getFile("files/mafft.aln");
		
		NucleotideSequenceDataStore datastore = GappedNucleotideAlignmentDataStore.createFromAlnFile(in);
		
		assertEquals(5, datastore.getNumberOfRecords());
		
		assertEquals(NucleotideSequenceTestUtil.create("--------------------------------------aatatcaagaaatcaag-----"),
										datastore.get("read1"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------aaatatcaagaaatcaag-----"),
				datastore.get("read2"));
		assertEquals(NucleotideSequenceTestUtil.create("--------------------------------------aatatcaagaaatcaag-----"),
				datastore.get("read3"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------aaatatcaagaaatcaag-----"),
				datastore.get("read4"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------------tggttgaccaggtctaa"),
				datastore.get("read5"));
		
		
	}
}
