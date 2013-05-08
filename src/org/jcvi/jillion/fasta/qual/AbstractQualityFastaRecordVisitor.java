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
package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractQualityFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link QualitySequenceFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link QualitySequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(QualitySequenceFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractQualityFastaRecordVisitor  extends AbstractFastaRecordVisitor{
	
	
	public AbstractQualityFastaRecordVisitor(String id, String comment) {
		super(id,comment);
	}
	
	@Override
	protected void visitRecord(String id, String comment,
			String fullBody) {
		QualityFastaRecord record = new QualityFastaRecordBuilder(id, fullBody)
												.comment(comment)
												.build();
		visitRecord(record);
		
	}


	protected abstract void visitRecord(QualityFastaRecord fastaRecord);
	

}
