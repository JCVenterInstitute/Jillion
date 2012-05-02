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

package org.jcvi.common.core.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;


public class FastqConsedPhdAdaptedIterator implements PhdReadRecordIterator{

	private final CloseableIterator<? extends FastqRecord> fastqIterator;
	private final Properties requiredComments;
	private final Date phdDate;
	private final File fastqFile;
	public FastqConsedPhdAdaptedIterator(CloseableIterator<? extends FastqRecord> fastqIterator,  File fastqFile,Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastqIterator = fastqIterator;	
		this.phdDate = phdDate;
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
		Phd phd = ArtificialPhd.createNewbler454Phd(
				id, 
				nextFastq.getNucleotides(), 
				nextFastq.getQualities(),
				requiredComments);
		
		PhdInfo info = ConsedUtil.generatePhdInfoFor(fastqFile, id, phdDate);
		return new DefaultPhdReadRecord(phd, info);
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
