package org.jcvi.jillion.sam.transform;

import static org.easymock.EasyMock.createMock;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

public class TestPaddedSamTransformationService extends TestSamTransformationService{

	@Override
	protected AssemblyTransformationService getSut() throws IOException {
		ResourceHelper helper = new ResourceHelper(TestPaddedSamTransformationService.class);
		File paddedSam =helper.getFile("paddedExample.sam");
		return new PaddedSamTransformationService(paddedSam);
	}

	/**
	 * The padded sam file is ALMOST identical
	 * except for some reason the example
	 * has hard padding for r003 instead of soft padding.
	 * And instead of using skip, they
	 * made r003 map twice! 
	 * And they copy and pasted r001/2 sequence
	 * wrong it's ..G<strong>C</strong>CAT instead of ..G<strong>G</strong>CAT.
	 * {@inheritDoc}
	 */
	@Override
	protected AssemblyTransformer createExpectedTransformer(){
		AssemblyTransformer transformer = createMock(AssemblyTransformer.class);
		
		transformer.referenceOrConsensus("ref", NucleotideSequenceTestUtil.create("AGCATGTTAGATAA**GATAGCTGTGCTAGTAGGCAGTCAGCGCCAT"));
		
		transformer.aligned("r001/1",
				NucleotideSequenceTestUtil.create("TTAGATAAAGGATACTG")
				, null, null, null, "ref", 6, Direction.FORWARD, NucleotideSequenceTestUtil.create("TTAGATAAAGGATA*CTG"), 
				new ReadInfo(Range.ofLength(17), 17));
		
		transformer.aligned("r002",
				NucleotideSequenceTestUtil.create("aaaAGATAAGGATA")
				, null, null, null, "ref", 8, Direction.FORWARD, 
				NucleotideSequenceTestUtil.create("AGATAA*GGATA"), 
				new ReadInfo(Range.of(3,13), 14));
		transformer.aligned("r003",
				NucleotideSequenceTestUtil.create("AGCTAA")
				, null, null, null, "ref", 8, Direction.FORWARD, 
				NucleotideSequenceTestUtil.create("AGCTAA"), 
				new ReadInfo(Range.of(5,10), 11));
		transformer.aligned("r004",
				NucleotideSequenceTestUtil.create("ATAGCTTCAGC")
				, null, null, null, "ref", 17, Direction.FORWARD, 
				NucleotideSequenceTestUtil.create("ATAGCT**************TCAGC"), 
				new ReadInfo(Range.ofLength(11), 11));
		
		transformer.aligned("r003",
				NucleotideSequenceTestUtil.create("TAGGC").toBuilder().reverseComplement().build()
				, null, null, null, "ref", 30, Direction.REVERSE, 
				NucleotideSequenceTestUtil.create("TAGGC"), 
				new ReadInfo(Range.ofLength(5), 11));
		
		
		transformer.aligned("r001/2",
				NucleotideSequenceTestUtil.create("CAGCGCCAT").toBuilder().reverseComplement().build()
				, null, null, null, "ref", 38, Direction.REVERSE, 
				NucleotideSequenceTestUtil.create("CAGCGCCAT"), 
				new ReadInfo(Range.ofLength(9), 9));
		
		transformer.endAssembly();
		return transformer;
	}
}
