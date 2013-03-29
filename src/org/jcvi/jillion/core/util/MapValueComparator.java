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

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.jillion.internal.core.util.ComparableComparator;

/**
 * {@code MapValueComparator} is a {@link Comparator}
 * that allows sorting a Map's keys based on the values mapped
 * to the keys.
 * @author dkatzel
 *
 *
 */
public final class MapValueComparator<K extends Comparable<? super K>,V> implements Comparator<K> {

	private final Map<K, V> map;
	private final boolean ascending;
	private final Comparator<V> valueComparator;
    
    /**
     * Create an unmodifiable {@link SortedMap} that is sorted by value in ascending 
     * order.  If there are duplicate values, then their sort order will be determined
     * by comparing the keys to each other also in ascending order.
     * @param <K> The Comparable type of Key contained in the map.
     * @param <V> The type of the values contained in the map (does not have to be comparable).
     * @param unsorted the unsorted Map to be sorted.
     * @param comparator the {@link Comparator} used to sort the values in this map.
     * @return a sorted map sorted by the values in ascending order; never null.
     */
    public static <K extends Comparable<? super K>,V> SortedMap<K,V> sortAscending(Map<K, V> unsorted, Comparator<V> comparator){
        TreeMap<K,V> sorted= new TreeMap<K,V>(MapValueComparator.create(unsorted,comparator,true));
        sorted.putAll(unsorted);
        return sorted;
    }
    /**
     * Create an unmodifiable {@link SortedMap} that is sorted by value in descending 
     * order.  If there are duplicate values, then their sort order will be determined
     * by comparing the keys to each other also in ascending order.
     * @param <K> The Comparable type of Key contained in the map.
     * @param <V> The type of the values contained in the map (does not have to be comparable).
     * @param unsorted the unsorted Map to be sorted.
     * @param comparator the {@link Comparator} used to sort the values in this map.
     * @return a sorted map sorted by the values in ascending order; never null.
     */
    public static <K extends Comparable<? super K>,V> SortedMap<K,V> sortDescending(Map<K, V> unsorted, Comparator<V> comparator){
        TreeMap<K,V> sorted= new TreeMap<K,V>(MapValueComparator.create(unsorted,comparator,false));
        sorted.putAll(unsorted);
        return sorted;
    }
    /**
     * Create an unmodifiable {@link SortedMap} that is sorted by value in ascending 
     * order.  If there are duplicate values, then their sort order will be determined
     * by comparing the keys to each other also in ascending order.
     * @param <K> The Comparable type of Key contained in the map.
     * @param <V> The Comparable type of the values contained in the map.
     * @param unsorted the unsorted Map to be sorted.
     * @return a sorted map sorted by the values in ascending order; never null.
     */
    public static <K extends Comparable<? super K>,V extends Comparable<? super V>> SortedMap<K,V> sortAscending(Map<K, V> unsorted){
    	if(unsorted==null){
    		throw new NullPointerException("map can not be null");
    	}
    	TreeMap<K,V> sorted= new TreeMap<K,V>(MapValueComparator.create(unsorted,ComparableComparator.<V>create(),true));
        sorted.putAll(unsorted);
        return sorted;
    }
    /**
     * Create an unmodifiable {@link SortedMap} that is sorted by value in descending 
     * order.  If there are duplicate values, then their sort order will be determined
     * by comparing the keys to each other also in descending order.
     * @param <K> The Comparable type of Key contained in the map.
     * @param <V> The Comparable type of the values contained in the map.
     * @param unsorted the unsorted Map to be sorted.
     * @return a sorted map sorted by the values in descending order; never null.
     */
    public static <K extends Comparable<? super K>,V extends Comparable<? super V>> SortedMap<K,V> sortDescending(Map<K, V> unsorted){
    	if(unsorted==null){
    		throw new NullPointerException("map can not be null");
    	}
        TreeMap<K,V> sorted= new TreeMap<K,V>(MapValueComparator.create(unsorted,ComparableComparator.<V>create(),false));
        sorted.putAll(unsorted);
        return sorted;
    }
    /**
     * Helper static method to infer types of keys so we 
     * don't clutter the instantiation code with generic noise.
     * @param <K> The Comparable type of Key contained in the map.
     * @param <V> The Comparable type of the values contained in the map.
     * @param mao the Map to be get associations of key and values.
     * @param ascending should the sort be ordered in ascending order.
     * @return a new MapValueComparator instance.
     */
    private static <K extends Comparable<? super K>,V> MapValueComparator<K,V> create(Map<K, V> map, Comparator<V> comparator,boolean ascending){
        return new MapValueComparator<K, V>(map,comparator,ascending);
    }
   
    private MapValueComparator(Map<K, V> map,Comparator<V> valueComparator,boolean ascending) {
        if(map ==null){
            throw new NullPointerException("map can not be null");
        }
        this.map = map;
        this.valueComparator = valueComparator;
        this.ascending = ascending;
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public int compare(K o1, K o2) {
        if(ascending){
            return privateCompare(o1, o2);
        }
        //invert if descending
        return privateCompare(o2, o1);
    }

    private int privateCompare(K o1, K o2) {
        if(!map.containsKey(o1)){
            if(!map.containsKey(o2)){
                return 0;
            }
             return -1;
        }
        if(!map.containsKey(o2)){
            return 1;
        }
        int comp= valueComparator.compare(map.get(o1),map.get(o2));
        if(comp !=0){
            return comp;
        }
        return o1.compareTo(o2);
    }
    
}
