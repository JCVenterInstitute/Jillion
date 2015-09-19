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
package org.jcvi.jillion.assembly.consed.ace;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymock.IAnswer;
import org.jcvi.jillion.assembly.consed.ace.AceConsensusTagVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestAceFileParserStopParsingMiddleContig {

	private final File aceFile;
	
	public TestAceFileParserStopParsingMiddleContig() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestAceFileParserStopParsingMiddleContig.class);
		aceFile = resources.getFile("files/fluSample.ace");
	}
	@Test
	public void topParsingAfterFirstContig() throws IOException{
		final AtomicBoolean visitedHalted= new AtomicBoolean(false);
		AceFileVisitor visitor = new AceFileVisitor(){

			@Override
			public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
				//no-op				
			}

			@Override
			public AceContigVisitor visitContig(
					final AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				//only the first contig is valid
				assertEquals("22934-PB2",contigId);
				AceContigVisitor mock = createNiceMock(AceContigVisitor.class);
				mock.visitEnd();
				expectLastCall().andAnswer(new IAnswer<Void>() {

					@Override
					public Void answer() throws Throwable {
						callback.haltParsing();
						return null;
					}
					
				});
				
				replay(mock);
				return mock;
			}

			@Override
			public void visitReadTag(String id, String type, String creator,
					long gappedStart, long gappedEnd, Date creationDate,
					boolean isTransient) {
				fail("should not get to tags");				
			}

			@Override
			public AceConsensusTagVisitor visitConsensusTag(String id,
					String type, String creator, long gappedStart,
					long gappedEnd, Date creationDate, boolean isTransient) {
				fail("should not get to tags");	
				return null; // need return to compile
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				fail("should not get to tags");					
			}

			@Override
			public void visitEnd() {
				fail("should not call visitEnd on halt");	
				
			}

			@Override
			public void halted() {
				visitedHalted.set(true);				
			}
			
		};
		
		AceFileParser.create(aceFile).parse(visitor);
		assertTrue(visitedHalted.get());
	}
	@Test
	public void stopParsingAtFinalContigShouldSkipTags() throws IOException{
		final AtomicBoolean visitHalted = new AtomicBoolean(false);
		AceFileVisitor visitor2 = new AceFileVisitor(){
			@Override
			public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
				//no-op
				
			}

			@Override
			public AceContigVisitor visitContig(
					final AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				//last contig
				if("22934-NS".equals(contigId)){
					AceContigVisitor lastContigVisitor = createNiceMock(AceContigVisitor.class);
					lastContigVisitor.visitEnd();
					expectLastCall().andAnswer(new IAnswer<Object>() {

						@Override
						public Object answer() throws Throwable {
							callback.haltParsing();
							return null; //needed to compile
						}
						
					});
					replay(lastContigVisitor);
					return lastContigVisitor;
				}
				return null;
			}

			@Override
			public void visitReadTag(String id, String type, String creator,
					long gappedStart, long gappedEnd, Date creationDate,
					boolean isTransient) {
				fail("should not get to tags");				
			}

			@Override
			public AceConsensusTagVisitor visitConsensusTag(String id,
					String type, String creator, long gappedStart,
					long gappedEnd, Date creationDate, boolean isTransient) {
				fail("should not get to tags");	
				return null; // need return to compile
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				fail("should not get to tags");					
			}

			@Override
			public void visitEnd() {
				fail("should not call visitEnd on halt");	
				
			}


			@Override
			public void halted() {
				visitHalted.set(true);				
			}
			
		};
			
			
		AceFileParser.create(aceFile).parse(visitor2);
		assertTrue(visitHalted.get());
	}

}
