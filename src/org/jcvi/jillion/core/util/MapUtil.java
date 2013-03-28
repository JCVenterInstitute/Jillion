/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

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
	 * Computes the smallest possible 
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
