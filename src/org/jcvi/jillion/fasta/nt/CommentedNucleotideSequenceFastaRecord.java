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

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class CommentedNucleotideSequenceFastaRecord extends UnCommentedNucleotideSequenceFastaRecord{

	private final String comment;
	public CommentedNucleotideSequenceFastaRecord(String id,
			NucleotideSequence sequence, String comment) {
		super(id, sequence);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.equals(obj);
	}
	
	

}
