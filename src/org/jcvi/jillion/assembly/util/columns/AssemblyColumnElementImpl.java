package org.jcvi.jillion.assembly.util.columns;

import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

final class AssemblyColumnElementImpl implements QualifiedAssemblyColumnElement{

	//since it's only a direction and nucleotide, qv triple
	//we can compute all combinations and cache them
	private static AssemblyColumnElementImpl[][][] CACHE;
	private static byte DIRECTION_FWD = 0;
	private static byte DIRECTION_REV = 1;
	
	private static Nucleotide[] NUC_CACHE;
	static {
		List<Nucleotide> values = Nucleotide.ALL_VALUES;
		NUC_CACHE = Nucleotide.values();
		CACHE = new AssemblyColumnElementImpl[values.size()][2][128];
		//should be in ordinal order
		for(Nucleotide n : values) {
			for(int i=0; i< PhredQuality.MAX_VALUE; i++) {
				CACHE[n.ordinal()][DIRECTION_FWD][i] = new AssemblyColumnElementImpl(n.getOrdinalAsByte(), DIRECTION_FWD, (byte)i);
				CACHE[n.ordinal()][DIRECTION_REV][i] = new AssemblyColumnElementImpl(n.getOrdinalAsByte(), DIRECTION_REV, (byte)i);
			}
		}
	}
	
	public static AssemblyColumnElementImpl valueOf(Nucleotide n, Direction dir, PhredQuality quality) {
		return CACHE[n.ordinal()][dir==Direction.FORWARD? DIRECTION_FWD : DIRECTION_REV][quality.getQualityScore()];
	}
	private final byte n;
	private final byte dir;
	private final byte qv;
	private AssemblyColumnElementImpl(byte n, byte dir, byte qv) {
		this.n = n;
		this.dir = dir;
		this.qv = qv;
	}
	
	@Override
	public PhredQuality getQuality() {
		return PhredQuality.valueOf(qv);
	}

	@Override
	public byte getQualityScore() {
		return qv;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dir, n, qv);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyColumnElementImpl other = (AssemblyColumnElementImpl) obj;
		return dir == other.dir && n == other.n && qv == other.qv;
	}
	@Override
	public Nucleotide getBase() {
		return NUC_CACHE[n];
	}
	@Override
	public Direction getDirection() {
		return dir==DIRECTION_FWD? Direction.FORWARD: Direction.REVERSE;
	}
	
	
	
}
