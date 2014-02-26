package org.jcvi.jillion.sam.attribute;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.UnsignedByteArray;
import org.jcvi.jillion.core.util.UnsignedIntArray;
import org.jcvi.jillion.core.util.UnsignedShortArray;

public enum SamAttributeType {
/*
	A [!-~] Printable character
	i [-+]?[0-9]+ Singed 32-bit integer
	f [-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)? Single-precision 
	oating number
	Z [ !-~]+ Printable string, including space
	H [0-9A-F]+ Byte array in the Hex format2
	B [cCsSiIf](,[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)+ Integer or numeric array
*/
	PRINTABLE_CHARACTER('A'){
		
		private boolean assertValidValue(char c) {
			//[!-~]
			if(c < 33 || c> 126){
				return false;
			}
			return true;
		}
		
		@Override
		public boolean isPrintableCharacter(){
			return true;
		}
		
		@Override 
		public char getPrintableCharacter(Object obj){
			return ((Character) obj).charValue();
		}
		@Override
		public String textEncode(Object o) {
			try{
				return ((Character)o).toString();
			}catch(ClassCastException e){
				throw new IllegalArgumentException("not a character", e);
			}
		}
		
		@Override
		public void binaryEncode(Object o, ByteBuffer out) throws IOException{
			out.put((byte)o.toString().charAt(0));
		}

		@Override
		public Object decode(String value) {
			if(value.length() !=1){
				throw new IllegalArgumentException("length must be 1");
			}
			char firstChar = value.charAt(0);
			if(!assertValidValue(firstChar)){
				throw new IllegalArgumentException("invalid printable char ascii value : " + (int)firstChar);
			
			};
			return firstChar;
		}

		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof Character)){
				throw new InvalidValueTypeException("not a Character");
			}
			if(!assertValidValue(((Character)value).charValue())){
				throw new InvalidValueTypeException("not a valid printable character");
			}
			
		}
		
	},
	SIGNED_INT('i'){

		@Override
		public boolean isSignedInt() {
			return true;
		}

		@Override
		public int getSignedInt(Object obj) {			
			return Integer.parseInt(obj.toString());
		}

		@Override
		public String textEncode(Object o) {
			//this ugliness to to type check
			//that the object is a integer
			//or a type that can be converted into an integer
			//ex: "1" or Integer.valueOf(1) etc.
			try{
				return Integer.toString(Integer.parseInt(o.toString()));
			}catch(Exception e){
				throw new IllegalArgumentException("not an int", e);
			}
		}
		
		
		@Override
		public void encodeInBam(Object o, ByteBuffer b) throws IOException {
			int i = getSignedInt(o);
			//according to BAM spec
			//we can choose to represent this int
			//in a smaller number of bytes if it fits
			//but we have to change the type accordingly
			if(i >=Byte.MIN_VALUE && i <= Byte.MAX_VALUE){
				b.put((byte)'c');
				b.put((byte)i);
			}else if(i >=0 && i <= 255){
				b.put((byte)'C');
				b.put((byte)i);
			}else if(i >=Short.MIN_VALUE && i <= Short.MAX_VALUE){
				b.put((byte)'s');
				b.putShort((short)i);
			}else if(i >=0 && i <= 65535){
				b.put((byte)'S');
				b.putShort((short)i);
			}else{
				//the spec also mentions unsigned int
				//but I don't think we can actually store
				//that in this attribute type?
				//since SAM only allows up to signed ints.
				b.put((byte)'i');
				b.putInt(i);
			}
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out) throws IOException {
			//no-op since we override encoideInBam()
			
		}

		@Override
		public Object decode(String value) {
			return Integer.parseInt(value);
		}

		@Override
		public void validate(Object value) throws InvalidValueTypeException {			
			if(!(value instanceof Integer)){
				//check that it can be parsed into an int
				try{
					Integer.parseInt(value.toString());
				}catch(NumberFormatException e){
					throw new InvalidValueTypeException("not a valid int : '" + value + "'", e);
				}
			}
			
		}
		
	},
	FLOAT('f'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof Float)){
				throw new InvalidValueTypeException("not a valid float");				
			}
			
		}
		@Override
		public boolean isFloat() {
			return true;
		}

		@Override
		public int getSignedInt(Object obj) {			
			return Integer.parseInt(obj.toString());
		}

		@Override
		public String textEncode(Object o) {
			//this ugliness to to type check
			//that the object is a float
			//or a type that can be converted into an integer
			//ex: "1.23" or Float.valueOf(1.23) etc.
			try{
				return Float.toString(Float.parseFloat(o.toString()));
			}catch(Exception e){
				throw new IllegalArgumentException("not an float", e);
			}
		}
		@Override
		public float getFloat(Object obj) {
			return Float.parseFloat(obj.toString());
		}

		
		

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			out.putFloat( getFloat(o));
			
		}
		@Override
		public Object decode(String value) {
			return Float.parseFloat(value);
		}
		
	},
	STRING('Z'){

		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof String)){
				throw new InvalidValueTypeException("not a valid String");				
			}
			
		}
		@Override
		public boolean isString() {
			return true;
		}

		@Override
		public String getString(Object obj) {
			return obj.toString();
		}

		@Override
		public String textEncode(Object o) {
			return o.toString();
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			out.put(o.toString().getBytes(IOUtil.UTF_8));
			//null terminate
			out.put((byte)0);
		}
		@Override
		public Object decode(String value) {
			if(value ==null){
				throw new NullPointerException("value can not be null");
			}
			return value;
		}
		
	},
	BYTE_ARRAY_IN_HEX('H'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof byte[])){
				throw new InvalidValueTypeException("not a valid byte array");				
			}
			
		}
		@Override
		public boolean isByteArray() {
			return true;
		}

		@Override
		public byte[] getByteArray(Object obj) {
			return (byte[])obj;
		}

		@Override
		public String textEncode(Object o) {
			byte[] array = (byte[])o;
			//2hex digits per element
			StringBuilder builder = new StringBuilder(3+ 2*array.length);
			for(int i=0; i< array.length; i++){				
				builder.append(HEX_ARRAY[IOUtil.toUnsignedByte(array[i])]);
			}
			return builder.toString();
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			//TODO write as a null terminated string?
			out.put(textEncode(o).getBytes(IOUtil.UTF_8));
			out.put((byte)0);
			
		}
		@Override
		public Object decode(String value) {
			if(value.charAt(0) != optionalArrayType.charValue()){
				throw new IllegalArgumentException("must be hex string with in H format" + value);
			}
			byte[] array = new byte[(value.length()-1)/2];
			for(int i=1; i<value.length();i+=2){
				String hex =value.substring(i, i+2);
				array[i] =Byte.parseByte(hex, 16);
			}
			return array;
		}
		
	},
	//B [cCsSiIf](,[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)+ Integer or numeric array
	SIGNED_BYTE_ARRAY('B','c'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof byte[])){
				throw new InvalidValueTypeException("not a valid byte array");				
			}
			
		}
		@Override
		public boolean isByteArray() {
			return true;
		}

		@Override
		public byte[] getByteArray(Object obj) {
			return (byte[])obj;
		}
		@Override
		public String textEncode(Object o) {
			byte[] array = (byte[])o;
			//signed so max length per element is
			//-128, = 4 chars			
			StringBuilder builder = new StringBuilder(3+ 4*array.length);
			for(int i=0; i< array.length; i++){
				builder.append(Byte.toString(array[i])).append(',');
			}
			return builder.toString();
		}
		
		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			byte[] array = (byte[])o;
			out.put((byte)(optionalArrayType & 0xFF));
			out.putInt(array.length);
			out.put(array);
		}

		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			byte[] ret = new byte[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = Byte.parseByte(split[i]);
			}
			return ret;
		}
	},
	UNSIGNED_BYTE_ARRAY('B','C'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof UnsignedByteArray)){
				throw new InvalidValueTypeException("not a valid unsigned byte array");				
			}
			
		}
		@Override
		public boolean isUnsignedByteArray() {
			return true;
		}

		@Override
		public UnsignedByteArray getUnsignedByteArray(Object obj) {
			return (UnsignedByteArray)obj;
		}
		@Override
		public String textEncode(Object o) {
			UnsignedByteArray array = (UnsignedByteArray)o;
			int length = array.getLength();
			//signed so max length per element is
			//-128, = 4 chars			
			StringBuilder builder = new StringBuilder(3+ 4*length);
			for(int i=0; i< length; i++){
				builder.append(Integer.toString(array.get(i))).append(',');
			}
			return builder.toString();
		}
		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			UnsignedByteArray array = (UnsignedByteArray)o;
			out.put((byte)(optionalArrayType & 0xFF));
			int length = array.getLength();
			out.putInt(length);
			for(int i=0; i< length; i++){
				out.put((byte)array.get(i));
			}
			
		}

		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			byte[] ret = new byte[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = IOUtil.toSignedByte(Integer.parseInt(split[i]));
			}
			return new UnsignedByteArray(ret);
		}
	},
	SIGNED_SHORT_ARRAY('B','s'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof short[])){
				throw new InvalidValueTypeException("not a valid short array");				
			}
			
		}
		@Override
		public boolean isShortArray() {
			return true;
		}

		@Override
		public short[] getShortArray(Object obj) {
			return (short[])obj;
		}

		@Override
		public String textEncode(Object o) {
			short[] array = (short[])o;
			//signed so max length per element is
			//-32768, = 7 chars			
			StringBuilder builder = new StringBuilder(3+ 7*array.length);
			for(int i=0; i< array.length; i++){
				builder.append(Short.toString(array[i])).append(',');
			}
			return builder.toString();
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			short[] array = (short[])o;
			out.put((byte)(optionalArrayType & 0xFF));
			out.putInt(array.length);
			out.asShortBuffer().put(array);
		}
		
		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			short[] ret = new short[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = Short.parseShort(split[i]);
			}
			return ret;
		}
	},
	UNSIGNED_SHORT_ARRAY('B','S'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof UnsignedShortArray)){
				throw new InvalidValueTypeException("not a valid short array");				
			}
			
		}
		@Override
		public boolean isUnsignedShortArray() {
			return true;
		}

		@Override
		public UnsignedShortArray getUnsignedShortArray(Object obj) {
			return (UnsignedShortArray)obj;
		}

		@Override
		public String textEncode(Object o) {
			UnsignedShortArray array = (UnsignedShortArray)o;
			int length = array.getLength();
			//signed so max length per element is
			//65535, = 6 chars			
			StringBuilder builder = new StringBuilder(3+ 6*length);
			for(int i=0; i< length; i++){
				builder.append(Integer.toString(array.get(i))).append(',');
			}
			return builder.toString();
		}
		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			UnsignedShortArray array = (UnsignedShortArray)o;
			out.put((byte)(optionalArrayType & 0xFF));
			int length = array.getLength();
			out.putInt(length);
			ShortBuffer sb = out.asShortBuffer();
			for(int i=0; i< length; i++){
				sb.put((short)array.get(i));
			}
			
		}
		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			short[] ret = new short[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = IOUtil.toSignedShort(Integer.parseInt(split[i]));
			}
			return new UnsignedShortArray(ret);
		}
	},
	SIGNED_INT_ARRAY('B','i'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof int[])){
				throw new InvalidValueTypeException("not a valid int array");				
			}
			
		}
		@Override
		public boolean isIntArray() {
			return true;
		}

		@Override
		public int[] getIntArray(Object obj) {
			return (int[])obj;
		}

		@Override
		public String textEncode(Object o) {
			int[] array = (int[])o;
			//signed so max length per element is
			//-2147483648, = 12 chars			
			StringBuilder builder = new StringBuilder(3+ 12*array.length);
			for(int i=0; i< array.length; i++){
				builder.append(Integer.toString(array[i])).append(',');
			}
			return builder.toString();
		}
		
		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			int[] array = (int[])o;
			out.put((byte)(optionalArrayType & 0xFF));
			out.putInt(array.length);
			out.asIntBuffer().put(array);
		}

		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			int[] ret = new int[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = Integer.parseInt(split[i]);
			}
			return ret;
		}
	},
	UNSIGNED_INT_ARRAY('B','I'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof UnsignedIntArray)){
				throw new InvalidValueTypeException("not a valid unsignedInt array");				
			}
			
		}
		@Override
		public boolean isUnsignedIntArray() {
			return true;
		}

		@Override
		public UnsignedIntArray getUnsignedIntArray(Object obj) {
			return (UnsignedIntArray) obj;
		}

		@Override
		public String textEncode(Object o) {
			UnsignedIntArray array = (UnsignedIntArray)o;
			int length = array.getLength();
			//signed so max length per element is
			//2147483647, = 11 chars			
			StringBuilder builder = new StringBuilder(3+ 11*length);
			for(int i=0; i< length; i++){
				builder.append(Long.toString(array.get(i))).append(',');
			}
			return builder.toString();
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			UnsignedIntArray array = (UnsignedIntArray)o;
			out.put((byte)(optionalArrayType & 0xFF));
			int length = array.getLength();
			out.putInt(length);
			IntBuffer ib = out.asIntBuffer();
			for(int i=0; i< length; i++){
				ib.put((int)array.get(i));
			}
			
		}
		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			int[] ret = new int[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = IOUtil.toSignedInt(Long.parseLong(split[i]));
			}
			return new UnsignedIntArray(ret);
		}
	},
	FLOAT_ARRAY('B','f'){
		@Override
		public void validate(Object value) throws InvalidValueTypeException {
			if(!(value instanceof float[])){
				throw new InvalidValueTypeException("not a valid float array");				
			}
			
		}
		@Override
		public boolean isFloatArray() {
			return true;
		}

		@Override
		public float[] getFloatArray(Object obj) {
			return (float[])obj;
		}

		@Override
		public String textEncode(Object o) {
			float[] array = (float[])o;
			//floats should store around 7 decimal digits
			//plus the neg sign for a total of 8
			//if we don't guess correctly, the builder will expand for us anyway
			StringBuilder builder = new StringBuilder(3+ 8*array.length);
			for(int i=0; i< array.length; i++){
				builder.append(Float.toString(array[i])).append(',');
			}
			return builder.toString();
		}

		@Override
		public void binaryEncode(Object o, ByteBuffer out)
				throws IOException {
			float[] array = (float[])o;
			out.put((byte)(optionalArrayType & 0xFF));
			out.putInt(array.length);
			out.asFloatBuffer().put(array);
		}
		@Override
		public Object decode(String value) {
			String[] split = parseArrayElementsFrom(value);
			float[] ret = new float[split.length];
			for(int i=0; i< split.length; i++){
				ret[i] = Float.parseFloat(split[i]);
			}
			return ret;
		}
	},
	;
	private static final String[] HEX_ARRAY = new String[255];
	private static final Pattern SPLIT_ARRAY_PATTERN = Pattern.compile(",");
	private static final Pattern ARRAY_PATTERN = Pattern.compile("[cCsSiIf](,[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)+");
	
	static{
		for(int i=0; i<HEX_ARRAY.length; i++){
			//use X to make it uppercase
			HEX_ARRAY[i]= String.format("%02X",i);
		}
		
	}
	
	char value;
	Character optionalArrayType;
	
	SamAttributeType(char c){
		this(c,null);
	}
	SamAttributeType(char c, Character optionalArrayType){
		this.value = c;
		this.optionalArrayType = optionalArrayType;
	}
	public String getTextTypeCode(){
		StringBuilder builder = new StringBuilder();
		builder.append(value).append(':');
		if(optionalArrayType !=null){
			builder.append(optionalArrayType);
		}
		return builder.toString();
	}
	
	private void putBinaryTypeCode(ByteBuffer buf){
		buf.put((byte)(value & 0xFF));
		if(optionalArrayType !=null){
			buf.put((byte)(optionalArrayType & 0xFF));
		}
	}
	public abstract String textEncode(Object o);
	
	public void encodeInBam(Object o, ByteBuffer b) throws IOException{
		putBinaryTypeCode(b);
		binaryEncode(o, b);
	}
	/**
	 * Encode the type AND the value in BAM format
	 * and write those bytes to the given ByteBuffer.
	 * @param o
	 * @param out
	 * @throws IOException
	 */
	abstract void binaryEncode(Object o, ByteBuffer out) throws IOException;
	
	public abstract Object decode(String value);
	
	protected String[] parseArrayElementsFrom(String value) {
		Matcher matcher =ARRAY_PATTERN.matcher(value.trim());
		if(!matcher.matches()){
			throw new IllegalArgumentException("invalid encoded array " + value);
		}
		char code = matcher.group(1).charAt(0);
		if(!optionalArrayType.equals(code)){
			throw new IllegalArgumentException(
					String.format("incorrect array type: expected :%s but was %s: %s",
							optionalArrayType, code, value));
		}
		String[] split = SPLIT_ARRAY_PATTERN.split(matcher.group(2));
		return split;
	}
	
	public boolean isPrintableCharacter(){
		return false;
	}
	public char getPrintableCharacter(Object obj){
		throw new UnsupportedOperationException();
	}
	
	public boolean isSignedInt(){
		return false;
	}
	public int getSignedInt(Object obj){
		throw new UnsupportedOperationException();
	}
	
	public boolean isFloat(){
		return false;
	}
	public float getFloat(Object obj){
		throw new UnsupportedOperationException();
	}
	
	public boolean isString(){
		return false;
	}
	public String getString(Object obj){
		throw new UnsupportedOperationException();
	}
	
	public boolean isByteArray(){
		return false;
	}
	public byte[] getByteArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public boolean isShortArray(){
		return false;
	}
	public short[] getShortArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public boolean isIntArray(){
		return false;
	}
	public int[] getIntArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public boolean isUnsignedByteArray(){
		return false;
	}
	public UnsignedByteArray getUnsignedByteArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public boolean isUnsignedShortArray(){
		return false;
	}
	public UnsignedShortArray getUnsignedShortArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public boolean isUnsignedIntArray(){
		return false;
	}
	public UnsignedIntArray getUnsignedIntArray(Object obj){
		throw new UnsupportedOperationException();
	}
	
	public boolean isFloatArray(){
		return false;
	}
	public float[] getFloatArray(Object obj){
		throw new UnsupportedOperationException();
	}
	public abstract void validate(Object value2) throws InvalidValueTypeException;


	public static SamAttributeType parseType(char typeCode, String value){
		if(typeCode =='B'){
			//handle arrays separately
			char arrayType = value.charAt(0);
			//cCsSiIf
			switch(arrayType){
				case 'C': return UNSIGNED_BYTE_ARRAY;
				case 'D': return null;
				case 'E': return null;
				case 'F': return null;
				case 'G': return null;
				case 'H': return null;
				case 'I': return UNSIGNED_INT_ARRAY;
				case 'J': return null;
				case 'K': return null;
				case 'L': return null;
				case 'M': return null;
				case 'N': return null;
				case 'O': return null;
				case 'P': return null;
				case 'Q': return null;
				case 'R': return null;
				case 'S': return UNSIGNED_SHORT_ARRAY;
				case 'T': return null;
				case 'U': return null;
				case 'V': return null;
				case 'W': return null;
				case 'X': return null;
				case 'Y': return null;
				case 'Z': return null;
				
				case '[': return null;
				case '\\': return null;
				case ']': return null;
				case '^': return null;
				case '_': return null;
				case '`': return null;
				
				case 'a': return null;
				case 'b': return null;
				case 'c': return SIGNED_BYTE_ARRAY;
				case 'd': return null;
				case 'e': return null;
				case 'f': return FLOAT_ARRAY;
				case 'g': return null;
				case 'h': return null;
				case 'i': return SIGNED_INT_ARRAY;
				case 'j': return null;
				case 'k': return null;
				case 'l': return null;
				case 'm': return null;
				case 'n': return null;
				case 'o': return null;
				case 'p': return null;
				case 'q': return null;
				case 'r': return null;			
				case 's': return SIGNED_SHORT_ARRAY;
				
				default: return null;
			
			}
		}
		//there are so few types and some are so uncommon
		//that we can just take the performance hit
		//once in a while and compare each
		
		//ordered in most common to least common order
		//to do as few comparisons as possible
		
		if('Z' == typeCode){
			return STRING;
		}
		//BAM can use smaller number of bytes
		//so need to check for all sizes of ints
		if('i' == typeCode || 'I' == typeCode
				|| 'c' == typeCode || 'C' == typeCode
				|| 's' == typeCode || 'S' == typeCode){
			return SIGNED_INT;
		}
		if('f' == typeCode){
			return FLOAT;
		}
		if('A' == typeCode){
			return PRINTABLE_CHARACTER;
		}
		if('H' == typeCode){			
			return BYTE_ARRAY_IN_HEX;
		}
		return null;
		
	}
}
