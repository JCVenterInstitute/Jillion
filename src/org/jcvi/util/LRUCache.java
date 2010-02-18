/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * LRUCache.java
 * Created: May 23, 2008
 *
 * Copyright 2008: J. Craig Venter Institute
 */
package org.jcvi.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A <code>LRUCache</code> is a simplistic implementation of a 
 * Least-Recently-Used cache.  This uses the Java-native implementation of
 * a {@link LinkedHashMap} with last-access ordering and capacity limitation
 * to remove the element which was least recently accessed via the 
 * {@link #get(Object)} method.  This removal only occurs once the capacity
 * is reached.
 * <p>
 * This has the handy effect of creating a simple cache.  The greatest 
 * benefits when using this cache are seen when elements are accessed in
 * clusters, since they will generate large numbers of cache hits followed
 * by steadily dropping out of the cache.
 * 
 * @param <K> The key type.
 * @param <V> The value Type.
 * 
 * @author jsitz@jcvi.org
 * @author dkatzel@jcvi.org
 */
//TODO Needs Javadoc.
public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    /** The Serial Version UID */
    private static final long serialVersionUID = 6904810723111631250L;

    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    
    private static final int DEFAULT_CAPACITY = 16;
    
    private final int capacity;
    
    public LRUCache()
    {
        this(LRUCache.DEFAULT_CAPACITY, LRUCache.DEFAULT_LOAD_FACTOR);
    }

    public LRUCache(int capacity, float loadFactor)
    {
        super(capacity+1, loadFactor, true);
        this.capacity = capacity;
    }

    public LRUCache(int capacity)
    {
        this(capacity, LRUCache.DEFAULT_LOAD_FACTOR);
    }

    /* (non-Javadoc)
     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest)
    {
        return this.size() > this.capacity;
    }
}
