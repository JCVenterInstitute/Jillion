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
package org.jcvi.common.core.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    /** The Serial Version UID */
    private static final long serialVersionUID = 6904810723111631250L;

    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    
    public static final int DEFAULT_CAPACITY = 16;
    
    
    /**
     * Creates an LRUCache of default capacity.  Entries are held
     * in the map until capacity is exceeded
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createLRUCache(){
        return createLRUCache(DEFAULT_CAPACITY);
    }
    public static <K,V> Map<K,V> createLRUCache(int maxSize){
       
        return new LRUCache<K,V>(maxSize);
    }
    
    
    /**
     * Creates an LRUCache of default capacity where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueLRUCache(){
        return createSoftReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
   
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueLRUCache(int maxSize){
        return new SoftReferenceLRUCache<K, V>(maxSize);
    }
    
    /**
     * Creates an LRUCache with default capacity where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  This will
     * allow the map to remove any entry if its value
     * is only weakly reachable.
     * @param <K> the (strongly reference) key type
     * @param <V> the weakly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(){
        return createWeakReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  This will
     * allow the map to remove any entries if its value
     * is only weakly reachable.
     * @param <K> the (strongly reference) key type
     * @param <V> the weakly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(int maxSize){
        return new WeakReferenceLRUCache<K,V>(maxSize);
    }
    
    private final int capacity;
    
    protected LRUCache()
    {
        this(LRUCache.DEFAULT_CAPACITY, LRUCache.DEFAULT_LOAD_FACTOR);
    }

    protected LRUCache(int capacity, float loadFactor)
    {
        super(capacity+1, loadFactor, true);
        this.capacity = capacity;
    }

    protected LRUCache(int capacity)
    {
        this(capacity, LRUCache.DEFAULT_LOAD_FACTOR);
    }

    
    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest)
    {
        return this.size() > this.capacity;
    }
    
    /**
     * {@code AbstractReferencedLRUCache} is an adapter so we can make an LRUCache
     * but the values are not strong Java References.  This will allow
     * the JVM to remove entries from the cache if we need more memory.
     * @author dkatzel
     *
     *
     */
    private abstract static class AbstractReferencedLRUCache<K,V,R extends Reference<V>> extends AbstractMap<K,V>{
        
        protected abstract R createReferenceFor(V value,final ReferenceQueue<V> referenceQueue);
        
        private final Map<K, R> cache;
        private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<V>();
        private final Map<Reference<? extends V>, K> referenceKeyMap;
        /**
         * @param capacity
         * @param loadFactor
         */
        public AbstractReferencedLRUCache(int maxSize) {
            cache = new LRUCache(maxSize+1, DEFAULT_LOAD_FACTOR);
            referenceKeyMap = new HashMap<Reference<? extends V>, K>(maxSize+1, DEFAULT_LOAD_FACTOR);
        }
        /**
         * Remove any entries in the 
         * cache that have had their values
         * garbage collected.  The GC could have collected any of the values
         * we still have keys for so poll our registered references
         * to see what was collected and remove them from our cache.
         */
        private synchronized void removeAnyGarbageCollectedEntries(){
            Reference<? extends V> collectedReference;
            while((collectedReference = referenceQueue.poll()) !=null){
               
                K key =referenceKeyMap.remove(collectedReference);
                cache.remove(key);                
            }
        }

        @Override
        public synchronized int size() {
            removeAnyGarbageCollectedEntries();
            return cache.size();
        }

        @Override
        public synchronized boolean isEmpty() {
            removeAnyGarbageCollectedEntries();
            return cache.isEmpty();
        }

        @Override
        public synchronized boolean containsKey(Object key) {
            removeAnyGarbageCollectedEntries();
            return cache.containsKey(key);
        }

        @Override
        public synchronized V get(Object key) {
            removeAnyGarbageCollectedEntries();
            R softReference= cache.get(key);
            return getReference(softReference);
        }

        @Override
        public synchronized V put(K key, V value) {
            removeAnyGarbageCollectedEntries();
            R newReference = createReferenceFor(value,referenceQueue);
            R oldReference= cache.put(key, newReference);
            referenceKeyMap.put(newReference, key);
            return getReference(oldReference);
            
        }


        @Override
        public synchronized V remove(Object key) {
            removeAnyGarbageCollectedEntries();
            R oldReference= cache.remove(key);
            referenceKeyMap.remove(oldReference);
            return getReference(oldReference);
        }

        private V getReference(R ref){
            if(ref ==null){
                return null;
            }
            return ref.get();
        }

        @Override
        public synchronized void clear() {
            removeAnyGarbageCollectedEntries();
            cache.clear();
            referenceKeyMap.clear();
        }
        @Override
        public synchronized Set<K> keySet() {
            removeAnyGarbageCollectedEntries();
            return cache.keySet();
        }

        @Override
        public synchronized Collection<V> values() {
            removeAnyGarbageCollectedEntries();
            Collection<R> softValues =cache.values();
            List<V> actualValues = new ArrayList<V>(softValues.size());
            for(R softValue : softValues){
                if(softValue !=null){
                    actualValues.add(softValue.get());
                }
            }
            return actualValues;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized Set<Entry<K, V>> entrySet() {
            removeAnyGarbageCollectedEntries();
            Set<Entry<K,V>> result = new LinkedHashSet<Entry<K, V>>();
            for(final Entry<K,R> entry : cache.entrySet()){
                final K key = entry.getKey();
                final V value =entry.getValue().get();
                if(value !=null){
                    //we still have it
                    result.add(new Entry<K, V>() {

                        @Override
                        public K getKey() {
                            return key;
                        }

                        @Override
                        public V getValue() {
                            return value;
                        }

                        @Override
                        public V setValue(V newValue) {
                            entry.setValue(createReferenceFor(newValue,referenceQueue));
                            return value;
                        }
                        
                    });
                }
            }
            return result;
        }
        
    }
    /**
     * {@code SoftReferenceLRUCache} creates an LRUCache which uses
     * {@link SoftReference}s for the values.
     * @author dkatzel
     * @see SoftReference
     *
     */
    private static class SoftReferenceLRUCache<K,V> extends AbstractReferencedLRUCache<K,V, SoftReference<V>>{
       
       
        /**
         * @param capacity
         * @param loadFactor
         */
        public SoftReferenceLRUCache(int maxSize) {
          super(maxSize);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected SoftReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new SoftReference<V>(value, referenceQueue);
        }

    }
    /**
     * {@code WeakReferenceLRUCache} creates an LRUCache which uses
     * {@link WeakReference}s for the values.
     * @author dkatzel
     * @see WeakReference
     *
     */
    private static class WeakReferenceLRUCache<K,V> extends AbstractReferencedLRUCache<K,V, WeakReference<V>>{
        
        
        /**
         * @param capacity
         * @param loadFactor
         */
        public WeakReferenceLRUCache(int maxSize) {
          super(maxSize);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected WeakReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new WeakReference<V>(value,referenceQueue);
        }

    }
}
