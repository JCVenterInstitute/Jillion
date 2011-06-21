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
    
    private static final int DEFAULT_CAPACITY = 16;
    
    
    
    public static <K,V> Map<K,V> createLRUCache(){
        return createLRUCache(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createLRUCache(int capacity){
        return createLRUCache(capacity, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createLRUCache(int capacity, float loadFactor){
        return new LRUCache<K, V>(capacity, loadFactor);
    }
    
    public static <K,V> Map<K,V> createSoftReferenceLRUCache(){
        return createSoftReferenceLRUCache(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createSoftReferenceLRUCache(int capacity){
        return createSoftReferenceLRUCache(capacity, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createSoftReferenceLRUCache(int capacity, float loadFactor){
        return new SoftReferenceLRUCache<K, V>(capacity, loadFactor);
    }
    
    
    public static <K,V> Map<K,V> createWeakReferenceLRUCache(){
        return createWeakReferenceLRUCache(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createWeakReferenceLRUCache(int capacity){
        return createWeakReferenceLRUCache(capacity, DEFAULT_LOAD_FACTOR);
    }
    public static <K,V> Map<K,V> createWeakReferenceLRUCache(int capacity, float loadFactor){
        return new WeakReferenceLRUCache<K,V>(capacity, loadFactor);
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

    /* (non-Javadoc)
     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
     */
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
        private final Map<Reference<? extends V>, K> referenceKeyMap = new HashMap<Reference<? extends V>, K>();
        /**
         * @param capacity
         * @param loadFactor
         */
        public AbstractReferencedLRUCache(int capacity, float loadFactor) {
            cache = createLRUCache(capacity, loadFactor);
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
        public int size() {
            removeAnyGarbageCollectedEntries();
            return cache.size();
        }

        @Override
        public boolean isEmpty() {
            removeAnyGarbageCollectedEntries();
            return cache.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            removeAnyGarbageCollectedEntries();
            return cache.containsKey(key);
        }

        @Override
        public V get(Object key) {
            removeAnyGarbageCollectedEntries();
            R softReference= cache.get(key);
            return getReference(softReference);
        }

        @Override
        public V put(K key, V value) {
            removeAnyGarbageCollectedEntries();
            R oldReference= cache.put(key, createReferenceFor(value,referenceQueue));
            return getReference(oldReference);
            
        }


        @Override
        public V remove(Object key) {
            removeAnyGarbageCollectedEntries();
            R oldReference= cache.remove(key);
            return getReference(oldReference);
        }

        private V getReference(R ref){
            if(ref ==null){
                return null;
            }
            return ref.get();
        }

        @Override
        public void clear() {
            removeAnyGarbageCollectedEntries();
            cache.clear();
        }
        @Override
        public Set<K> keySet() {
            removeAnyGarbageCollectedEntries();
            return cache.keySet();
        }

        @Override
        public Collection<V> values() {
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
        public Set<Entry<K, V>> entrySet() {
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
        public SoftReferenceLRUCache(int capacity, float loadFactor) {
          super(capacity,loadFactor);
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
     * {@code SoftReferenceLRUCache} creates an LRUCache which uses
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
        public WeakReferenceLRUCache(int capacity, float loadFactor) {
          super(capacity,loadFactor);
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
