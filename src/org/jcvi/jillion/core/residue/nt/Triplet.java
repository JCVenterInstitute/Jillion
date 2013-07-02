package org.jcvi.jillion.core.residue.nt;

/**
 * {@code Triplet} is a group of three
 * consecutive {@link Nucleotide}s.
 * @author dkatzel
 *
 */
public final class Triplet {

	private final byte first,second,third;
	/**
	 * Our cache of all unique triplets.  Uses the fly-weight
	 * design pattern to not create more instances
	 * that we need to.
	 * <p/>
	 * NOTE: We don't synchronize the cache because it is 
	 * too expensive
	 */
	private static final Triplet[][][] CACHE = new Triplet[16][16][16];
	
	public static Triplet create(char first, char second, char third){
		return create(Nucleotide.parse(first), Nucleotide.parse(second), Nucleotide.parse(third));
	}
	public static Triplet create(Nucleotide first, Nucleotide second, Nucleotide third){
		int o1 = first.ordinal();
		int o2 = second.ordinal();
		int o3 = third.ordinal();
		Triplet fromCache = CACHE[o1][o2][o3];
		if(fromCache !=null){
			return fromCache;
		}
		Triplet newTriplet = new Triplet(o1,o2,o3);
		CACHE[o1][o2][o3] = newTriplet;
		return newTriplet;
	}
	
	static void clearCache(){
		for(int i=0; i<CACHE.length; i++){
			for(int j=0; j<CACHE[i].length; j++){
				for(int k=0; k<CACHE[i][j].length; k++){
					CACHE[i][j][k]= null;
				}
			}
		}
	}
	private Triplet(int first, int second, int third){
		this.first = (byte)first;
		this.second = (byte)second;
		this.third = (byte)third;
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
		if (first != other.first) {
			return false;
		}
		if (second != other.second) {
			return false;
		}
		if (third != other.third) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString(){
		return new StringBuilder(3)
					.append(Nucleotide.VALUES.get(first))
					.append(Nucleotide.VALUES.get(second))
					.append(Nucleotide.VALUES.get(third))
					.toString();
					
	}
	
	public Nucleotide getFirst(){
		return Nucleotide.VALUES.get(first);
	}
	public Nucleotide getSecond(){
		return Nucleotide.VALUES.get(second);
	}
	public Nucleotide getThrid(){
		return Nucleotide.VALUES.get(third);
	}
}
