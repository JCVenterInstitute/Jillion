package org.jcvi.common.core.symbol.residue.nt;

import java.util.Arrays;

import org.jcvi.common.core.symbol.residue.nt.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Regression test to fix and a bug that was
 * discovered after memory optimizations to reference 
 * encoded nucleotide sequences with SNPS of gaps and Ns.
 * Reference encoded SNP information where the SNPs are
 *  a mix of gaps and Ns revealed a bug in the bit masking
 *   used to figure out the SNP value. 
 *   As a result the sequence got encoded incorrectly:
<pre>
before : TT--AA-T
after  : TT-NAA-T
</pre>
Since we lost a gap, the gap to ungap computations and ungapped quality computations all fail with off by 1 errors.

 * @author dkatzel
 *
 */
public class VHTNGS_365 {

	@Test
	public void lastSnpNIsEncodedCorrectly(){
		NucleotideSequence consensus = new NucleotideSequenceBuilder(
				"GGAT-AAC-TAAC-AAAGT-AAATT-CTGTGA-TT-G-AAAAG-A-TGAAT-ACTC-AG-TTTG-AAG-CT-G-TT-GGG-AAAG-A-GTT-CAAC-AA-T-C-T-AGAG-AGAAG--ACT-GG-AAAACTT-AAA-T-AAAAAG-AT-GGAA-GAT-GGGTTTCTAGA-TGTATGG-AC")
						.build();
		//read should have snps 
		//{84=N, 133=-, 146=N}
		
		ReferenceEncodedNucleotideSequence seq = new DefaultReferenceEncodedNucleotideSequence(consensus, 
				"GGAT-AAC-TAAC-AAAGT-AAATT-CTGTGA-TT-G-AAAAG-A-TGAAT-ACTC-AG-TTTG-AAG-CT-G-TT-GGG-AAAN-A-GTT-CAAC-AA-T-C-T-AGAG-AGAAG--ACT-GG-AAAACTT--AA-T-AAAAAG-NT-GGAA-GAT-GGGTTTCTAGA-TGTATGG-AC"
				,
				0);
		
		assertEquals(Arrays.asList(84,133,146), seq.getSnpOffsets());
		assertEquals(Nucleotide.Unknown, seq.get(84));
		assertEquals(Nucleotide.Unknown, seq.get(146));
		assertEquals(Nucleotide.Gap, seq.get(133));
		
	}
	
	@Test
	public void lastSnpNotNIsEncodedCorrectly(){
		NucleotideSequence consensus = new NucleotideSequenceBuilder(
				"GGAT-AAC-TAAC-AAAGT-AAATT-CTGTGA-TT-G-AAAAG-A-TGAAT-ACTC-AG-TTTG-AAG-CT-G-TT-GGG-AAAG-A-GTT-CAAC-AA-T-C-T-AGAG-AGAAG--ACT-GG-AAAACTT-AAA-T-AAAAAG-AT-GGAA-GAT-GGGTTTCTAGA-TGTATGG-AC")
						.build();
		//read should have snps 
		//{84=N, 133=-, 146=R}
		
		ReferenceEncodedNucleotideSequence seq = new DefaultReferenceEncodedNucleotideSequence(consensus, 
				"GGAT-AAC-TAAC-AAAGT-AAATT-CTGTGA-TT-G-AAAAG-A-TGAAT-ACTC-AG-TTTG-AAG-CT-G-TT-GGG-AAAN-A-GTT-CAAC-AA-T-C-T-AGAG-AGAAG--ACT-GG-AAAACTT--AA-T-AAAAAG-RT-GGAA-GAT-GGGTTTCTAGA-TGTATGG-AC"
				,
				0);
		
		assertEquals(Arrays.asList(84,133,146), seq.getSnpOffsets());
		assertEquals(Nucleotide.Unknown, seq.get(84));
		assertEquals(Nucleotides.parse("R").get(0), seq.get(146));
		assertEquals(Nucleotide.Gap, seq.get(133));
		
	}
}
