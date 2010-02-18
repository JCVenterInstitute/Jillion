/*
 * Created on Aug 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathUtil {
    public static Integer minOf(List<Integer> integers){
        if(integers.size() ==0){
            throw new IllegalArgumentException("must pass in at least one value");
        }
        int currentMin = Integer.MAX_VALUE;
        for(Integer integer : integers){
            currentMin = Math.min(currentMin, integer);
        }

        return currentMin;
    }
    public static Integer minOf(Integer...integers){
        return minOf(Arrays.asList(integers));
    }
    public static Integer maxOf(List<Integer> integers){
        if(integers.size() ==0){
            throw new IllegalArgumentException("must pass in at least one value");
        }
        int currentMax = Integer.MIN_VALUE;
        for(Integer integer : integers){
            currentMax = Math.max(currentMax, integer);
        }

        return currentMax;
    }
    public static Integer maxOf(Integer...integers){
        return maxOf(Arrays.asList(integers));
    }

    public static  Double averageOf(Integer... values){
        return averageOf(Arrays.asList(values));
    }
    public static  Double averageOf(List<Integer> values){
        if(values.size() ==0){
            throw new IllegalArgumentException("must pass in at least one value");
        }
        long sum=0;
        for(int i = 0; i<values.size(); i++){
            sum += values.get(i).intValue();
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
    public static BigInteger sumOf(List<? extends Number> values){
        BigInteger sum=BigInteger.valueOf(0);
        for(Number value : values){
            sum =sum.add(BigInteger.valueOf(value.longValue()));
        }
        return sum;
    }
    public static Integer medianOf(Integer... values){
        return medianOf(Arrays.asList(values));
    }
    public static Integer medianOf(List<Integer> values){
        int size =values.size();

        if(size ==0){
            throw new IllegalArgumentException("must pass in at least one value");
        }
        List<Integer> sorted = new ArrayList<Integer>(values);
        Collections.sort(sorted);
        if(size %2==0){
            //handle evens
            int middleIndex = size/2;
            //get the middle of the two middle points
            //more complicated expression A+(B-A)/2
            //to avoid issues of overflow or underflow

            int a= sorted.get(middleIndex-1);
            int b = sorted.get(middleIndex);
            return a+(b-a)/2;
        }
            //handle odds
            return sorted.get(size/2);
    }
}
