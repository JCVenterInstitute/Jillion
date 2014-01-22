package org.jcvi.jillion.core.util;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;


public class UnsignedByteArray {

	private final byte[] array;
	
	public UnsignedByteArray(byte[] array){
		if(array ==null){
			throw new NullPointerException("array can not be null");
		}
		this.array = array;
	}
	
	/**
	 * The number of elements in the array.
	 * @return an int always >=0.
	 */
	public int getLength(){
		return array.length;
	}
	/**
	 * Get the unsigned byte value
	 * of the given 0-based index.
	 * @param i the 0-based index; must be >=0 and < length
	 * @return the unsigned byte value;
	 * will always be >=0.
	 */
	public int get(int i){
		return IOUtil.toUnsignedByte(array[i]);
	}
	/**
	 * Set the given value to the given
	 * 0-based index in the array.
	 * @param i
	 * @param value the unsigned value to set.
	 */
	public void put(int i, int value){
		array[i] =IOUtil.toSignedByte(value);
	}


	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UnsignedByteArray)) {
			return false;
		}
		UnsignedByteArray other = (UnsignedByteArray) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		return true;
	}
	
	
	
}
