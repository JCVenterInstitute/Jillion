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
package org.jcvi.jillion.core.residue.nt;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@code Triplet} is a group of three
 * consecutive {@link Nucleotide}s.
 * @author dkatzel
 *
 */
public final class Triplet implements Comparable<Triplet>{

	private final byte first,second,third;
	private static final byte URACIL_VALUE = Nucleotide.Uracil.getOrdinalAsByte();
	private static final byte THYMINE_VALUE = Nucleotide.Thymine.getOrdinalAsByte();
	private static final byte GAP_VALUE = Nucleotide.Gap.getOrdinalAsByte();
	
	private final boolean hasUracil;
	
	/**
	 * Our cache of all unique triplets.  Uses the fly-weight
	 * design pattern to not create more instances
	 * that we need to.
	 * <p/>
	 * NOTE: We don't synchronize the cache because it is 
	 * too expensive
	 */
	private static final int cacheSize = Nucleotide.values().length;
	private static final boolean[] isAmbiguityCache = new boolean[cacheSize];
	
	private static final Triplet[][][] CACHE = new Triplet[cacheSize][cacheSize][cacheSize];

	static {
		int i=0;
		for(Nucleotide n : Nucleotide.values()) {
			if(n.isAmbiguity()) {
				isAmbiguityCache[i]=true;
			}
			i++;
		}
	}
	public static Triplet create(char first, char second, char third){
		return create(Nucleotide.parse(first), Nucleotide.parse(second), Nucleotide.parse(third));
	}
	
	public Triplet withUracil() {
		boolean changed=false;
		byte a,b,c;
		if(first==THYMINE_VALUE) {
			a=URACIL_VALUE;
			changed=true;
		}else {
			a=first;
		}
		if(second==THYMINE_VALUE) {
			b=URACIL_VALUE;
			changed=true;
		}else {
			b=second;
		}
		if(third==THYMINE_VALUE) {
			c=URACIL_VALUE;
			changed=true;
		}else {
			c=third;
		}
		if(changed) {
			return getCachedValue(a,b,c); 
		}
		return this;
	}
	public Triplet withThymine() {
		if(!hasUracil) {
			return this;
		}
		byte a,b,c;
		if(first==URACIL_VALUE) {
			a=THYMINE_VALUE;
		
		}else {
			a=first;
		}
		if(second==URACIL_VALUE) {
			b=THYMINE_VALUE;
			
		}else {
			b=second;
		}
		if(third==URACIL_VALUE) {
			c=THYMINE_VALUE;
			
		}else {
			c=third;
		}
			
		return getCachedValue(a,b,c); 
		
	}

	public static Triplet create(Nucleotide first, Nucleotide second, Nucleotide third){
		int o1 = first.ordinal();
		int o2 = second.ordinal();
		int o3 = third.ordinal();

		return getCachedValue(o1, o2, o3);
	}

	private static Triplet getCachedValue(int o1, int o2, int o3) {
		Triplet fromCache = CACHE[o1][o2][o3];
		if(fromCache !=null){
			return fromCache;
		}
		Triplet newTriplet = new Triplet(o1,o2,o3);
		CACHE[o1][o2][o3] = newTriplet;
		return newTriplet;
	}
	
	static void clearCache(){

		for(int i=0; i<cacheSize; i++){
			for(int j=0; j<cacheSize; j++){
				for(int k=0; k<cacheSize; k++){
					CACHE[i][j][k]= null;
				}
			}
		}
	}
	private Triplet(int first, int second, int third){
		this.first = (byte)first;
		this.second = (byte)second;
		this.third = (byte)third;
		hasUracil = this.first==URACIL_VALUE || this.second==URACIL_VALUE || this.third == URACIL_VALUE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		result = prime * result + third;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Triplet)) {
			return false;
		}
		Triplet other = (Triplet) obj;

		return first == other.first && second == other.second && third == other.third;
	}
	
	
	@Override
	public String toString(){
		return new StringBuilder(3)
					.append(Nucleotide.getDnaValues().get(first))
					.append(Nucleotide.getDnaValues().get(second))
					.append(Nucleotide.getDnaValues().get(third))
					.toString();
					
	}
	
	public Nucleotide getFirst(){
		return Nucleotide.getDnaValues().get(first);
	}
	public Nucleotide getSecond(){
		return Nucleotide.getDnaValues().get(second);
	}
	public Nucleotide getThird(){
		return Nucleotide.getDnaValues().get(third);
	}

	public boolean hasGaps() {
		return first==GAP_VALUE || second == GAP_VALUE || third == GAP_VALUE;
	}
	
	public Set<Triplet> explode(){
		if(isAmbiguityCache[first] || isAmbiguityCache[second] || isAmbiguityCache[third]) {
        	//handle ambiguities
        	Set<Triplet> triplets = new LinkedHashSet<Triplet>();
        	for(Nucleotide f : getFirst().getBasesFor()) {
        		for(Nucleotide s : getSecond().getBasesFor()) {
        			for(Nucleotide t: getThird().getBasesFor()) {
        				triplets.add(Triplet.create(f, s, t));
        			}
        		}
        	}
        	return triplets;
		}
		return Set.of(this);
	}

	@Override
	public int compareTo(Triplet o) {
		//we cache so this might to be an exact match
		if(this==o) {
			return 0;
		}
		int f = Byte.compare(first, o.first);
		if(f!=0) {
			return f;
		}
		int s = Byte.compare(second, o.second);
		if(s!=0) {
			return s;
		}
		return Byte.compare(third, o.third);
	}

	/**
	 * Get the number of bases in this triplet that are different than 
	 * the other triplet.
	 * @param other the other triplet to compare can not be null.
	 * @param penalizeAmbiguties
	 * @return the number of bases that are different in the triplet.
	 * @since 6.0
	 * @see #getNumChanges(Triplet, boolean)
	 */
	public int getNumChanges(Triplet other) {
		return getNumChangesScore(other, false);
		
	}
	/**
	 * Compute a score based on the number of differences between this triplet
	 * and the other triplet.  The difference between this method and {@link #getNumChanges(Triplet)}
	 * is that this method penalizes mismatching ambiguities more.
	 * @param other the other triplet to compare can not be null.
	 * @param penalizeAmbiguties
	 * @return the number of bases that are different in the triplet.
	 * @since 6.0
	 */
	public int computeChangeScore(Triplet other) {
		return getNumChangesScore(other, true);
	}
	/**
	 * Get the number of bases in this triplet that are different than 
	 * the other triplet.
	 * @param other the other triplet to compare can not be null.
	 * @param penalizeAmbiguties
	 * @return the number of bases that are different in the triplet.
	 * @since 6.0
	 */
	private int getNumChangesScore(Triplet other, boolean penalizeAmbiguties) {
		int changes=0;
		if(first!= other.first) {
			if(penalizeAmbiguties && isAmbiguityCache[other.first]) {
				changes+=4;
			}else {
				changes++;
			}
		}
		if(second!= other.second) {
			if(penalizeAmbiguties && isAmbiguityCache[other.second]) {
				changes+=4;
			}else {
				changes++;
			}
		}
		if(third!= other.third) {
			if(penalizeAmbiguties && isAmbiguityCache[other.third]) {
				changes+=4;
			}else {
				changes++;
			}
		}
		return changes;
		
	}

}
