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
	 * UTF-8 allows variable length characters.
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
	 * will be short UTF-8 Strings less than 50 characters
	 * so we don't need to store each character in shorts 
	 * also we can save more space by using fields instead of arrays
	 * since we have to store extra information about an array
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
		if(id.length() == 0 || id.indexOf( '\0') != -1){
			return new StringJid(id);
		}
		
		final CharsetEncoder enc = US_ACII.newEncoder();
		//enc.onMalformedInput(CodingErrorAction.REPORT);
	    final CharBuffer charBuffer = CharBuffer.wrap(id);
	    try {
	        final ByteBuffer byteBuffer = enc.encode(charBuffer);
	        final byte[] byteArray = byteBuffer.array();
	        //use buffer limit() instead of array length because
	        //array might contain extra padding
	        int length = byteBuffer.limit();
			if(length <= 12 ){
	            return new Packed12(byteArray);
	        }else if(length <=20){
	        	return new Packed20(byteArray);
	        }else if(length <=28){
	        	return new Packed28(byteArray);
	        }else if(length <=36){
	        	return new Packed36(byteArray);
	        }else if(length <=44){
	        	return new Packed44(byteArray);
	        }else if(length <=52){
	        	return new Packed52(byteArray);
	        }else{
	            return new StringJid(id);
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
	
	private abstract static class AbstractPackedStringJId implements Jid{
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
	    protected int computeHashcodeFor(int f){
	    	return computeHashcodeFor(f,0);
	    }
	    protected int computeHashcodeFor(int f, int currentHashCode){
	    	int temp = currentHashCode;
	    	//need to check to see if we 
	    	//have reached the end of the String
	    	//known because our tX value is 0
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
	 * This class will pack a String that is less than 13 characters
	 * into 3 int fields.  This will only take up 24 bytes instead of
	 * 72 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed12 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	 
	    public Packed12( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate( 12 );
	        bbuf.putInt( f1 );
	        bbuf.putInt( f2 );
	        bbuf.putInt( f3 );
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
		        Packed12 packed12 = (Packed12) o;
		        return f1 == packed12.f1 && f2 == packed12.f2 && f3 == packed12.f3;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {	    	
	        int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        return result;
	    }
	    
	    
	}
	
	/**
	 * This class will pack a String that is less than 21 characters
	 * into 5 int fields.  This will only take up 32 bytes instead of
	 * 104 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed20 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	 
	    public Packed20( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(20);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
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
		        Packed20 packed20 = (Packed20) o;
		        return f1 == packed20.f1 && f2 == packed20.f2 && f3 == packed20.f3
		        		&& f4 == packed20.f4 && f5 == packed20.f5;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        return result;
	    }
	}
	
	/**
	 * This class will pack a String that is less than 29 characters
	 * into 7 int fields.  This will only take up 40 bytes instead of
	 * 120 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed28 extends AbstractPackedStringJId
	{
	    private final int f1;
	    private final int f2;
	    private final int f3;
	    private final int f4;
	    private final int f5;
	    private final int f6;
	    private final int f7;
	 
	    public Packed28( final byte[] ar ){
	    	//should be the same logic as in java.util.Bits.getInt, because ByteBuffer.putInt use it
	        f1 = get( ar, 3 ) | get( ar, 2 ) << 8 | get( ar, 1 ) << 16 | get( ar, 0 ) << 24;
	        f2 = get( ar, 7 ) | get( ar, 6 ) << 8 | get( ar, 5 ) << 16 | get( ar, 4 ) << 24;
	        f3 = get( ar, 11 ) | get( ar, 10 ) << 8 | get( ar, 9 ) << 16 | get( ar, 8 ) << 24;
	        f4 = get( ar, 15 ) | get( ar, 14 ) << 8 | get( ar, 13 ) << 16 | get( ar, 12 ) << 24;
	        f5 = get( ar, 19 ) | get( ar, 18 ) << 8 | get( ar, 17 ) << 16 | get( ar, 16 ) << 24;
	        f6 = get( ar, 23 ) | get( ar, 22 ) << 8 | get( ar, 21 ) << 16 | get( ar, 20 ) << 24;
	        f7 = get( ar, 27 ) | get( ar, 26 ) << 8 | get( ar, 25 ) << 16 | get( ar, 24 ) << 24;
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(28);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
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
		        Packed28 packed28 = (Packed28) o;
		        return f1 == packed28.f1 && f2 == packed28.f2 && f3 == packed28.f3
		        		&& f4 == packed28.f4 && f5 == packed28.f5 && f6 == packed28.f6 && f7 == packed28.f7;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        return result;
	    }
	}
	/**
	 * This class will pack a String that is less than 37 characters
	 * into 9 int fields.  This will only take up 48 bytes instead of
	 * 136 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed36 extends AbstractPackedStringJId
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
	 
	    public Packed36( final byte[] ar ){
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
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(36);
	        bbuf.putInt(f1);
	        bbuf.putInt(f2);
	        bbuf.putInt(f3);
	        bbuf.putInt(f4);
	        bbuf.putInt(f5);
	        bbuf.putInt(f6);
	        bbuf.putInt(f7);
	        bbuf.putInt(f8);
	        bbuf.putInt(f9);
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
	        	Packed36 packed36 = (Packed36) o;
		        return f1 == packed36.f1 && f2 == packed36.f2 && f3 == packed36.f3
		        		&& f4 == packed36.f4 && f5 == packed36.f5 && f6 == packed36.f6 && f7 == packed36.f7
		        && f8 == packed36.f8 && f9 == packed36.f9;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {
	    	int result = computeHashcodeFor(f1);
	        result = computeHashcodeFor(f2,result);
	        result = computeHashcodeFor(f3,result);
	        result = computeHashcodeFor(f4,result);
	        result = computeHashcodeFor(f5,result);
	        result = computeHashcodeFor(f6,result);
	        result = computeHashcodeFor(f7,result);
	        result = computeHashcodeFor(f8,result);
	        result = computeHashcodeFor(f9,result);
	        return result;
	    }
	}
	/**
	 * This class will pack a String that is less than 45 characters
	 * into 11 int fields.  This will only take up 56 bytes instead of
	 * 152 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed44 extends AbstractPackedStringJId
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
	 
	    public Packed44( final byte[] ar ){
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
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate(44);
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
	        	Packed44 packed44 = (Packed44) o;
		        return f1 == packed44.f1 && f2 == packed44.f2 && f3 == packed44.f3
		        		&& f4 == packed44.f4 && f5 == packed44.f5 && f6 == packed44.f6 && f7 == packed44.f7
		        && f8 == packed44.f8 && f9 == packed44.f9 && f10 == packed44.f10 && f11 == packed44.f11;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {
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
	        return result;
	    }
	}
	
	/**
	 * This class will pack a String that is less than 53 characters
	 * into 13 int fields.  This will only take up 64 bytes instead of
	 * 168 bytes for a normal String.
	 * @author dkatzel
	 *
	 */
	private static class Packed52 extends AbstractPackedStringJId
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
	 
	    public Packed52( final byte[] ar ){
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
	        
	    }
	 
	    protected ByteBuffer toByteBuffer(){
	        final ByteBuffer bbuf = ByteBuffer.allocate( 52 );
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
	        	Packed52 packed52 = (Packed52) o;
		        return f1 == packed52.f1 && f2 == packed52.f2 && f3 == packed52.f3
		        		&& f4 == packed52.f4 && f5 == packed52.f5 && f6 == packed52.f6 && f7 == packed52.f7
		        && f8 == packed52.f8 && f9 == packed52.f9 && f10 == packed52.f10 && f11 == packed52.f11
		        && f12 == packed52.f12 && f13 == packed52.f13;
	        }
	        //not same class probably isn't
	        //equal but should check anyway in case someone else made
	        //an implementation
	        return toString().equals(o.toString());
	    }
	 
	    @Override
	    public int hashCode() {
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
	        return result;
	    }
	}
}
