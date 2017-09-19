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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestIupacTranslationTables {

	private final IupacTranslationTables table;
	private final String dnaString;
	private final ProteinSequence expectedAa;
	private final int expectedTableNumber;
	
	@Parameters
	public static Collection<?> data(){
		 List<Object[]> data = new ArrayList<Object[]>();
		 data.add(new Object[]{1, IupacTranslationTables.STANDARD, 
				 			"ATGATTTCCGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
							"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
							"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTA" +
							"TATCACGCGCTGCAACACCCGCTTTACCGTGACGTCGAAACCATTGTCCGCGTGAATGC" +
							"GCTGGATTCCGAATGGGGCGTCAACGATCTGGAAGCGGTCGTGCGCGGCGGCGCGGACG" +
							"TGGTGCGTTTACCGAAAACCGACACCGCGCAGGACGTCATTGATATCGAAAACGAAATT" +
							"CTGCGCATTGAAAACGCCTGCGGTCGCGAACCGGGCAGTACCGGCCTGTTAGCCGCCGT" +
							"GGAATCGCCGCTGGGTATTACCCGCGCGGTAGAAATCGCCCACGCCTCTGAACGGTTGA" +
							"TCGGGATTGCGCTGGGCGCGGAAGACTATGTTCGCAACCTGCGCACCGAACGCTCTCCG" +
							"GAAGGCACCGAACTGCTGTTCGCCCGCTGCGCCATTTTGCAGGCCGCGCGTTCCGCCGG" +
							"CATTCAGGCCTTCGATACCGTCTATTCCGACGCCAACAACGAAGCCGGTTTCCTGCAAG" +
							"AAGCCGCCCACATTAAACAGTTGGGCTTTGACGGTAAGTCACTGATCAACCCCCGCCAG" +
							"ATCGAACTGCTGCACAACCTTTATGCGCCAACGCGTAAAGAGGTCGCCCATGCGCGTCT" +
							"GGTCGTTGAAGCCGCAGAAGCCGCCGCCCGCGAAGGTCTCGGCGTCGTTTCCCTTAACG" +
							"GCAAGATGGTCGACAGCCCGGTGATTGAACGCGCCCGTCTGGTGCTCTCCCGTGCAGAA" +
							"CTTTCCGGTATTCGCGAAGAATAA",
							
							"MISASLQQRKTRTRRSMLFVPGANAAMVSNSFIYPADALMFDLEDSVALREKDAARRLV" +
									"YHALQHPLYRDVETIVRVNALDSEWGVNDLEAVVRGGADVVRLPKTDTAQDVIDIENEI" +
									"LRIENACGREPGSTGLLAAVESPLGITRAVEIAHASERLIGIALGAEDYVRNLRTERSP" +
									"EGTELLFARCAILQAARSAGIQAFDTVYSDANNEAGFLQEAAHIKQLGFDGKSLINPRQ" +
									"IELLHNLYAPTRKEVAHARLVVEAAEAAAREGLGVVSLNGKMVDSPVIERARLVLSRAE" +
									"LSGIREE*"
		 			}
				 );
		 //taken from  genbank accession nuccore AY089989.1  GI:20148763
		 data.add(new Object[]{3, IupacTranslationTables.YEAST_MITOCHONDRIAL, 
				 "atgaaaaaagctgtaatcaatggtgaacaaatcagatcaatctcagatttacatcaaaca" +
			     "ttaaaaaaagaattagctttacctgaatattatggtgaaaatttagatgctttatgagat" +
			     "tgtttaacaggttgagtagaatatcctttagtattagaatgaagacaattcgaacaatca" +
			     "aaacaattaacagaaaatggtgctgaatcagtattacaagtattcagagaagctaaagct"+
			     "gaaggttgtgatatcacaatcatcttatcataa",
			     
			     "MKKAVINGEQIRSISDLHQTLKKELALPEYYGENLDALWDCLTG"+
                 "WVEYPLVLEWRQFEQSKQLTENGAESVLQVFREAKAEGCDITIILS*"
		 	}
				 );
		//taken from  genbank accession nuccore AY544190.1  GI:49471680
		 data.add(new Object[]{4, IupacTranslationTables.MOLD_PROTOZOAN_COELENTERATE_MITOCHONDRIAL_AND_MYCOPLASMA_SPIROPLAMSA, 
				new NucleotideSequenceBuilder( "gactccatttagagcagaagatctttctaccgaaagagaacaaagagttttggataaatt"+
       "ttctgaacttaaaaatcaagttatttttacgacaacgttgaaagaagaagaaaatttaaa"+
      "gtatgattcaattgatggaataaacggcatttaattatagtggacataaaaactataagat"+
      "actacaacaaacatatgtgtgcgatttcatgaaaaagttagattcaatgtcaattcaaat"+
      "aaaataaaatacataaaaagccgatggaagaagatttgtcgtctttttgttttttgccaa"+
      "agcaaaaaaagaaattttcaatgaaacaaaaaatcattaacatcaaaacaagctatttgt"+
      "cataatttattttctaaagtttgaacacctttgatttcaattgatgaaattataagagaa"+
      "ctagaagacgaataataaagttgcaaataattaaaaccgtacat")
		 .reverseComplement()
		 .toString(),
			      
			     
			     "MYGFNYLQLYYSSSSSLIISSIEIKGVQTLENKLWQIACFDVND"+
                     "FLFHWKFLFLLWQKTKRRQIFFHRLFMYFILFELTLNLTFSWNRTHMFVVVSYSFYVH"+
                     "YN*MPFIPSIESYFKFSSSFNVVVKITWFLSSENLSKTLCSLSVERSSALNGV"
		 	}
				 );
		 
		 //taken from  genbank accession nuccore AB195239.1  GI:57158240
		 data.add(new Object[]{6, IupacTranslationTables.CILIATE_DASYCLADACEAN_AND_HEXAMITA, 
				 "atgagaaagggagaagaattgttcacaggagtcgtcccaattcttgttgaattagatggt" +
			     "gatgttaatggacacaaattttcagtctcaggagaaggagaaggagatgctacatatgga" +
			      "aaattgacacttaaattgatttgcacaacaggaaaattgccagtcccatggccaacattg" +
			      "gtcacaacattgggatatggagtctaatgctttgctagatacccagatcatatgaaacag" +
			      "catgactttttcaagtcagctatgccagaaggatatgtctaagaaagaacaatttttttc" +
			      "aaagatgatggaaactataagacaagagctgaagtcaagtttgaaggtgatacccttgtt" +
			      "aatagaatcgagttaaaaggtattgattttaaagaagatggaaacattcttggacacaaa" +
			      "ttggaatataactataactcacacaatgtctatatcacagctgataaataaaagaatgga" +
			      "atcaaagctaacttcaaaattagacacaacattgaagatggaggagtctaattggctgat" +
			      "cattattaataaaatacaccaattggagatggaccagtccttttaccagataaccattat" +
			      "ttgtcatattaatcagctttgtcaaaagatccaaacgaaaagagagatcacatggtcctt" +
			      "cttgagtttgtaacagctgctggaattacattgggaatggatgaattgtataagagagct" +
			      "ccatga",
			     
			     "MRKGEELFTGVVPILVELDGDVNGHKFSVSGEGEGDATYGKLTL"+
                     "KLICTTGKLPVPWPTLVTTLGYGVQCFARYPDHMKQHDFFKSAMPEGYVQERTIFFKD"+
                    "DGNYKTRAEVKFEGDTLVNRIELKGIDFKEDGNILGHKLEYNYNSHNVYITADKQKNG"+
                     "IKANFKIRHNIEDGGVQLADHYQQNTPIGDGPVLLPDNHYLSYQSALSKDPNEKRDHM"+
                     "VLLEFVTAAGITLGMDELYKRAP*"
		 	}
				 );
		 
		//taken from  genbank accession nuccore AF162859.1  GI:15054485
		 data.add(new Object[]{11, IupacTranslationTables.BACTERIAL_ARCHAEL_AND_PLANT_PLASTID, 
				 "gtgatgcggtggattttagtgttctttctgggatt" +
			      "cctattcgccatcgatgcagcaggccaggacgtagcgccgtgcacgcctgagattaacgg" +
			      "ctgcgatcaggggcaggcgtaccagcacgcgtcacgtgatgccagcgcggagggctactg" +
			      "cacttccgccggcacatgggcgatggtgagccatgaggtgtatgcggatggcgaaaatcg" +
			      "ctatggcgttgaggtgcgttgccggaacaatgaaaattttgagacaggttttcgcaacgc" +
			      "ccgccgctggtacttcggtcaatcttgttctgctcgccccccattgatcggtgcaagatc" +
			      "ttccgatggtagtggttttagttgtgacgatggatgcttctacaatttcaccattggtgg" +
			      "tgaaaagggcagtggcatgtatccaagcggtgccacgtgttctgttggtgacgcgccacc" +
			      "ttccacgcccggtgatggtgatggccacggtgatgaccatggtgatggccacggtgatga" +
			      "ccatggtgatggccacggtgatgaccatggtgatggccacggtgatgaccacggtgatgg" +
			      "ccacggtgatgaccacggtgatgaccacggtgatgaccacggtgatggccatggtgatgg" +
			      "ccatggtgatggccatggtgatggccatggtgatggccatggtgatggccatggtgatgg" +
			      "ccatggtgatgaccaaggtggcagtgaaggcggagagggtgcccccatgtctgagcttta" +
			      "caagaaaagcggcaaaactgttgagtctgtgctgagcaaattcaatacgcaaggtcgtgg" +
			      "cacacccatggtggccggcatcactaatttcatgacggttccgtctggcggttcgtgtcc" +
			     "ggtgttttcgctggcggggtctaagttttgggacgccatgacgatcaactttcattgtgg" +
			     "cggcgatttccttgcgtttcttcgtgcagctggttgggtgatcttcgccattgccgcata" +
			     "cgccgcgttgcgcatcgctgtgacttga",
			     
			     "MIRWILVFFLGFLFAIDAAGQDVAPCTPEINGCDQGQAYQHASR" +
                     "DASAEGYCTSAGTWAIVSHEVYADGENRYGVEVRCRNNENFETGFRNARRWYFGQSCS" +
                     "ARPPLIGARSSDGSGFSCDDGCFYNFTIGGEKGSGIYPSGATCSVGDAPPSTPGDGDG" +
                     "HGDDHGDGHGDDHGDGHGDDHGDGHGDDHGDGHGDDHGDDHGDDHGDGHGDGHGDGHG" +
                     "DGHGDGHGDGHGDGHGDDQGGSEGGEGAPISELYKKSGKTVESVLSKFNTQGRGTPIV" +
                     "AGITNFITVPSGGSCPVFSLAGSKFWDAITINFHCGGDFLAFLRAAGWVIFAIAAYAA" +
                     "LRIAVT*"
		 	}
				 );
		 
		 
		 return data;
	}

	public TestIupacTranslationTables(int expectedTableNumber, IupacTranslationTables table,
			String dnaString, String aaString) {
		this.expectedTableNumber = expectedTableNumber;
		this.table = table;
		this.dnaString = dnaString;
		expectedAa = new ProteinSequenceBuilder(aaString).build();
		
	}
	
	@Test
	public void tableNumber(){
		int tableNumber = table.getTableNumber();
		assertEquals(table.name(), expectedTableNumber, tableNumber);
		assertEquals(table, IupacTranslationTables.getTableByTableNumber(tableNumber));
	}
	
	@Test
	public void translateFrame0(){
		NucleotideSequence seq = new NucleotideSequenceBuilder(dnaString).build();
		
		assertEquals(expectedAa.toString(), table.translate(seq).toString());
	}
	
	@Test
	public void translateFrame1(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("n"+dnaString).build();
		
		assertEquals(expectedAa, table.translate(seq, Frame.TWO));
	}
	
	@Test
	public void translateFrame2(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("nn"+dnaString).build();
		
		assertEquals(expectedAa, table.translate(seq, Frame.THREE));
	}
	@Test
	public void TestFindStops(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
							"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
							"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();
		
		Map<Frame,List<Long>> expected = new HashMap<Frame,List<Long>>();
		expected.put(Frame.THREE, Arrays.asList(5l));
		expected.put(Frame.TWO, Arrays.asList(31l));
		expected.put(Frame.ONE,Arrays.asList(87l,120l));
		Map<Frame,List<Long>> actual = table1.findStops(seq);
		assertEquals(expected,actual);	    
	}
	
}
