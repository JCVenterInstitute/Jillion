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
