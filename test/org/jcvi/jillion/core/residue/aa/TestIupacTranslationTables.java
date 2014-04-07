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
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.residue.aa.TranslationTable.Frame;
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
		
		assertEquals(expectedAa, table.translate(seq, Frame.ONE));
	}
	
	@Test
	public void translateFrame2(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("nn"+dnaString).build();
		
		assertEquals(expectedAa, table.translate(seq, Frame.TWO));
	}
}
