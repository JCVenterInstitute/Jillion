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
package org.jcvi.jillion.profile;


import org.jcvi.jillion.core.residue.nt.Nucleotide;

final class BaseCount implements Comparable<BaseCount>{
	private final Nucleotide base;
	private final double count;
	public BaseCount(Nucleotide base, double count) {
		this.base = base;
		this.count = count;
	}
	Nucleotide getBase() {
		return base;
	}
	
	
	public double getCount() {
		return count;
	}
	@Override
	public int compareTo(BaseCount o) {
		//higher value gets lower compareTo value
		//to get descending order
		return Double.compare(o.count, count);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		long temp;
		temp = Double.doubleToLongBits(count);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!(obj instanceof BaseCount)) {
			return false;
		}
		BaseCount other = (BaseCount) obj;
		if (base != other.base) {
			return false;
		}
		if (Double.doubleToLongBits(count) != Double
				.doubleToLongBits(other.count)) {
			return false;
		}
		return true;
	}
	
	
	
}
