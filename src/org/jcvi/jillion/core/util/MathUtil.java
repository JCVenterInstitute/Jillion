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
/*
 * Created on Aug 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final  class MathUtil {
	private MathUtil(){
		
	}
    public static <N extends Number> N minOf(Collection<N> numbers){
        verifyNotEmpty(numbers);
        Iterator<N> iter = numbers.iterator();
        N currentMin = iter.next();
        while(iter.hasNext()){
            N next = iter.next();
            if(next.longValue() < currentMin.longValue()){
                currentMin = next;
            }
        }

        return currentMin;
    }
	private static <N> void verifyNotEmpty(Collection<N> numbers) {
		if(numbers.isEmpty()){
            throw new IllegalArgumentException("must pass in at least one value");
        }
	}
    public static <N extends Number> N minOf(N...numbers){
        return minOf(Arrays.asList(numbers));
    }
    public static <N extends Number> N maxOf(Collection<N> numbers){
    	verifyNotEmpty(numbers);
        Iterator<N> iter = numbers.iterator();
        N currentMax = iter.next();
        while(iter.hasNext()){
            N next = iter.next();
            if(next.longValue() > currentMax.longValue()){
                currentMax = next;
            }
        }

        return currentMax;
    }
    public static <N extends Number> N maxOf(N...integers){
        return maxOf(Arrays.asList(integers));
    }

    public static<N extends Number> Double averageOf(N... values){
        return averageOf(Arrays.asList(values));
    }
    public static  <N extends Number> Double averageOf(Collection<N> values){
    	verifyNotEmpty(values);
        long sum=0;
        for(N value : values){
            sum += value.longValue();
        }
        return Double.valueOf((double)sum/values.size());


    }
    public static BigInteger sumOf(byte[] values){
        List<Byte> list = new ArrayList<Byte>(values.length);
        for(int i=0; i<values.length; i++){
            list.add(Byte.valueOf(values[i]));
        }
       return sumOf(list);
    }
    public static BigInteger sumOf(int[] values){
       List<Integer> list = new ArrayList<Integer>(values.length);
        for(int i=0; i<values.length; i++){
            list.add(Integer.valueOf(values[i]));
        }
       return sumOf(list);
    }
    public static BigInteger sumOf(long[] values){
        List<Long> list = new ArrayList<Long>(values.length);
        for(int i=0; i<values.length; i++){
            list.add(Long.valueOf(values[i]));
        }
       return sumOf(list);
    }
    public static BigInteger sumOf(short[] values){
        List<Short> list = new ArrayList<Short>(values.length);
        for(int i=0; i<values.length; i++){
            list.add(Short.valueOf(values[i]));
        }
       return sumOf(list);
    }
    public static BigInteger sumOf(Collection<? extends Number> values){
        BigInteger sum=BigInteger.valueOf(0);
        for(Number value : values){
            sum =sum.add(BigInteger.valueOf(value.longValue()));
        }
        return sum;
    }
    public static <N extends Number & Comparable<N>> Long medianOf(N... values){
        return medianOf(Arrays.asList(values));
    }
    public static <N extends Number & Comparable<N>> Long medianOf(Collection<N> values){
        
    	verifyNotEmpty(values);
    	int size =values.size();
        List<N> sorted = new ArrayList<N>(values);
        Collections.sort(sorted);
        if(size %2==0){
            //handle evens
            int middleIndex = size/2;
            //get the middle of the two middle points
            //more complicated expression A+(B-A)/2
            //to avoid issues of overflow or underflow

            N a= sorted.get(middleIndex-1);
            N b = sorted.get(middleIndex);
            return a.longValue()+(b.longValue()-a.longValue())/2;
        }
            //handle odds
            return sorted.get(size/2).longValue();
    }
}
