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

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;

/**
 * {@code AbstractSffFlowgramVisitor} is an {@link SffFileVisitor}
 * implementation that builds {@link Flowgram} instances
 * as the various SFF data blocks are being parsed.  This
 * simplifies visiting sff encoded files if all the client
 * cares about are the final constructed {@link Flowgram}s.
 * @author dkatzel
 *
 */
public abstract class AbstractSffFlowgramVisitor implements SffFileVisitor{

	private SffReadHeader currentReadHeader;
	
	/**
	 * Visit the current {@link Flowgram} in the file
	 * being parsed.
	 * @param flowgram
	 * @return an instance of {@link ReadDataReturnCode};
	 * can not be null.
	 */
	protected abstract ReadDataReturnCode visitFlowgram(Flowgram flowgram);
	@Override
	public void visitFile() {
		//no-op
	}

	
	@Override
	public void visitEndOfFile() {
		//no-op
	}

	@Override
	public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
		return CommonHeaderReturnCode.PARSE_READS;
	}

	@Override
	public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
		this.currentReadHeader = readHeader;
		return ReadHeaderReturnCode.PARSE_READ_DATA;
	}

	@Override
	public ReadDataReturnCode visitReadData(SffReadData readData) {
		
		return visitFlowgram(
				SffFlowgram.create(currentReadHeader, readData));
	}
	
	

}
