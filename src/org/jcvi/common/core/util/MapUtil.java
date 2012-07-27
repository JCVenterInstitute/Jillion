package org.jcvi.common.core.util;

import java.util.HashMap;
import java.util.Map;
/**
 * Utility class for working with {@link Map}s.
 * @author dkatzel
 *
 */
public final class MapUtil {
	
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private MapUtil(){
		//can not instantiate
	}
	/**
	 * Computes the smallest possible initial
	 * size of a {@link HashMap} using the default
	 * load factor that will not cause the map to 
	 * perform an expensive rehashing operation (which also
	 * doubles the number of buckets used).
	 * @param expectedNumberOfEntries the expected number
	 * of entries that will be put into the map.
	 * This type is long to help clients avoid
	 * casting but the max value allowed is {@link Integer#MAX_VALUE}.
	 * Passing in a value greater than {@link Integer#MAX_VALUE} will
	 * throw an Exception.  (This is better than having
	 * clients downcast and possibly truncate the value without any error).
	 * @return the initial size value to pass to the HashMap constructor.
	 * @throws IllegalArgumentException if expectedNumberOfEntries is <0 or
	 * > {@link Integer#MAX_VALUE}.
	 */
	public static int computeMinHashMapSizeWithoutRehashing(long expectedNumberOfEntries){
		if(expectedNumberOfEntries <0){
			throw new IllegalArgumentException("number of entries must be >=0");
		}
		if(expectedNumberOfEntries > Integer.MAX_VALUE){
			throw new IllegalArgumentException("number of entries must be <= Integer.MAX_VALUE");
		}
		return (int)(expectedNumberOfEntries/DEFAULT_LOAD_FACTOR +1);
	}
}
