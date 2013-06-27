/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * {@code JidFactory} is a Factory class
 * that will create {@link Jid}s.
 * @author dkatzel
 *
 */
public final class JidFactory {
	/**
	 * Use US ACII to force each
	 * character to be 1 byte,
	 * anything else that doesn't fit
	 * will throw an exception which we catch.
	 * UTF-8 was not chosen
	 * because it allows variable length characters.
	 */
	private static Charset US_ACII = Charset.forName("US-ASCII");
	
	private JidFactory(){
		//can not instantiate
	}
	/*
	 *Implementation details:
	 *
	 *The code used to pack String into 
	 *int fields was based on a web article I read
	 * from  the Java Performance Tuning Guide:
	 * String packing : conversion to more compact data types.
	 * 
	 * http://java-performance.info/string-packing-converting-characters-to-bytes/
	 * 
	 * We can use this because most of the time genomic ids 
	 * will be short ACII Strings less than 50 characters
	 * so we don't need to store each character is chars; 
	 * also we can save more space by using fields instead of arrays
	 * since the JVM stores extra information about an array
	 * such as its length.
	 */
	
	/**
	 * Create a new {@link Jid} for the given String.
	 * @param id the String representation of the Id.
	 * @return a new {@link Jid} instance; will never be null.
	 * @throw NullPointerException if id is null.
	 */
	public static Jid create(final String id){
		//we optimize our byte arrays to assume no '\0' are
		//contained in the Strings.
		//so short circuit if we have any
		if(id.length() == 0 ||id.length()>56 || id.indexOf( '\0') != -1){
			return new StringJid(id);
		}
		
		final CharsetEncoder enc = US_ACII.newEncoder();
	    final CharBuffer charBuffer = CharBuffer.wrap(id);
	    try {
	        final ByteBuffer byteBuffer = enc.encode(charBuffer);
	        final byte[] byteArray = byteBuffer.array();
	        //use buffer limit() instead of array length because
	        //array might contain extra padding
	        int length = byteBuffer.limit();
			if(length <= 16 ){
	            return new Packed16(byteArray);
	        }else if(length <=24){
	        	return new Packed24(byteArray);
	        }else if(length <=32){
	        	return new Packed32(byteArray);
	        }else if(length <=40){
	        	return new Packed40(byteArray);
	        }else if(length <=48){
	        	return new Packed48(byteArray);
	        }else{
	        	return new Packed56(byteArray);
	        }
	    } catch (CharacterCodingException e) {
	        //there are some chars not fitting to our encoding
	        return new StringJid(id);
	    }
	}
	/**
	 * This Jid implementation just wraps a String.
	 * @author dkatzel
	 *
	 */
	//made package private for testing
	static final class StringJid implements Jid{
		private final String s;

		public StringJid(String s) {
			this.s = s;
		}

		@Override
		public int hashCode() {
			return s.hashCode();

		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Jid)) {
				return false;
			}
			Jid other = (Jid) obj;
			return s.equals(other.toString());
		}

		@Override
		public String toString() {
			return s;
		}
		
		
		
	}
	/**
	 * {@code AbstractPackedStringJId} is a {@link Jid}
	 * implementation that will compactly pack
	 * String data into as few bytes as possible.
	 * Since the JVM pads to 8 byte boundaries,
	 * there will be several subclasses whose sum field
	 * sizes will be multiples of 8 to minimize the number
	 * of bytes each instance takes up in memory. 
	 * @author dkatzel
	 *
	 */
	private abstract static class AbstractPackedStringJId implements Jid{
		/**
		 * Lazy loaded hashcode value.
		 * We are saving so many bytes
		 * by packing our Strings
		 * we can afford to waste 4 bytes
		 * here to make hashing faster.
		 * This hash field was taken
		 * into account when determining the
		 * number of fields each subclass can 
		 * have without causing the JVM
		 * to waste more bytes by padding.
		 * Storing the hashcode result assumes
		 * that Jids will often be used
		 * in HashMaps as keys.
		 */
		private int hash;
		
	    protected int get( final byte[] ar, final int index ){
	        return index < ar.length ? ar[ index ] : 0;
	    }
	 
	    protected abstract ByteBuffer toByteBuffer();
	 
	    protected String toString( final ByteBuffer bbuf ){
	        final byte[] ar = bbuf.array();
	        //skip zero bytes at the tail of the string
	        int last = ar.length - 1;
	        while ( last > 0 && ar[ last ] == 0 ){
	            --last;
	        }
	        
	        return new String( ar, 0, last+1, US_ACII );
	    }
	 
	    public String toString(){
	        return toString( toByteBuffer() );
	    }
	    
	    public final int hashCode(){
	    	//length can't be 0 
	    	//so a hash of 0 probably means
	    	//it hasn't been computed yet.
	    	//If for some reason the hashcode
	    	//is really 0 then we will 
	    	//re-compute hashcode every time
	    	//I am willing to take that
	    	//performance penalty
	    	//every 2^-31 times on average.
	    	
	    	//we don't synchronize because
	    	//it is too expensive most of the time.
	    	//worst case we re-compute hash code
	    	//a few times in concurrent threads
	    	//all calling hashcode at the same time.
	    	if(hash==0){
	    		hash =computeHashCode();
	    	}
	    	return hash;
	    }
	    /**
	     * Compute the hash code of this 
	     * Jid as defined by {@link Jid#hashCode()}
	     * contract.
	     * @return an int.
	     */
	    protected abstract int computeHashCode();
	    
	    protected int computeHashcodeFor(int f){
	    	return computeHashcodeFor(f,0);
	    }
	    /**
	     * Compute the hashcode for the given
	     * int field (which will store up to
	     * 4 characters each as one byte.
	     * This method will be called once for
	     * each int field in order.
	     * In order to match {@link String#hashCode()}
	     * we also have to take into account the previous
	     * computed hashcode value which was summed
	     * over all preceding int fields.
	     * @param f the int field value to compute the hashcode of.
	     * @param currentHashCode the computed hashcode computed
	     * so far.
	     * @return an int.
	     */
	    protected int computeHashcodeFor(int f, int currentHashCode){
	    	int temp = currentHashCode;
	    	//need to check to see if we 
	    	//have reached the end of the String
	    	//known because our tX value is 0
	    	//which can't happen because
	    	//we don't allow any String with nulls (\0)
	        int t1 = f  & 0xFF000000;
	        if(t1==0){
	        	return temp;
	        }
			temp = 31*(temp)+(t1  >>24);
	        int t2 = f  & 0xFF0000;
	        if(t2==0){
	        	return temp;
	        }
			temp = 31*(temp)+(t2  >>16);
	        int t3 = f  & 0xFF00;
	        if(t3==0){
	        	return temp;
	        }
			temp = 31*(temp)+(t3  >>8);
	    	int t4 = f & 0xFF;
	    	if(t4==0){
	        	return temp;
	        }
			temp = 31*(temp)+t4;
	    	
	    	
	    	
	    	return temp;
	    }
	}
	/**
	 * This class will pack a String that is less than 17 characters
	 * into 4 int fields.  This will only take up 32 bytes instead of
	 * 72 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed16 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	 
	    public Packed16( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(16);
	        bbuf.putInt( f1 );
	        bbuf.putInt( f2 );
	        bbuf.putInt( f3 );
	        bbuf.putInt( f4 );
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
		        Packed16 packed12 = (Packed16) o;
		        return f1 == packed12.f1 && f2 == packed12.f2
		        		&& f3 == packed12.f3 && f4 == packed12.f4;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    protected int computeHashCode() {	    	
	        int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        return result;
	    }
	    
	    
	}
	
	/**
	 * This class will pack a String that is less than 25 characters
	 * into 6 int fields.  This will only take up 40 bytes instead of
	 * 104 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed24 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	 
	    public Packed24( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(24);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
		        Packed24 packed20 = (Packed24) o;
		        return f1 == packed20.f1 && f2 == packed20.f2 && f3 == packed20.f3
		        		&& f4 == packed20.f4 && f5 == packed20.f5 && f6 == packed20.f6;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int computeHashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        return result;
	    }
	}
	
	/**
	 * This class will pack a String that is less than 33 characters
	 * into 8 int fields.  This will only take up 48 bytes instead of
	 * 120 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed32 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	    private final int f7;
	    private final int f8;
	 
	    public Packed32( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        f7 = get( ar, 27 ) | get( ar, 26 ) << 8 | get( ar, 25 ) << 16 | get( ar, 24 ) << 24;
	        f8 = get( ar, 31 ) | get( ar, 30 ) << 8 | get( ar, 29 ) << 16 | get( ar, 28 ) << 24;
		       
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(32);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
	        bbuf.putInt(f8);
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
		        Packed32 packed28 = (Packed32) o;
		        return f1 == packed28.f1 && f2 == packed28.f2 && f3 == packed28.f3
		        		&& f4 == packed28.f4 && f5 == packed28.f5 && f6 == packed28.f6 
		        		&& f7 == packed28.f7 && f8 == packed28.f8;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int computeHashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        result = computeHashcodeFor(f8,result);
	        return result;
	    }
	}
	/**
	 * This class will pack a String that is less than 41 characters
	 * into 10 int fields.  This will only take up 56 bytes instead of
	 * 136 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed40 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	    private final int f7;
	    private final int f8;
	    private final int f9;
	    private final int f10;
	 
	    public Packed40( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        f7 = get( ar, 27 ) | get( ar, 26 ) << 8 | get( ar, 25 ) << 16 | get( ar, 24 ) << 24;
	        f8 = get( ar, 31 ) | get( ar, 30 ) << 8 | get( ar, 29 ) << 16 | get( ar, 28 ) << 24;
	        f9 = get( ar, 35 ) | get( ar, 34 ) << 8 | get( ar, 33 ) << 16 | get( ar, 32 ) << 24;
	        f10 = get( ar, 39 ) | get( ar, 38 ) << 8 | get( ar, 37 ) << 16 | get( ar, 36 ) << 24;
	        
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(40);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
	        bbuf.putInt(f8);
	        bbuf.putInt(f9);
	        bbuf.putInt(f10);
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
	        	Packed40 packed36 = (Packed40) o;
		        return f1 == packed36.f1 && f2 == packed36.f2 && f3 == packed36.f3
		        		&& f4 == packed36.f4 && f5 == packed36.f5 && f6 == packed36.f6 && f7 == packed36.f7
		        && f8 == packed36.f8 && f9 == packed36.f9 && f10 == packed36.f10;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int computeHashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        result = computeHashcodeFor(f8,result);
	        result = computeHashcodeFor(f9,result);
	        result = computeHashcodeFor(f10,result);
	        return result;
	    }
	}
	/**
	 * This class will pack a String that is less than 49 characters
	 * into 12 int fields.  This will only take up 64 bytes instead of
	 * 152 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed48 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	    private final int f7;
	    private final int f8;
	    private final int f9;
	    private final int f10;
	    private final int f11;
	    private final int f12;
	 
	    public Packed48( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        f7 = get( ar, 27 ) | get( ar, 26 ) << 8 | get( ar, 25 ) << 16 | get( ar, 24 ) << 24;
	        f8 = get( ar, 31 ) | get( ar, 30 ) << 8 | get( ar, 29 ) << 16 | get( ar, 28 ) << 24;
	        f9 = get( ar, 35 ) | get( ar, 34 ) << 8 | get( ar, 33 ) << 16 | get( ar, 32 ) << 24;
	        f10 = get( ar, 39 ) | get( ar, 38 ) << 8 | get( ar, 37 ) << 16 | get( ar, 36 ) << 24;
	        f11 = get( ar, 43 ) | get( ar, 42 ) << 8 | get( ar, 41 ) << 16 | get( ar, 40 ) << 24;
	        f12 = get( ar, 47 ) | get( ar, 46 ) << 8 | get( ar, 45 ) << 16 | get( ar, 44 ) << 24;
		       
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(48);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
	        bbuf.putInt(f8);
	        bbuf.putInt(f9);
	        bbuf.putInt(f10);
	        bbuf.putInt(f11);
	        bbuf.putInt(f12);
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
	        	Packed48 packed44 = (Packed48) o;
		        return f1 == packed44.f1 && f2 == packed44.f2 && f3 == packed44.f3
		        		&& f4 == packed44.f4 && f5 == packed44.f5 && f6 == packed44.f6 && f7 == packed44.f7
		        && f8 == packed44.f8 && f9 == packed44.f9 && f10 == packed44.f10 
		        && f11 == packed44.f11 && f12 == packed44.f12;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int computeHashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        result = computeHashcodeFor(f8,result);
	        result = computeHashcodeFor(f9,result);
	        result = computeHashcodeFor(f10,result);
	        result = computeHashcodeFor(f11,result);
	        result = computeHashcodeFor(f12,result);
	        return result;
	    }
	}
	
	/**
	 * This class will pack a String that is less than 57 characters
	 * into 14 int fields.  This will only take up 72 bytes instead of
	 * 168 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed56 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	    private final int f7;
	    private final int f8;
	    private final int f9;
	    private final int f10;
	    private final int f11;
	    private final int f12;
	    private final int f13;
	    private final int f14;
	 
	    public Packed56( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        f7 = get( ar, 27 ) | get( ar, 26 ) << 8 | get( ar, 25 ) << 16 | get( ar, 24 ) << 24;
	        f8 = get( ar, 31 ) | get( ar, 30 ) << 8 | get( ar, 29 ) << 16 | get( ar, 28 ) << 24;
	        f9 = get( ar, 35 ) | get( ar, 34 ) << 8 | get( ar, 33 ) << 16 | get( ar, 32 ) << 24;
	        f10 = get( ar, 39 ) | get( ar, 38 ) << 8 | get( ar, 37 ) << 16 | get( ar, 36 ) << 24;
	        f11 = get( ar, 43 ) | get( ar, 42 ) << 8 | get( ar, 41 ) << 16 | get( ar, 40 ) << 24;
	        f12 = get( ar, 47 ) | get( ar, 46 ) << 8 | get( ar, 45 ) << 16 | get( ar, 44 ) << 24;
	        f13 = get( ar, 51 ) | get( ar, 50 ) << 8 | get( ar, 49 ) << 16 | get( ar, 48 ) << 24;
	        f14 = get( ar, 55 ) | get( ar, 54 ) << 8 | get( ar, 53 ) << 16 | get( ar, 52 ) << 24;
	        
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(56);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
	        bbuf.putInt(f8);
	        bbuf.putInt(f9);
	        bbuf.putInt(f10);
	        bbuf.putInt(f11);
	        bbuf.putInt(f12);
	        bbuf.putInt(f13);
	        bbuf.putInt(f14);
	        return bbuf;
	    }
	 
	    @Override
	    public boolean equals(Object o) {
	        if ( this == o ){
	        	return true;
	        }
	        if ( !( o instanceof Jid)){
	        	return false;
	        }
	        if(getClass() == o.getClass()){
	        	Packed56 packed52 = (Packed56) o;
		        return f1 == packed52.f1 && f2 == packed52.f2 && f3 == packed52.f3
		        		&& f4 == packed52.f4 && f5 == packed52.f5 && f6 == packed52.f6 && f7 == packed52.f7
		        && f8 == packed52.f8 && f9 == packed52.f9 && f10 == packed52.f10 && f11 == packed52.f11
		        && f12 == packed52.f12 && f13 == packed52.f13 && f14 == packed52.f14;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int computeHashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        result = computeHashcodeFor(f8,result);
	        result = computeHashcodeFor(f9,result);
	        result = computeHashcodeFor(f10,result);
	        result = computeHashcodeFor(f11,result);
	        result = computeHashcodeFor(f12,result);
	        result = computeHashcodeFor(f13,result);
	        result = computeHashcodeFor(f14,result);
	        return result;
	    }
	}
}
