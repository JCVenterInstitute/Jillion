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
package org.jcvi.jillion.internal.fasta;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractResuseableFastaRecordVisitor}
 * is a {@link FastaRecordVisitor}
 * that gathers consecutive calls to
 * {@link #visitBodyLine(String)} to compile the entire
 * body of a fasta record.  This class can 
 * be reused by resetting the current id and comment
 * using {@link #prepareNewRecord(String, String)}
 * so we don't create new instances for each
 * fasta record to be visited. 
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractResuseableFastaRecordVisitor implements FastaRecordVisitor{
	private String currentId;
	private String currentComment;
	private StringBuilder builder;
	
	public final void prepareNewRecord(String id, String optionalComment){
		this.currentId = id;
		this.currentComment = optionalComment;
		builder = new StringBuilder();
	}
	@Override
	public final void visitBodyLine(String line) {
		builder.append(line);
		
	}

	@Override
	public final void visitEnd() {
		visitRecord(currentId, currentComment, builder.toString());		
	}
	@Override
	public void halted() {
		//no-op				
	}
	public abstract void visitRecord(String id, String optionalComment, String fullBody);
}

