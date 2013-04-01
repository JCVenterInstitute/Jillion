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
/**
 * LRUCache.java
 * Created: May 23, 2008
 *
 * Copyright 2008: J. Craig Venter Institute
 */
package org.jcvi.jillion.internal.core.util;

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

import org.jcvi.jillion.core.util.MapUtil;

/**
 * <code>Caches</code> is a utility class which contains various 
 * implementations caches. Some implementations use a Least-Recently-Used
 * policy (LRU) some implementations use {@link SoftReference}s or {@link WeakReference}s
 * to avoid memory leaks.  
 * 
 * 
 * @author dkatzel
 * @author jsitz@jcvi.org
 * 
 */
public final class Caches
{
    

    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    /**
     * The value of this constant is {@value}.
     */
    public static final int DEFAULT_CAPACITY = 16;
    
    private Caches(){
    	//can not instantiate
    }
    
    /**
     * Creates an LRUCache of default capacity.  Entries are held
     * in the map until capacity is exceeded.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @return a new Map instance with default capacity {@value #DEFAULT_CAPACITY}.
     */
    public static <K,V> Map<K,V> createLRUCache(){
        return createLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache of the given max capacity.  Entries are held
     * in the map until capacity is exceeded.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @param maxcapacity the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with given capacity
     */
    public static <K,V> Map<K,V> createLRUCache(int maxcapacity){
       
        return new LRUCache<K,V>(maxcapacity);
    }
    /**
     * Create a map of strong references with an initial default capacity
     * of {@value #DEFAULT_CAPACITY} which <strong>CAN GROW</strong>  if more than
     * that number of entries is inserted.  This should have
     * the same semantics as {@code new HashMap<K,V>() }.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @return a new Map instance with default capacity that can grow
     * if more entries are added just like a normal Map.
     */
    public static <K,V> Map<K,V> createMap(){
        return createMap(DEFAULT_CAPACITY);
    }
    /**
     * Create a map of strong references with an initial given capacity
     * which <strong>CAN GROW</strong> if more than
     * that number of entries is inserted.  This should have
     * the same semantics as {@code new HashMap<K,V>() }.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @param initialSize the initial size of the map which can later
     * grow in size as more entries are added.
     * @return a new Map instance with the given capacity that can grow
     * if more entries are added just like a normal Map.
     */
    public static <K,V> Map<K,V> createMap(int initialSize){
        return new LinkedHashMap<K,V>(initialSize);
    }
    /**
     * Creates an LRUCache of default capacity where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueLRUCache(){
        return createSoftReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates an Map of default capacity {@value #DEFAULT_CAPACITY}
     *  where the VALUES in the map
     * are each wrapped with a {@link SoftReference}. The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueCache(){
        return createSoftReferencedValueCache(DEFAULT_CAPACITY);
    }
   
    /**
     * Creates an Map using the given capacity where the VALUES in the map
     * are each wrapped with a {@link SoftReference}. The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param initialCapacity the initialCapacity of this cache which is used
     * to calculate when the map should grow and be re-hashed.
     * @return a new Map instance with the given capacity.
     */
    public static <K,V> Map<K,V> createSoftReferencedValueCache(int initialCapacity){
        return new SoftReferenceCache<K, V>(initialCapacity);
    }
    
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
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
     * Creates an LRUCache with max capacity of {@value #DEFAULT_CAPACITY} where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity.
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(){
        return createWeakReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(int maxSize){
        return new WeakReferenceLRUCache<K,V>(maxSize);
    }
    
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> if its the value is only weakly reachable.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueCache(){
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
    public static <K,V> Map<K,V> createWeakReferencedValueCache(int maxSize){
        return new WeakReferenceCache<K,V>(maxSize);
    }
    
    private static <K,V> Map<K,V> createNonLRUMap(int maxSize){
    	return new LinkedHashMap<K, V>(maxSize);
    }
    /**
     * This uses the Java-native implementation of
    * a {@link LinkedHashMap} with last-access ordering and capacity limitation
    * to remove the element which was least recently accessed via the 
    * {@link #get(Object)} method.  This removal only occurs once the capacity
    * is reached.
    * <p>
    * This has the handy effect of creating a simple cache.  The greatest 
    * benefits when using this cache are seen when elements are accessed in
    * clusters, since they will generate large numbers of cache hits followed
    * by steadily dropping out of the cache.
    */
    private static final class LRUCache<K,V> extends LinkedHashMap<K, V>{
 
            private static final long serialVersionUID = -9015747210650112857L;
        private final int maxAllowedSize;
    
        protected LRUCache(int maxAllowedSize, float loadFactor)
        {
            super(MapUtil.computeMinHashMapSizeWithoutRehashing(maxAllowedSize), loadFactor, true);
            this.maxAllowedSize = maxAllowedSize;
        }
    
        protected LRUCache(int maxAllowedSize)
        {
            this(maxAllowedSize, Caches.DEFAULT_LOAD_FACTOR);
        }
    
        
        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest)
        {
            return this.size() > this.maxAllowedSize;
        }
    
    }
    /**
     * {@code AbstractReferencedLRUCache} is an adapter so we can make an LRUCache
     * but the values are not strong Java References.  This will allow
     * the JVM to remove entries from the cache if we need more memory.
     * @author dkatzel
     *
     *
     */
    private abstract static class AbstractReferencedCache<K,V,R extends Reference<V>> extends AbstractMap<K,V>{
        
        
        private final Map<K, R> cache;
        private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<V>();
        private final Map<Reference<? extends V>, K> referenceKeyMap;
        /**
         * Creates a new AbstractReferencedCache instance using the given map
         * @param map the map of {@link Reference}s mapped by a Key.
         * @param initialCapacity the initial size of the references.
         */
        AbstractReferencedCache(Map<K,R> map, int initialCapacity) {
            cache = map;
            int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(initialCapacity);
            referenceKeyMap = new HashMap<Reference<? extends V>, K>(mapSize);
        }
        protected abstract R createReferenceFor(V value,final ReferenceQueue<V> referenceQueue);
        
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
    private static class SoftReferenceCache<K,V> extends AbstractReferencedCache<K,V, SoftReference<V>>{
       
       
        /**
         * Create a new SoftReferenceCache with the given capacity.
         * @param initialCapacity the number of references to store in the map;
         * should be >=1.
         */
        public SoftReferenceCache(int initialCapacity) {
        	super(Caches.<K,SoftReference<V>>createNonLRUMap(initialCapacity), initialCapacity);
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
     * {@link SoftReference}s for the values.
     * @author dkatzel
     * @see SoftReference
     *
     */
    private static class SoftReferenceLRUCache<K,V> extends AbstractReferencedCache<K,V, SoftReference<V>>{
       
    	 /**
         * Create a new SoftReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        public SoftReferenceLRUCache(int maxSize) {
          super(
        		  new LRUCache<K,SoftReference<V>>(
        				  maxSize, 
        				  DEFAULT_LOAD_FACTOR), 
				  maxSize);
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
    private static class WeakReferenceLRUCache<K,V> extends AbstractReferencedCache<K,V, WeakReference<V>>{
        
        
    	 /**
         * Create a new WeakReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        public WeakReferenceLRUCache(int maxSize) {
            super(new LRUCache<K,WeakReference<V>>(maxSize, DEFAULT_LOAD_FACTOR), maxSize);
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected WeakReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new WeakReference<V>(value,referenceQueue);
        }

    }
    
    /**
     * {@code WeakReferenceCache} creates an Cache which uses
     * {@link WeakReference}s for the values.
     * @author dkatzel
     * @see WeakReference
     *
     */
    private static class WeakReferenceCache<K,V> extends AbstractReferencedCache<K,V, WeakReference<V>>{
        
        
    	/**
         * Create a new WeakReferenceCache with the given capacity.
         * @param initialCapacity the number of references to store in the map;
         * should be >=1.
         */
        public WeakReferenceCache(int initialCapacity) {
        	super(Caches.<K,WeakReference<V>>createNonLRUMap(initialCapacity), initialCapacity);
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
