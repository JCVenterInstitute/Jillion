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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class AbstractFastqRecordVisitor implements FastqRecordVisitor{

	private final String id;
	private final String optionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private NucleotideSequence currentBasecalls;
	private QualitySequence currentQualities;

	
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec) {
		this.id = id;
		this.optionalComment = optionalComment;
		this.qualityCodec = qualityCodec;
	}

	@Override
	public final void visitNucleotides(NucleotideSequence nucleotides) {
		currentBasecalls = nucleotides;
		
	}

	@Override
	public final void visitEncodedQualities(String encodedQualities) {
		currentQualities = qualityCodec.decode(encodedQualities);
		
	}

	@Override
	public final void visitEnd() {
		visitRecord(new FastqRecordBuilder(id, currentBasecalls, currentQualities)
									.comment(optionalComment)
									.build());		
	}
	
	@Override
	public void halted() {
		//no-op			
	}

	protected abstract void visitRecord(FastqRecord record);

}
