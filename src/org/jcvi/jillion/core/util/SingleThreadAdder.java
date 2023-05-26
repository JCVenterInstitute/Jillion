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
package org.jcvi.jillion.core.util;

import java.util.Objects;

/**
 * {@code SingleThreadAdder}
 * is an equivalent class to {@link java.util.concurrent.atomic.LongAdder}
 * but is not threadsafe.  This should be more performant than  
 * using LongAdder but should only be used 
 * by a single thread.
 * @author dkatzel
 *
 */
public class SingleThreadAdder implements Comparable<SingleThreadAdder>{

	private long value;
	
	public SingleThreadAdder(){
		value = 0;
	}
	
	public SingleThreadAdder(long initialValue){
		value = initialValue;
	}
	
	/**
     * Adds the given value.
     *
     * @param x the value to add
     */
    public void add(long x) {
    	value +=x;
    }
    /**
     * Equivalent to {@code add(1)}.
     */
	public void increment(){
		value++;
	}
	/**
     * Equivalent to {@code add(-1)}.
     */
	public void decrement(){
		value--;
	}
	
	public long longValue(){
		return value;
	}
	
	public int intValue(){		
		return (int)value;
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public int compareTo(SingleThreadAdder o) {
		return Long.compare(value, o.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleThreadAdder other = (SingleThreadAdder) obj;
		return value == other.value;
	}
	/**
	 * Set to the given value.
	 * @param value the value to set to.
	 * 
	 * @since 6.0
	 */
	public void set(long value) {
		this.value=value;
	}
	
}
