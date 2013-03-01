/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ace;

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
		AceFileVisitor2 visitor = new AceFileVisitor2(){

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
		
		AceFileParser2.create(aceFile).accept(visitor);
		assertTrue(visitedHalted.get());
	}
	@Test
	public void stopParsingAtFinalContigShouldSkipTags() throws IOException{
		final AtomicBoolean visitHalted = new AtomicBoolean(false);
		AceFileVisitor2 visitor2 = new AceFileVisitor2(){
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
			
			
		AceFileParser2.create(aceFile).accept(visitor2);
		assertTrue(visitHalted.get());
	}

}
