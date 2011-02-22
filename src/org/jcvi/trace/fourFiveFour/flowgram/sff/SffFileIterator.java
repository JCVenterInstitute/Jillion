/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.util.AbstractBlockingCloseableIterator;
import org.jcvi.util.CloseableIterable;

/**
 * {@code SffFileIterator} is a {@link CloseableIterable}
 * that can iterate over {@link SFFFlowgram}s contained
 * in a sff file.
 * @author dkatzel
 *
 */
public class SffFileIterator extends AbstractBlockingCloseableIterator<SFFFlowgram>{

	private final File sffFile;
	
    public static SffFileIterator createNewIteratorFor(File sffFile){
    	SffFileIterator iter;
		try {
			iter = new SffFileIterator(sffFile);
			iter.start();
		} catch (InterruptedException e) {
			throw new IllegalStateException("error creating sff iterator for " + sffFile.getAbsolutePath(),e);
		}
    	
    	return iter;
    }
	
	private SffFileIterator(File sffFile){
		this.sffFile = sffFile;
		 
	}

	@Override
	protected void backgroundThreadRunMethod() {
		 try {
         	SffFileVisitor visitor = new AbstractSFFFlowgramVisitor() {
					
         		@Override
         		protected boolean visitSFFFlowgram(SFFFlowgram flowgram) {
         			SffFileIterator.this.blockingPut(flowgram);
         			return !SffFileIterator.this.isClosed();
         		}

					@Override
					public void visitEndOfFile() {
					    SffFileIterator.this.finishedIterating();
					}
				};
             SffParser.parseSFF(sffFile, visitor);
         } catch (IOException e) {
             //should never happen
             throw new RuntimeException(e);
         }
		
	}
	
	

}
