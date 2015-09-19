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
package org.jcvi.jillion.assembly.tigr.tasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestAnnotationTasmParsing {
	 private static final ResourceHelper RESOURCES = new ResourceHelper(TestAnnotationTasmParsing.class);
	 
	 private final TasmContigDataStore datastore;
	 public TestAnnotationTasmParsing() throws FileNotFoundException, IOException{
		 NucleotideFastaDataStore empty = DataStoreUtil.adapt(NucleotideFastaDataStore.class, Collections.<String,NucleotideFastaRecord>emptyMap());
		 datastore = new TasmContigFileDataStoreBuilder(RESOURCES.getFile("files/annotation.tasm"),empty)
		 							.build();
			
	 }
	 @Test
	 public void numberOfContigs() throws DataStoreException{
		 assertEquals(3L, datastore.getNumberOfRecords());
	 }
	 @Test
	 public void correctContigIds() throws IOException, DataStoreException{
		 assertTrue(datastore.contains("1122071329926"));
		 assertTrue(datastore.contains("1122071329927"));
		 assertTrue(datastore.contains("1122071329928"));
		 
	 }
	 
	 @Test
	 public void contigEndingIn927() throws DataStoreException{
		 String gappedConsensusOf927 = 
				 "GAATGGATGTCAATCCGACTTTACTTTTCCTGAAAGTTCCAGCGCAGAATGCCAT" +
				 "AAGCACCACATTCCCTTATACTGGAGATCCTCCATACAGCCATGGAACGGGAACA" +
				 "GGATACACCATGGACACAGTCAACAGGACACATCAATATTCAGAAAAGGGGAAAT" +
				 "GGACAACAAACACAGAGACTGGGGCACCCCAACTCAACCCAATCGATGGACCACT" +
				 "GCCCGAAGACAATGACCCAAGTGGATATGCACAAACAGACTGTGTCCTTGAAGCA" +
				 "ATGGCTTTCCTTGAAGAGTCCCACCC-AGGAATCTTTGAAAACTCGTGTCTTGAA" +
				 "ACGATGGAAGTTGTCCAACAAACAAGAATGGACAAACTGACCCAGGGTCGTCAGA" +
				 "CCTATGATTGGACTTTAAACAGGAATCAGCCGGCTGCAACTGCATTAGCCAATAC" +
				 "TATAGAGGTCTTCAGATCGAATGGTCTGACAGCTAATGAATCAGGAAGGCTGATA" +
				 "GATTTCCTCAAGGATGTGATGGAATCAATGGATAAAGAGGGAATGGAAATAACAA" +
				 "CGCACTTCCAAAGAAAGAGAAGAGTAAGAGACAACATGACCAAGAAAATGGTCAC" +
				 "ACAAAGGACAATAGGAAAGAAGAAGCAAAGGCTGAACAAGAAAAGCTATCTAATA" +
				 "AGAGCATTGACACTAAACACAATGACCAA-AGATGCTGAAAGAGGCAAATTAAAG" +
				 "AGGAGAGCAATTGCTACACCCGGAATGCAGATCAGAGGATTTGTATACTTTGTTG" +
				 "AAACATTAGCGAGGAGCATCTGTGAGAAGCTTGAACAATCTGGACTCCCAGTTGG" +
				 "AGGCAATGAAAAGAAGGCTAAACTGGCAAACGTTGTAAGAAAAATGATGACTAAT" +
				 "TCACAAGACACAGAACTCTCCTTCACTATCACCGGAGACAACACCAAATGGAATG" +
				 "AGAATCAGAACCCTAGGATGTTTCTGGCAATGATAACATACATAACAAGGAACCA" +
				 "ACCTGAGTGGTTTAGGAATGTCTTGAGCATTGCACCTATAATGTTCTCGAATAAA" +
				 "ATGGCA-AGACTAGGGAAGGGATACATGTTCGAAAGCA-AGAGCATGAAGCTTAG" +
				 "AACACAGATACCAGCAGAAATGCTAGCAAGTATTGATCTAAAATATTTCAATGAG" +
				 "TCAACAAGAAAGAAAATAGAGAAGATAAGGCCTCTTCTAATAGATGGTACAGCTT" +
				 "CATTGAGCCCTGGAATGATGATGGGCATGTTCAACATGCTAAGTACAGTTTTGGG" +
				 "AGTCTCGATTCTCAACCTAGGGCAGAAGAGGTACACCAAAACAACATATTGGTGG" +
				 "GACGGACTCCAATCCTCCGATGACTTTGCTCTTATAGTGAATGCTCCGAATCATG" +
				 "AAGGAATACAAGCAGGAGTAGATAGATTCTATAGAACCTGCAAGCTGGTCGGAAT" +
				 "AAATATGAGCAAAAAGAAGTCCTACATAAACAAGACAGGGACATTTGAATTCACA" +
				 "AGCTTTTTCTATCGCTATGGATTTGTAGCCAATTTTAGCATGGAGCTTCCCAGTT" +
				 "TTGGAGTGTCTGGGATTAATGAATCTGCTGACATGAGCATTGGAGTAACAGTGAT" +
				 "AAAGAACAACATGATAAACAATGATCTTGGACCAGCAACAGCTCAAATGGCTCTT" +
				 "CAGCTATTCATCAAGGATTACAGATACACGTATCGGTGCCACAGAGGGGACACAC" +
				 "AAATTCAAACAAGGAGGTCATTCGAGCTGAAGAAGTTGTGGGAACAAACCCGCTC" +
				 "AAAGGCAGGACTGCTGGTTTCAGATGGAGGGCCAAACTTATACAATATCCGAAAT" +
				 "CTCCACATCCCGGAGGTCTGCCTGAAATGGGAGCTGATGGACGAAGATTATCAGG" +
				 "GAAGGCTTTGTAACCCCCTGAATCCATTTGTCAGCCACAAAGAGATAGAGTCTGT" +
				 "AAACAATGCTGTGGTGATGCCAGCTCATGGCCCAGCCAAGAGCATGGAATATGAT" +
				 "GCTGTTGCTACCACGCACTCCTGGATCCCTAAGAGGAACCGCTCCATTCTCAACA" +
				 "CAAGCCAAAGGGGAATCCTTGAAGATGAACAGATGTATCAGAAATGCTGCAATCT" +
				 "ATTCGAGAAATTCTTCCCTAGCAGCTCATACAGGAGACCGGTTGGAATTTCCAGC" +
				 "ATGGTGGAGGCCATGGTTTCTAGGGCCCGAATTGATGCGAGAATTGACTTCGAAT" +
				 "CTGGACGGATTAAGAAGGAGGAGTTTGCTGAGATCATGAAGATCTGTTCCACCAT" +
				 "TGAAGAGCTCAGACGGCAGAAATAGTGAATTTAGCTTGTCCTTCATG";
		 
		 TasmContig contig = datastore.get("1122071329927");
		assertEquals(gappedConsensusOf927, contig.getConsensusSequence().toString());
		assertEquals("annotation contig didn't set the num reads from file", 33, contig.getNumberOfReads());
		assertTrue(contig.isAnnotationContig());
	 }
}
