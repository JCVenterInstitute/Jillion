package org.jcvi.common.core.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
public class TestAceFileParserStopParsingMiddleContig {

	private final File aceFile;
	
	public TestAceFileParserStopParsingMiddleContig() throws IOException{
		ResourceFileServer resources = new ResourceFileServer(TestAceFileParserStopParsingMiddleContig.class);
		aceFile = resources.getFile("files/fluSample.ace");
	}
	@Test
	public void topParsingAfterFirstContig() throws IOException{
		AceFileVisitor visitor = new AbstractAceFileVisitor() {
			@Override
			public EndContigReturnCode handleEndOfContig() {
				return EndContigReturnCode.STOP_PARSING;
			}

			@Override
			protected void visitNewContig(String contigId,
					NucleotideSequence consensus, int numberOfBases, int numberOfReads,
					boolean isComplemented) {
				//only the first contig is valid
				assertEquals("22934-PB2",contigId);
			}
			
			@Override
			protected void visitAceRead(String readId,
					NucleotideSequence validBasecalls, int offset, Direction dir,
					Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public synchronized void visitBeginConsensusTag(String id,
					String type, String creator, long gappedStart,
					long gappedEnd, Date creationDate, boolean isTransient) {
				//any tags should not be visited?
				throw new IllegalStateException("should not visit tags if stop parsing");
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				//any tags should not be visited?
				throw new IllegalStateException("should not visit tags if stop parsing");
			}

			@Override
			public void visitReadTag(String id, String type, String creator,
					long gappedStart, long gappedEnd, Date creationDate,
					boolean isTransient) {
				//any tags should not be visited?
				throw new IllegalStateException("should not visit tags if stop parsing");
			}
			
		};
		
		AceFileParser.parse(aceFile, visitor);
	}
	@Test
	public void stopParsingAtFinalContigShouldSkipTags() throws IOException{
			AceFileVisitor visitor = new AbstractAceFileVisitor() {
				boolean keepParsing =true;
			
				@Override
				public EndContigReturnCode handleEndOfContig() {
					return keepParsing ? EndContigReturnCode.KEEP_PARSING : EndContigReturnCode.STOP_PARSING;
				}

				@Override
				protected void visitNewContig(String contigId,
						NucleotideSequence consensus, int numberOfBases, int numberOfReads,
						boolean isComplemented) {
					//only the first contig is valid
					keepParsing = !"22934-NS".equals(contigId);
				}
				
				@Override
				protected void visitAceRead(String readId,
						NucleotideSequence validBasecalls, int offset, Direction dir,
						Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public synchronized void visitBeginConsensusTag(String id,
						String type, String creator, long gappedStart,
						long gappedEnd, Date creationDate, boolean isTransient) {
					//any tags should not be visited?
					throw new IllegalStateException("should not visit tags if stop parsing");
				}

				@Override
				public void visitWholeAssemblyTag(String type, String creator,
						Date creationDate, String data) {
					//any tags should not be visited?
					throw new IllegalStateException("should not visit tags if stop parsing");
				}

				@Override
				public void visitReadTag(String id, String type, String creator,
						long gappedStart, long gappedEnd, Date creationDate,
						boolean isTransient) {
					//any tags should not be visited?
					throw new IllegalStateException("should not visit tags if stop parsing");
				}
				
			};
			
			AceFileParser.parse(aceFile, visitor);
	}

}
