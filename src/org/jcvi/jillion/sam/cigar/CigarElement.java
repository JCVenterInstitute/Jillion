package org.jcvi.jillion.sam.cigar;
/**
 * Value object containing a single 
 * element of a Cigar.
 * @author dkatzel
 *
 */
public final class CigarElement {
	//since the BAM file
	//stores the op_length in 28bits
	//we can safely use a signed int
	//which can store 31 bits
	private final int length;
	
	private final CigarOperation op;

	public CigarElement(CigarOperation op, int length) {
		if(op==null){
			throw new NullPointerException("cigar op can not be null");
		}
		//length shouldn't be 0
		if(length<1){
			throw new IllegalArgumentException("length can < 1");
		}
		this.op = op;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public CigarOperation getOp() {
		return op;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		if (!(obj instanceof CigarElement)) {
			return false;
		}
		CigarElement other = (CigarElement) obj;
		if (length != other.length) {
			return false;
		}
		if (op != other.op) {
			return false;
		}
		return true;
	}
	
	
	
}
