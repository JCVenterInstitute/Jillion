package org.jcvi.common.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Ranges;

public final class Gene {

	private final List<Range> exons;
	private final List<Range> introns;
	private final String name;
	public Gene(String name, Range...ranges){
		this(name, Arrays.asList(ranges));
	}
	public Gene(String name, List<Range> exons){
	    if(name ==null){
	        throw new NullPointerException("name can not be null");
	    }
	    if(exons ==null){
            throw new NullPointerException("exons can not be null");
        }
	    if(exons.isEmpty()){
	        throw new IllegalArgumentException("must have at least 1 exon");
	    }
		this.name = name;
		this.exons = new ArrayList<Range>(exons);
		Collections.sort(this.exons);
		
		List<Range> completeExonRange = Arrays.asList(Ranges.createInclusiveRange(this.exons));
		List<Range> introns = completeExonRange;
		for(Range exon : this.exons){
			 introns = exon.complimentFrom(introns);
		}
		this.introns = introns.equals(completeExonRange)? 
				Collections.<Range>emptyList() 
				: introns;
	}
	/**
	 * @return the exons
	 */
	public List<Range> getExons() {
		return exons;
	}
	/**
	 * @return the introns
	 */
	public List<Range> getIntrons() {
		return introns;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exons == null) ? 0 : exons.hashCode());
		result = prime * result + ((introns == null) ? 0 : introns.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Gene)) {
			return false;
		}
		Gene other = (Gene) obj;
		if (exons == null) {
			if (other.exons != null) {
				return false;
			}
		} else if (!exons.equals(other.exons)) {
			return false;
		}
		if (introns == null) {
			if (other.introns != null) {
				return false;
			}
		} else if (!introns.equals(other.introns)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gene [exons=" + exons + ", introns=" + introns + ", name="
				+ name + "]";
	}
	
	
	
}
