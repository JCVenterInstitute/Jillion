/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.internal.core.util.BoundedPriorityQueue;

public class JillionCollectors {

    public static <T extends Comparable<? super T>> Collector<T, ?, List<T>> topN(int n){
        return topN(n, Comparator.<T>naturalOrder());
    }
    public static <T> Collector<T, ?, List<T>> topN(int n, Comparator<T> comparator){
        return Collector.of(()-> BoundedPriorityQueue.create(n, comparator),  
                BoundedPriorityQueue::add,
               (left, right) -> { left.addAll(right); return left;},
                q -> new ArrayList<>(q));
    }
    
    /**
     * Create a new Collector that will collect all the Streamed records
     * and turn it into a new DataStore.
     * 
     * @param cls the class of the DataStore implementation to adapt.
     * @param idFunction a Function that can compute the ID for a given record.  This is the ID
     * that will be used to fetch this record from the datastore; can not be null or return null.
     * 
     * @return a new DataStore object.
     * 
     * @throws NullPointerException if either parameter is null. or if the idFunction returns null.
     */
    public static <T, D extends DataStore<T>> Collector<T, ?, D> toDataStore(Class<D> cls, Function<T, String> idFunction){
    	Objects.requireNonNull(cls);
    	Objects.requireNonNull(idFunction);
    	
    	return Collector.of(() -> new LinkedHashMap<String, T>(), 
    			(m, r) -> m.put(Objects.requireNonNull(idFunction.apply(r)), r), 
    			(left, right) -> { left.putAll(right); return left;},
    			m-> DataStoreUtil.adapt(cls, m));
    }
   
}
