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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.net.URI;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.Trace;

final class ReadData implements Trace{

	private final PositionSequence positions;
	private final URI uri;
	private final String id;
	private final NucleotideSequence seq;
	private final QualitySequence quals;
	
	private ReadData(Builder builder){
		this.id = builder.id;
		this.seq = builder.seq;
		this.quals = builder.quals;
		this.positions = builder.positions;
		this.uri = builder.uri;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public NucleotideSequence getNucleotideSequence() {
		return seq;
	}
	@Override
	public QualitySequence getQualitySequence() {
		return quals;
	}
	public PositionSequence getPositions() {
		return positions;
	}
	public URI getUri() {
		return uri;
	}
	
	
	static final class Builder{
		private PositionSequence positions;
		private URI uri;
		private final String id;
		private NucleotideSequence seq;
		private QualitySequence quals;
		
		public Builder(Trace trace){
			this.id = trace.getId();
			this.seq = trace.getNucleotideSequence();
			this.quals = trace.getQualitySequence();			
		}
		
		public Builder(String id, NucleotideSequence seq){
			if(id ==null){
				throw new NullPointerException("id can not be null");
			}
			if(seq ==null){
				throw new NullPointerException("seq can not be null");
			}
			this.id = id;
			this.seq = seq;
		}
		public Builder setQualities(QualitySequence qualities){
			this.quals = qualities;
			return this;
		}
		public Builder setUri(URI uri){
			this.uri = uri;
			return this;
		}
		
		public Builder setPositions(PositionSequence pos){
			this.positions = pos;
			return this;
		}
		
		public ReadData build(){
			return new ReadData(this);
		}

		public String getId() {
			return id;
		}

		public Builder setNucleotideSequence(NucleotideSequence nucleotideSequence) {
			if(nucleotideSequence ==null){
				throw new NullPointerException("nucleotide sequence can not be null");
			}
			this.seq = nucleotideSequence;
			return this;
			
		}
	}
	
}
