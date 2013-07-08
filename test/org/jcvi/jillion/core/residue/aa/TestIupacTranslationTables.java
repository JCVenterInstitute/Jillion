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
	private final AminoAcidSequence expectedAa;
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
		 return data;
	}

	public TestIupacTranslationTables(int expectedTableNumber, IupacTranslationTables table,
			String dnaString, String aaString) {
		this.expectedTableNumber = expectedTableNumber;
		this.table = table;
		this.dnaString = dnaString;
		expectedAa = new AminoAcidSequenceBuilder(aaString).build();
		
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
		
		assertEquals(expectedAa, table.translate(seq));
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
