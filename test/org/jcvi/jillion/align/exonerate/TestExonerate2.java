package org.jcvi.jillion.align.exonerate;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.jcvi.jillion.align.exonerate.vulgar.VulgarProtein2Genome2;
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
}
