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
/**
 * {@code AbstractSFFFlowgramVisitor} is an {@link SffFileVisitor}
 * implementation that builds {@link SFFFlowgram} instances
 * as the various SFF data blocks are being parsed.  This
 * simplifies visiting SFFFlowgrams if all the client
 * cares about are the final constructed SFFFlowgrams.
 * @author dkatzel
 *
 */
public abstract class AbstractSFFFlowgramVisitor implements SffFileVisitor{

	private SFFReadHeader currentReadHeader;
	
	/**
	 * Visit the current {@link SFFFlowgram} in the file
	 * being parsed.
	 * @param flowgram
	 * @return {@code true} to keep parsing the sff file;
	 * {@code false} to halt parsing entirely.
	 */
	protected abstract boolean visitSFFFlowgram(SFFFlowgram flowgram);
	@Override
	public void visitFile() {}

	
	@Override
	public void visitEndOfFile() {}

	@Override
	public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
		return true;
	}

	@Override
	public boolean visitReadHeader(SFFReadHeader readHeader) {
		this.currentReadHeader = readHeader;
		return true;
	}

	@Override
	public boolean visitReadData(SFFReadData readData) {
		
		return visitSFFFlowgram(
				SFFUtil.buildSFFFlowgramFrom(currentReadHeader, readData));
	}
	
	

}
