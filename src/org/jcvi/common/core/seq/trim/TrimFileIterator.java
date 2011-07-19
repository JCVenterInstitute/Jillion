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

package org.jcvi.common.core.seq.trim;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.AbstractBlockingCloseableIterator;

public class TrimFileIterator extends AbstractBlockingCloseableIterator<Range>{

	private final File trimFile;
	public static TrimFileIterator createNewIteratorFor(File trimFile){
		TrimFileIterator iter;
				iter = new TrimFileIterator(trimFile);
				iter.start();
			
	    	
	    	return iter;
	    }
	
	public TrimFileIterator(File trimFile) {
		this.trimFile = trimFile;
	}


	@Override
	protected void backgroundThreadRunMethod() {
		TrimFileVisitor visitor = new TrimFileVisitor(){

			@Override
			public void visitFile() {			
			}

			@Override
			public void visitEndOfFile() {
				TrimFileIterator.this.finishedIterating();
				
			}

			@Override
			public boolean visitTrim(String id, Range trimRange) {
				TrimFileIterator.this.blockingPut(trimRange);
				return !TrimFileIterator.this.isClosed();
			}

            @Override
            public void visitLine(String line) {
                
            }
			
		};
		
		try {
			TrimFileUtil.parseTrimFile(trimFile, visitor);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("error parsing trim file " + trimFile.getAbsolutePath(), e);
		}
		
	}

}
