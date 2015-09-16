/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.trace.fastq.CommentedParsedFastqRecord;
import org.jcvi.jillion.internal.trace.fastq.ParsedFastqRecord;

public abstract class AbstractFastqRecordVisitor implements FastqRecordVisitor{

	private final String id;
	private final String optionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private NucleotideSequence currentBasecalls;
	private QualitySequence currentQualities;
	private String encodedQualities;
	private boolean turnOffCompression;
	
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec){
		this(id,optionalComment, qualityCodec, false);
	}
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec, boolean turnOffCompression) {
		this.id = id;
		this.optionalComment = optionalComment;
		this.qualityCodec = qualityCodec;
		this.turnOffCompression = turnOffCompression;
	}

	@Override
	public final void visitNucleotides(NucleotideSequence nucleotides) {
		currentBasecalls = nucleotides;
		
	}

	@Override
	public final void visitEncodedQualities(String encodedQualities) {
		this.encodedQualities = encodedQualities;
		
	}
	
	

	@Override
	public void visitQualities(QualitySequence qualities) {
		currentQualities = qualities;
		
	}

	@Override
	public final void visitEnd() {
	    FastqRecord fastqRecord;
	    if(currentQualities !=null){
	        fastqRecord = new FastqRecordBuilder(id, currentBasecalls, currentQualities)
            							.comment(optionalComment)
            							.build();
       	
	    }else{
	        if(optionalComment ==null){
	            fastqRecord = new ParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression);
	        }else{
	            fastqRecord = new CommentedParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression, optionalComment);
	        }
	    }
	    
	    visitRecord(fastqRecord);
	}
	
	@Override
	public void halted() {
		//no-op			
	}

	protected abstract void visitRecord(FastqRecord record);
	
	

}
