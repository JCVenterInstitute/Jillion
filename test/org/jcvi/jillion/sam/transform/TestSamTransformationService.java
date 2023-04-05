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
import org.jcvi.jillion.assembly.AssemblyTransformer.AssemblyTransformerCallback;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

public class TestSamTransformationService {

	
	protected AssemblyTransformationService getSut() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestSamTransformationService.class);
		File refFasta = helper.getFile("reference.fasta");
		
		File samFile = helper.getFile("example.sam");
		
		return new SamTransformationService(samFile, refFasta);
	}
	@Test
	public void assertGappedAlignmentsCorrect() throws IOException{
		
		AssemblyTransformationService sut = getSut();
		
		AssemblyTransformer transformer = createExpectedTransformer();
		replay(transformer);
		
		sut.transform(transformer);
		verify(transformer);
		
	}
	
	
	protected AssemblyTransformer createExpectedTransformer(){
		AssemblyTransformer transformer = createMock(AssemblyTransformer.class);
		
		transformer.referenceOrConsensus(eq("ref"), eq(NucleotideSequenceTestUtil.create("AGCATGTTAGATAA**GATAGCTGTGCTAGTAGGCAGTCAGCGCCAT")), isA(AssemblyTransformerCallback.class));
		
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
				eq(NucleotideSequenceTestUtil.create("gcctaAGCTAA"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(8L), eq(Direction.FORWARD), 
				eq(NucleotideSequenceTestUtil.create("AGCTAA")), 
				eq(new ReadInfo(Range.of(5,10), 11)), notNull());
		transformer.aligned(eq("r004"),
				eq(NucleotideSequenceTestUtil.create("ATAGCTTCAGC"))
				, isNull(), isNull(), isNull(), eq("ref"), eq(17L), eq(Direction.FORWARD), 
				eq(NucleotideSequenceTestUtil.create("ATAGCT**************TCAGC")), 
				eq(new ReadInfo(Range.ofLength(11), 11)), notNull());
		//the other alignment for r003 should not map since it's not primary?
		transformer.aligned(eq("r001/2"),
				eq(NucleotideSequenceTestUtil.create("CAGCGGCAT").toBuilder().reverseComplement().build())
				, isNull(), isNull(), isNull(), eq("ref"), eq(38L), eq(Direction.REVERSE), 
				eq(NucleotideSequenceTestUtil.create("CAGCGGCAT")), 
				eq(new ReadInfo(Range.ofLength(9), 9)), notNull());
		
		transformer.endAssembly();
		return transformer;
	}
}
