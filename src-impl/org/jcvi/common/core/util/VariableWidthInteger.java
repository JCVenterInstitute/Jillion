package org.jcvi.common.core.util;
/**
 * {@code VariableWidthInteger} is an object wrapper around a primitive
 * integer value similar to {@link Long}.
 * The difference however is if the value will be packed into as small 
 * a primitive field as possible in order to reduce memory usage.
 * 
 * This can be very helpful, for example of storing file offsets
 * in a Collection.  Clients that use this collection can treat the 
 * returned values as longs but they might be 
 * stored as bytes, shorts or integers
 * if the offsets are small enough.
 * @author dkatzel
 *
 */
public abstract class VariableWidthInteger {

	/**
	 * Create a new instance of a {@link VariableWidthInteger}
	 * which will wrap the given value.
	 * @param value the value to wrap; may
	 * be negative.
	 * @return a VariableWidthInteger instance that
	 * wraps the given value in as few bytes as possible.
	 */
	public static VariableWidthInteger valueOf(long value){
		//TODO: should we do caching to return 
		//already created instances (flyweight)?
		//This is probably going to be used mostly
		//for file offsets. If we wrap
		//several fastq files, each of which have
		//the same number of bases we might get a lot of
		//duplicate instances...
		
		
		if(value>=Byte.MIN_VALUE && value <=Byte.MAX_VALUE){
			return new ByteWidthInteger((byte)value);
		}else if(value>=Short.MIN_VALUE && value <=Short.MAX_VALUE){
			return new ShortWidthInteger((short)value);
		}
		else if(value>=Integer.MIN_VALUE && value <=Integer.MAX_VALUE){
			return new IntWidthInteger((int)value);
		}
		return new LongWidthInteger(value);
	}
	private VariableWidthInteger(){
		//can not instantiate 
		//except by inner-subclasses
	}
	/**
	 * Get the wrapped value as a long.
	 * @return the value; may be negative.
	 */
	public abstract long getValue();
	
	@Override
	public boolean equals(Object obj){
		if(obj ==null){
			return false;
		}
		if(obj instanceof VariableWidthInteger){
			return getValue()==((VariableWidthInteger)obj).getValue();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		long value = getValue();
		return prime * (int) (value ^ (value >>> 32));
	}
	
	private static class ByteWidthInteger extends VariableWidthInteger{
		
		private byte value;

		public ByteWidthInteger(byte value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}
		
	}
	
	private static class ShortWidthInteger extends VariableWidthInteger{
		
		private short value;

		public ShortWidthInteger(short value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}
		
	}
	
	private static class IntWidthInteger extends VariableWidthInteger{
		
		private int value;

		public IntWidthInteger(int value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}
		
	}
	private static class LongWidthInteger extends VariableWidthInteger{
		
		private long value;

		public LongWidthInteger(long value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}	
	}
}
