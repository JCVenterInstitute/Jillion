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
package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
/**
 * {@code AbstractNucleotideFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link NucleotideFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link NucleotideSequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(NucleotideFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractNucleotideFastaRecordVisitor extends  AbstractFastaRecordVisitor{

	public AbstractNucleotideFastaRecordVisitor(String id, String comment) {
		super(id,comment);
	}

	
	protected abstract void visitRecord(NucleotideFastaRecord fastaRecord);

	@Override
	protected final  void visitRecord(String id, String optionalComment,
			String fullBody) {
		NucleotideFastaRecord record = new NucleotideSequenceFastaRecordBuilder(id, fullBody)
													.comment(optionalComment)
													.build();
		visitRecord(record);
	}
	
}
