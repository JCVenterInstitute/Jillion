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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.nt.Triplet;

public final class Codon {

	private final Triplet triplet;
	
	private final AminoAcid aminoAcid;
	
	private final boolean isStart, isStop;

	private Codon(Triplet triplet, AminoAcid aminoAcid, boolean isStart,
			boolean isStop) {
		this.triplet = triplet;
		this.aminoAcid = aminoAcid;
		this.isStart = isStart;
		this.isStop = isStop;
	}

	public Triplet getTriplet() {
		return triplet;
	}

	public AminoAcid getAminoAcid() {
		return aminoAcid;
	}

	public boolean isStart() {
		return isStart;
	}

	public boolean isStop() {
		return isStop;
	}
	
	public static final class Builder{
		private final Triplet triplet;
		
		private final AminoAcid aminoAcid;

		private boolean isStart, isStop;
		
		
		public Builder(Triplet triplet, AminoAcid aminoAcid) {
			if(triplet ==null){
				throw new NullPointerException("triplet can not be null");
			}
			if(aminoAcid ==null){
				throw new NullPointerException("aminoAcid can not be null");
			}
			this.triplet = triplet;
			this.aminoAcid = aminoAcid;
		}
		
		public Builder isStop(boolean value){
			this.isStop = value;
			return this;
		}
		
		public Builder isStart(boolean value){
			this.isStart = value;
			return this;
		}
		
		public Codon build(){
			return new Codon(triplet, aminoAcid,isStart, isStop);
		}
	}
	
}
