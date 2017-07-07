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
import java.util.List;
import java.util.stream.Collector;

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
    
   
}
