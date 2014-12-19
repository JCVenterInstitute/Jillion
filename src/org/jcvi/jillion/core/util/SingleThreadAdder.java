package org.jcvi.jillion.core.util;
/**
 * {@code SingleThreadAdder}
 * is an equivalent class to {@link java.util.concurrent.atomic.LongAdder}
 * but is not threadsafe.  This should be more performant than  
 * using LongAdder but should only be used 
 * by a single thread.
 * @author dkatzel
 *
 */
public class SingleThreadAdder {

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
		return "SingleThreadAdder [value=" + value + "]";
	}
	
	
}
