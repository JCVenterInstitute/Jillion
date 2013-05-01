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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqRecord;


class FastqConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{

	private final StreamingIterator<? extends FastqRecord> fastqIterator;
	private final Map<String,String> requiredComments;
	private final Date phdDate;
	private final File fastqFile;
	public FastqConsedPhdAdaptedIterator(StreamingIterator<? extends FastqRecord> fastqIterator,  File fastqFile,Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastqIterator = fastqIterator;	
		this.phdDate = new Date(phdDate.getTime());
		this.fastqFile = fastqFile;
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		FastqRecord nextFastq = fastqIterator.next();
		String id = nextFastq.getId();
		Phd phd = new PhdBuilder(id, nextFastq.getNucleotideSequence(),nextFastq.getQualitySequence())
							.comments(requiredComments)
							.fakePeaks()
							.build();
		
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(fastqFile, id, phdDate);
		return new PhdReadRecord(phd, info);
	}

	@Override
	public void remove() {
		fastqIterator.remove();
		
	}
	@Override
	public void close() throws IOException {
		fastqIterator.close();
		
	}
}
