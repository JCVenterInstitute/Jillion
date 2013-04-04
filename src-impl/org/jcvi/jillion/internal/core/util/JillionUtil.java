package org.jcvi.jillion.internal.core.util;
/**
 * {@code JillionUtil} is a utility class
 * that has helper methods that don't fit anywhere else.
 * 
 * @author dkatzel
 *
 */
public final class JillionUtil {

	private JillionUtil(){
		//can not instantiate
	}
	
	
	/**
     * The same as {@link Long#compareTo(Long)} but does 
     * not require boxing so it should be faster.
     * @param value1
     * @param value2
     * @return {@code 0} if both values are equal,
     * a negative  number if value1 is less than value 2
     * and a positive number if value1 is greater than value2. 
     */
	public static int compare(long value1, long value2){
		if(value1==value2){
    		return 0;
    	}
    	if(value1 < value2){
    		return -1;
    	}
    	return 1;
	}
	/**
     * The same as {@link Integer#compareTo(Integer)} but does 
     * not require boxing so it should be faster.
     * @param value1
     * @param value2
     * @return {@code 0} if both values are equal,
     * a negative  number if value1 is less than value 2
     * and a positive number if value1 is greater than value2. 
     */
	public static int compare(int value1, int value2){
		if(value1==value2){
    		return 0;
    	}
    	if(value1 < value2){
    		return -1;
    	}
    	return 1;
	}
	
	/**
     * The same as {@link Short#compareTo(Short)} but does 
     * not require boxing so it should be faster.
     * @param value1
     * @param value2
     * @return {@code 0} if both values are equal,
     * a negative  number if value1 is less than value 2
     * and a positive number if value1 is greater than value2. 
     */
	public static int compare(short value1, short value2){
		if(value1==value2){
    		return 0;
    	}
    	if(value1 < value2){
    		return -1;
    	}
    	return 1;
	}
	
	/**
     * The same as {@link Byte#compareTo(Byte)} but does 
     * not require boxing so it should be faster.
     * @param value1
     * @param value2
     * @return {@code 0} if both values are equal,
     * a negative  number if value1 is less than value 2
     * and a positive number if value1 is greater than value2. 
     */
	public static int compare(byte value1, byte value2){
		if(value1==value2){
    		return 0;
    	}
    	if(value1 < value2){
    		return -1;
    	}
    	return 1;
	}
}
