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
package org.jcvi.jillion.trim.lucy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.jcvi.jillion.trim.NucleotideTrimmer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLucyLikeVectorSpliceTrimmer {

	private static NucleotideSequence EBOFF01T00RACE5F;
	private static NucleotideSequence EBOFC01T00RACE3F;
	private static NucleotideSequence EBOFD01T00RACER5;
	
	
	private NucleotideTrimmer sut;
	@BeforeClass
	public static void getSequences() throws IOException, DataStoreException{
		ResourceHelper helper = new ResourceHelper(TestLucyLikeVectorSpliceTrimmer.class);
		try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(helper.getFile("files/race.fasta.untrimmed"))
														.build()){
			EBOFF01T00RACE5F = datastore.get("EBOFF01T00RACE5F").getSequence();
			EBOFC01T00RACE3F = datastore.get("EBOFC01T00RACE3F").getSequence();
			EBOFD01T00RACER5 = datastore.get("EBOFD01T00RACER5").getSequence();
		}
	}
	
	@Before
	public void setup(){
		sut = new LucyVectorSpliceTrimmerBuilder(
					NucleotideSequenceTestUtil.create("TATCGCTCGAGGGATCCGAATTCAGGAGGTAAAAACCATGAT"),
					NucleotideSequenceTestUtil.create("CTGATAATAATGACGTCAGAATTCTCGAGTCGGGGAAATGTGCGCGG"))
					.build();
	}
	
	
	@Test
	public void forwardRead1(){
		assertEquals(Range.of(105,406),sut.trim(EBOFF01T00RACE5F));
	}
	@Test
	public void forwardRead2(){
		assertEquals(Range.of(103,775),sut.trim(EBOFC01T00RACE3F));
	}
	
	@Test
	public void reverseRead(){
		assertEquals(Range.of(87,388),sut.trim(EBOFD01T00RACER5));
	}
	
	@Test
	public void reverseSequenceWithoutCheckingBiDirectionallyWillNotFindVector(){
		NucleotideTrimmer singleDirTrimmer = new LucyVectorSpliceTrimmerBuilder(
							NucleotideSequenceTestUtil.create("TATCGCTCGAGGGATCCGAATTCAGGAGGTAAAAACCATGAT"),
							NucleotideSequenceTestUtil.create("CTGATAATAATGACGTCAGAATTCTCGAGTCGGGGAAATGTGCGCGG"))
				
					.onlyCheckForwardDirection()
					.build();
		
		assertEquals(Range.ofLength(EBOFD01T00RACER5.getLength()), singleDirTrimmer.trim(EBOFD01T00RACER5));
	}
}
