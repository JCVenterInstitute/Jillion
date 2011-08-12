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

package org.jcvi.common.core.assembly.contig.ace.consed;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.common.core.assembly.contig.ace.PhdInfo;
import org.jcvi.common.core.seq.read.trace.sanger.FileSangerTrace;
import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.joda.time.DateTime;

public class SangerTraceConsedPhdAdaptedIterator implements PhdReadRecordIterator{

	private final CloseableIterator<? extends FileSangerTrace> fastqIterator;
	private final DateTime phdDate;
	private final Properties requiredComments;
	public SangerTraceConsedPhdAdaptedIterator(CloseableIterator<? extends FileSangerTrace> flowgramIterator,  DateTime phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.phdDate = phdDate;
		this.fastqIterator = flowgramIterator;		
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		FileSangerTrace next = fastqIterator.next();
		String name;
		try {
			name = FilenameUtils.getBaseName(next.getFile().getName());
		
			Phd phd= new DefaultPhd(
					name, 
					next.getBasecalls(), 
					next.getQualities(),
					next.getPeaks(),
					requiredComments);
			
			PhdInfo info = ConsedUtil.generatePhdInfoFor(next.getFile(), name, phdDate);
			return new DefaultPhdReadRecord(phd, info);
		} catch (IOException e) {
			throw new IllegalStateException("could not get sanger trace file",e);
		}
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
