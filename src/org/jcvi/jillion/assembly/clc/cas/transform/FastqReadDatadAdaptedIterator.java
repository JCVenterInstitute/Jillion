/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.net.URI;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqRecord;


class FastqReadDatadAdaptedIterator implements StreamingIterator<ReadData>{

	private final StreamingIterator<? extends FastqRecord> fastqIterator;
	private final URI fastqFileUri;
	public FastqReadDatadAdaptedIterator(StreamingIterator<? extends FastqRecord> fastqIterator,  File fastqFile ){
		this.fastqIterator = fastqIterator;	
		this.fastqFileUri = fastqFile.toURI();
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public ReadData next() {
		FastqRecord nextFastq = fastqIterator.next();
		return new ReadData.Builder(nextFastq)
							.setUri(fastqFileUri)
							.build();
	}

	@Override
	public void remove() {
		fastqIterator.remove();
		
	}
	@Override
	public void close(){
		fastqIterator.close();
		
	}
}
