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
package org.jcvi.jillion.sam.transform;

import static org.easymock.EasyMock.*;

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
		
		transformer.aligned(eq("r001/1"),
				eq(NucleotideSequenceTestUtil.create("TTAGATAAAGGATACTG"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(6L), eq(Direction.FORWARD), eq(NucleotideSequenceTestUtil.create("TTAGATAAAGGATA*CTG")), 
				eq(new ReadInfo(Range.ofLength(17), 17)), notNull());
		
		transformer.aligned(eq("r002"),
				eq(NucleotideSequenceTestUtil.create("aaaAGATAAGGATA"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(8L), eq(Direction.FORWARD), 
				eq(NucleotideSequenceTestUtil.create("AGATAA*GGATA")), 
				eq(new ReadInfo(Range.of(3,13), 14)), notNull());
		transformer.aligned(eq("r003"),
				eq(NucleotideSequenceTestUtil.create("AGCTAA"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(8L), eq(Direction.FORWARD), 
				eq(NucleotideSequenceTestUtil.create("AGCTAA")), 
				eq(new ReadInfo(Range.of(5,10), 11)), notNull());
		transformer.aligned(eq("r004"),
				eq(NucleotideSequenceTestUtil.create("ATAGCTTCAGC"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(17L), eq(Direction.FORWARD), 
				eq(NucleotideSequenceTestUtil.create("ATAGCT**************TCAGC")), 
				eq(new ReadInfo(Range.ofLength(11), 11)), notNull());
		
		transformer.aligned(eq("r003"),
				eq(NucleotideSequenceTestUtil.create("TAGGC").toBuilder().reverseComplement().build())
				, isNull(), isNull(), isNull(), eq("ref"), eq(30L), eq(Direction.REVERSE), 
				eq(NucleotideSequenceTestUtil.create("TAGGC")), 
				eq(new ReadInfo(Range.ofLength(5), 11)), notNull());
		
		
		transformer.aligned(eq("r001/2"),
				eq(NucleotideSequenceTestUtil.create("CAGCGCCAT").toBuilder().reverseComplement().build())
				, isNull(), isNull(), isNull(), eq("ref"), eq(38L), eq(Direction.REVERSE), 
				eq(NucleotideSequenceTestUtil.create("CAGCGCCAT")), 
				eq(new ReadInfo(Range.ofLength(9), 9)), notNull());
		
		transformer.endAssembly();
		return transformer;
	}
}
