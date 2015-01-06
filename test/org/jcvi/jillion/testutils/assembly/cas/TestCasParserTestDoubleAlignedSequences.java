package org.jcvi.jillion.testutils.assembly.cas;

import static org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.clc.cas.AbstractAlignedReadCasVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.testutils.assembly.cas.CasParserTestDouble;
import org.jcvi.jillion.testutils.assembly.cas.CasTestUtil;
import org.jcvi.jillion.trace.Trace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCasParserTestDoubleAlignedSequences {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File workingDir;
	
	@Before
	public void createWorkingDir() throws IOException{
		workingDir = tmpDir.newFolder();
	}
	
	@Test(expected = IllegalStateException.class)
	public void noAlignmentsShouldThrowIllegalStateException() throws IOException{
		new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.build();

		
	}
	
	@Test
	public void forwardReadFullMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGG");
		
	}
	@Test
	public void forwardReadWithMisMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, "AATGGGG")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AATGGGG");
		
	}
	@Test
	public void reverseReadWithMisMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.reverseMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, "AATGGGG")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AATGGGG");
		
	}
	
	
	
	
	@Test
	public void twoforwardReadsFullMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									.forwardMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGG", "AAAGGGG");
		
	}
	@Test
	public void forwardAndReverseReadsFullMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									.reverseMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGG", "AAAGGGG");
		
	}

	@Test
	public void reverseReadFullMatch() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.reverseMatch("ref", 0)
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGG");
		
	}
	
	@Test
	public void forwardReadWithLeadingInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(INSERT, "CCC")
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "CCCAAAGGGG");
		
	}
	@Test
	public void forwardReadWithTrailingInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.addAlignmentRegion(INSERT, "CCC")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGGCCC");
		
	}
	@Test
	public void forwardReadWithLeadingAndTrailingInsertions() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.forwardMatch("ref", 0)
											.addAlignmentRegion(INSERT, "TTTTT")
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.addAlignmentRegion(INSERT, "CCC")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "TTTTTAAAGGGGCCC");
		
	}
	
	@Test
	public void reverseReadWithLeadingInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.reverseMatch("ref", 0)
											.addAlignmentRegion(INSERT, "CCC")
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "CCCAAAGGGG");
		
	}
	@Test
	public void reverseReadWithTrailingInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.reverseMatch("ref", 0)
											
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.addAlignmentRegion(INSERT, "CCC")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "AAAGGGGCCC");
		
	}
	@Test
	public void reverseReadWithLeadingAndTrailingInsertions() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
									.addReference("ref", "AAAGGGG")
									.reverseMatch("ref", 0)
											.addAlignmentRegion(INSERT, "TTTTT")
											.addAlignmentRegion(MATCH_MISMATCH, 7)
											.addAlignmentRegion(INSERT, "CCC")
											.build()
									
									.build();

		assertFullRangeGappedAlignmentsCorrect(parser, "TTTTTAAAGGGGCCC");
		
	}
	
	/*
	 * .forwardMatch("ref", 0)
													.addAlignmentRegion(INSERT, "AATT")
													.addAlignmentRegion(MATCH_MISMATCH, 2)
													.addAlignmentRegion(INSERT, 1)
													.addAlignmentRegion(MATCH_MISMATCH, 14)
													.build()
											.reverseMatch("ref", 0)
												.addAlignmentRegion(INSERT, "AATT")
												.addAlignmentRegion(MATCH_MISMATCH, 2)
												.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 14)
												.build();
	 */
	
	@Test
	public void forwardInternalInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AAANGGGG", "AAA-GGGG");
	}
	@Test
	public void forwardLeadingAndInternalInsertions() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.forwardMatch("ref", 0)
												.addAlignmentRegion(INSERT, "AATT")
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AATTAAANGGGG", "AAA-GGGG");
	}
	@Test
	public void reverseInternalInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AAANGGGG", "AAA-GGGG");
	}
	@Test
	public void reverseLeadingAndInternalInsertion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.reverseMatch("ref", 0)
												.addAlignmentRegion(INSERT, "AATT")
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AATTAAANGGGG", "AAA-GGGG");
	}
	///////////////////////////
	@Test
	public void forwardInternalDeletion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(DELETION, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.build()
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AAA-GGG", "AAAGGGG");
	}
	@Test
	public void forwardLeadingAndInternalDeletions() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.forwardMatch("ref", 0)
												.addAlignmentRegion(INSERT, "AATT")
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(DELETION, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.build()
										.forwardMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AATTAAA-GGG", "AAAGGGG");
	}
	@Test
	public void reverseInternalDeletion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(DELETION, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.build()
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AAA-GGG", "AAAGGGG");
	}
	@Test
	public void reverseLeadingAndInternalDeletion() throws IOException{
		CasParser parser = new CasParserTestDouble.Builder(workingDir)
										.addReference("ref", "AAAGGGG")
										.reverseMatch("ref", 0)
												.addAlignmentRegion(INSERT, "AATT")
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.addAlignmentRegion(DELETION, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												.build()
										.reverseMatch("ref", 0)
												
												.addAlignmentRegion(MATCH_MISMATCH, 3)
												//.addAlignmentRegion(INSERT, 1)
												.addAlignmentRegion(MATCH_MISMATCH, 4)
												.build()
										.build();
		
		assertFullRangeGappedAlignmentsCorrect(parser, "AATTAAA-GGG", "AAAGGGG");
	}

	private static void assertFullRangeGappedAlignmentsCorrect(CasParser parser, String... seqs) throws IOException {
		CasGappedReferenceDataStore gappedReferenceDataStore = CasTestUtil.createGappedReferenceDataStore(parser);
		
		Iterator<NucleotideSequence> iter = createExpectedFullRangeGappedIterator(seqs);
		
		CasFileVisitor visitor = new AbstractAlignedReadCasVisitor(parser.getWorkingDir(), gappedReferenceDataStore) {
			
			@Override
			protected void notAligned(Trace currentTrace) {
				assertEquals(iter.next(), currentTrace.getNucleotideSequence());
				
			}
			
			@Override
			protected void aligned(Trace traceOfRead, String referenceId,
					CasPlacedRead read) {
				assertEquals(iter.next(), AssemblyUtil.buildGappedComplementedFullRangeBases(read, traceOfRead.getNucleotideSequence(), true));
				
			}
		};
		
		parser.parse(visitor);
		
		assertFalse(iter.hasNext());
	}
	
	
	
	
	private static Iterator<NucleotideSequence> createExpectedFullRangeGappedIterator(String...seqs){
		List<NucleotideSequence> list = new ArrayList<NucleotideSequence>();
		for(String seq : seqs){
			list.add(new NucleotideSequenceBuilder(seq).build());
		}
		return list.iterator();
	}
	
}
