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

package org.jcvi.assembly.cas.read;

import java.io.IOException;


import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.FastXRecord;
import org.jcvi.trace.sanger.FileSangerTrace;
import org.jcvi.trace.sanger.SangerFileDataStore;
import org.jcvi.util.CloseableIterator;


public class FastXRecordFileSangerTraceAdapterIterator implements CloseableIterator<FileSangerTrace>{

	private final SangerFileDataStore<FileSangerTrace> sangerTraceDataStore;
	private final CloseableIterator<FastXRecord<?>> fastaIterator;
	
	public FastXRecordFileSangerTraceAdapterIterator(
			CloseableIterator<FastXRecord<?>> fastaIterator,
			SangerFileDataStore<FileSangerTrace> sangerTraceDataStore){
		this.sangerTraceDataStore = sangerTraceDataStore;
		this.fastaIterator = fastaIterator;
	}
	@Override
	public void remove() {
		fastaIterator.remove();		
	}

	@Override
	public boolean hasNext() {
		return fastaIterator.hasNext();
	}

	@Override
	public void close() throws IOException {
		fastaIterator.close();
		
	}

	@Override
	public FileSangerTrace next() {
		FastXRecord fastXRecord = fastaIterator.next();
		try {
			sangerTraceDataStore.get(fastXRecord.getId());
		} catch (DataStoreException e) {
			throw new IllegalStateException("could not find sanger trace file for "+fastXRecord.getId(),e);
		}
		return null;
	}

}
