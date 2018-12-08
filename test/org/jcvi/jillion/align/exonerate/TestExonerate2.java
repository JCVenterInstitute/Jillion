package org.jcvi.jillion.align.exonerate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.align.exonerate.vulgar.VulgarProtein2Genome;
import org.jcvi.jillion.align.exonerate.vulgar.VulgarProtein2Genome2;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestExonerate2 {
	 private static final ResourceHelper RESOURCES = new ResourceHelper(TestExonerate2.class);

	@Test
	public void parseVulgarOutputTestFrame1() throws IOException{
			List<VulgarProtein2Genome2> alignments = Exonerate2.parseVulgarOutput(RESOURCES.getFile("files/Exonerate_chikv.txt"));
			VulgarProtein2Genome2 alignment = alignments.get(1);
			Frame actual = alignment.getAlignmentFragments().get(1).getFrame();
			assertEquals(Frame.THREE,actual);
	}
	
	@Test
	public void parseVulgarOutputTestFrame2() throws IOException{
		List<VulgarProtein2Genome2> alignments = Exonerate2.parseVulgarOutput(RESOURCES.getFile("files/Exonerate_chikv_shiftedOneNucleotide.txt"));
		VulgarProtein2Genome2 alignment = alignments.get(1);
		Frame actual = alignment.getAlignmentFragments().get(1).getFrame();
		assertEquals(Frame.TWO,actual);
	}
	
	@Test
	public void parseVulgarOutputTestAtFrameshiftElement() throws IOException{
		List<VulgarProtein2Genome2> alignments = Exonerate2.parseVulgarOutput(RESOURCES.getFile("files/Exonerate_withFrameshiftElement.txt"));
		VulgarProtein2Genome2 alignment = alignments.get(0);
		Frame actual = alignment.getAlignmentFragments().get(1).getFrame();
		assertEquals(Frame.ONE,actual);
	}
	
	@Test
	public void parseVulgarOutputTestAtGaps() throws IOException{
		List<VulgarProtein2Genome2> alignments = Exonerate2.parseVulgarOutput(RESOURCES.getFile("files/Exonerate_chikv.txt"));
		VulgarProtein2Genome2 alignment = alignments.get(0);
		long actual = alignment.getAlignmentFragments().get(1).getProteinSeqRange().getBegin();
		assertEquals(516,actual);
		
	}

	@Test
	public void parseVulgarOutputTestOppositeStrand() throws IOException {
		List<DirectedRange[]> expectedFragmentRanges = new ArrayList<>();
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(46,2190, Direction.REVERSE)
		});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1434,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1494,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1443,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1434,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1434,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1434,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1494,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1624,2190, Direction.REVERSE),
				DirectedRange.of(1434,1622, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1434,1619, Direction.REVERSE),
				});
		expectedFragmentRanges.add(new DirectedRange[] {
				DirectedRange.of(1621,2190, Direction.REVERSE),
				DirectedRange.of(1494,1619, Direction.REVERSE),
				});

		List<VulgarProtein2Genome2> alignments = Exonerate2.parseVulgarOutput(RESOURCES.getFile("files/Exonerate_flua_reversed.txt"));
		assertEquals(expectedFragmentRanges.size(), alignments.size());
		DirectedRange targetRange, queryRange;
		VulgarProtein2Genome2 alignment;
		List<VulgarProtein2Genome2.AlignmentFragment> fragments;
		VulgarProtein2Genome2.AlignmentFragment fragment;
		DirectedRange[] expectedRanges;
		for (int i=0; i < alignments.size(); i++) {
			alignment = alignments.get(i);
			assertTrue(alignment.getTargetStrand().isPresent() && alignment.getTargetStrand().get() == Direction.REVERSE);
			expectedRanges = expectedFragmentRanges.get(i);
			fragments = alignment.getAlignmentFragments();
			assertEquals(expectedRanges.length, fragments.size());

			for (int j=0; j < expectedRanges.length; j++) {
				fragment = fragments.get(j);
				targetRange = fragment.getNucleotideSeqRange();
				queryRange = fragment.getProteinSeqRange();
				assertEquals(String.format("alignment %s fragment %s expected %s, actual %s", i, j, expectedRanges[j],targetRange),
							 expectedRanges[j],targetRange);
				assertTrue( queryRange.getDirection() == Direction.FORWARD);

			}

		}

	}
}
