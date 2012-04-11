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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code SffFileIterator} is a {@link CloseableIterator}
 * that can iterate over {@link Flowgram}s contained
 * in a sff file.
 * @author dkatzel
 *
 */
public final class SffFileIterator extends AbstractBlockingCloseableIterator<Flowgram>{

	private final File sffFile;
	
    public static SffFileIterator createNewIteratorFor(File sffFile){
    	SffFileIterator iter;
			iter = new SffFileIterator(sffFile);
			iter.start();
		
    	
    	return iter;
    }
	
	private SffFileIterator(File sffFile){
		this.sffFile = sffFile;
		 
	}

	@Override
	protected void backgroundThreadRunMethod() {
		 try {
         	SffFileVisitor visitor = new AbstractSffFlowgramVisitor() {
					
         		@Override
         		protected boolean visitFlowgram(Flowgram flowgram) {
         			SffFileIterator.this.blockingPut(flowgram);
         			return !SffFileIterator.this.isClosed();
         		}

				};
             SffFileParser.parseSFF(sffFile, visitor);
         } catch (IOException e) {
             //should never happen
             throw new RuntimeException(e);
         }
		
	}
	
	

}
