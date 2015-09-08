/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.net.URI;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sff.SffFlowgram;

class FlowgramReadDataAdaptedIterator implements StreamingIterator<ReadData>{
	private final StreamingIterator<? extends SffFlowgram> flowgramIterator;
	private final URI sffFileUri;
	public FlowgramReadDataAdaptedIterator(StreamingIterator<? extends SffFlowgram> flowgramIterator, File sffFile){
		this.flowgramIterator = flowgramIterator;	
		this.sffFileUri = sffFile.toURI();
	}
	@Override
	public boolean hasNext() {
		return flowgramIterator.hasNext();
	}

	@Override
	public ReadData next() {
		SffFlowgram nextFlowgram = flowgramIterator.next();
		return new ReadData.Builder(nextFlowgram)
										.setUri(sffFileUri)
										.build();
	
	}

	@Override
	public void remove() {
		flowgramIterator.remove();
		
	}
	@Override
	public void close() {
	   flowgramIterator.close();
		
	}

}
