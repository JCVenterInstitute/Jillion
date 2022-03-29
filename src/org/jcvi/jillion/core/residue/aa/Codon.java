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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
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

	public static Codon merge(Iterable<Codon> codons) {
	
		List<Nucleotide> a = new ArrayList<>();
		List<Nucleotide> b = new ArrayList<>();
		List<Nucleotide> c = new ArrayList<>();
		List<AminoAcid> aas = new ArrayList<AminoAcid>();
		Codon first=null;
		boolean isStart=false;
		boolean isStop=false;
		for(Codon codon: codons) {
			if(first==null) {
				first= codon;
			}
			aas.add(codon.aminoAcid);
			a.add(codon.triplet.getFirst());
			b.add(codon.triplet.getSecond());
			c.add(codon.triplet.getThird());
			isStart |=codon.isStart;
			isStop &= codon.isStop;
		}
		if(aas.size()==1) {
			return first;
		}
		return new Codon(Triplet.create(Nucleotide.getAmbiguityFor(a), Nucleotide.getAmbiguityFor(b), Nucleotide.getAmbiguityFor(c)),
				AminoAcid.merge(aas),
				isStart, isStop
				);
		
	}
	
}
