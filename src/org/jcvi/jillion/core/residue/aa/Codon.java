/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
