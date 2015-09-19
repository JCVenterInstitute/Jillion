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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
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
				NucleotideSequenceTestUtil.create("gcctaAGCTAA")
				, null, null, null, "ref", 8, Direction.FORWARD, 
				NucleotideSequenceTestUtil.create("AGCTAA"), 
				new ReadInfo(Range.of(5,10), 11));
		transformer.aligned("r004",
				NucleotideSequenceTestUtil.create("ATAGCTTCAGC")
				, null, null, null, "ref", 17, Direction.FORWARD, 
				NucleotideSequenceTestUtil.create("ATAGCT**************TCAGC"), 
				new ReadInfo(Range.ofLength(11), 11));
		//the other alignment for r003 should not map since it's not primary?
		transformer.aligned("r001/2",
				NucleotideSequenceTestUtil.create("CAGCGGCAT").toBuilder().reverseComplement().build()
				, null, null, null, "ref", 38, Direction.REVERSE, 
				NucleotideSequenceTestUtil.create("CAGCGGCAT"), 
				new ReadInfo(Range.ofLength(9), 9));
		
		transformer.endAssembly();
		return transformer;
	}
}
