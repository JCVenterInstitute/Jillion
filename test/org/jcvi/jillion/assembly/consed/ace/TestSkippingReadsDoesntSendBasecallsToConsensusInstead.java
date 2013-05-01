package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.assembly.consed.ace.AceHandler;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
/**
 * Bug detected 4/11/2013 VHTNGS-600 internal JCVI JIRA
 * reveals bug where if AceVisitor skips underlying reads
 * the parser thinks it's still in "consensus mode"
 * and all basecalls it sees are sent to the {@link AceContigVisitor#visitBasesLine(String)}
 * instead of skipped.
 * @author dkatzel
 *
 */
public class TestSkippingReadsDoesntSendBasecallsToConsensusInstead {

	@Test
	public void skipReadsShouldAlsoSkipTheirBases() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestSkippingReadsDoesntSendBasecallsToConsensusInstead.class);
	
		File aceFile = helper.getFile("files/sample.ace");
		
		final ReadSkipperConsensusCollector skippedReadCollector = new ReadSkipperConsensusCollector();
		final ReadParsedConsensusCollector parsedReadCollector = new ReadParsedConsensusCollector();
		
		AceHandler parser =AceFileParser.create(aceFile);
		
		parser.accept(new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				return skippedReadCollector;
			}
			
		});
		
		parser.accept(new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				return parsedReadCollector;
			}
			
		});
		
		assertEquals(parsedReadCollector.getConsensusSequence(), skippedReadCollector.getConsensusSequence());
	}
	
	
	private static class ReadSkipperConsensusCollector extends AbstractAceContigVisitor{

		private NucleotideSequenceBuilder consensusSequence = new NucleotideSequenceBuilder();
		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			consensusSequence.append(mixedCaseBasecalls);
		}

		@Override
		public AceContigReadVisitor visitBeginRead(String readId,
				int gappedLength) {
			return null;
		}
		
		public NucleotideSequence getConsensusSequence(){
			return consensusSequence.build();
		}
	}
	
	private static class ReadParsedConsensusCollector extends AbstractAceContigVisitor{

		private NucleotideSequenceBuilder consensusSequence = new NucleotideSequenceBuilder();
		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			consensusSequence.append(mixedCaseBasecalls);
		}

		@Override
		public AceContigReadVisitor visitBeginRead(String readId,
				int gappedLength) {
			return new AbstractAceContigReadVisitor() {
			};
		}
		
		public NucleotideSequence getConsensusSequence(){
			return consensusSequence.build();
		}
	}
}
