package org.jcvi.common.annotation.hmm;

public final class Transition {

	private final int fromIndex, toIndex;
	private final double probability;
	public Transition(int fromIndex, int toIndex, double probability) {
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.probability = probability;
	}
	/**
	 * @return the fromIndex
	 */
	public int getFromIndex() {
		return fromIndex;
	}
	/**
	 * @return the toIndex
	 */
	public int getToIndex() {
		return toIndex;
	}
	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fromIndex;
		long temp;
		temp = Double.doubleToLongBits(probability);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + toIndex;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Transition)) {
			return false;
		}
		Transition other = (Transition) obj;
		if (fromIndex != other.fromIndex) {
			return false;
		}
		if (Double.doubleToLongBits(probability) != Double
				.doubleToLongBits(other.probability)) {
			return false;
		}
		if (toIndex != other.toIndex) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return String.format("transition %d -> %d (%.2f%%)", 
					fromIndex, toIndex, probability);
	}
	
}
