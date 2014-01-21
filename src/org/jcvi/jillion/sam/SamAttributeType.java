package org.jcvi.jillion.sam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.internal.core.util.GrowableByteArray;

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

		private void assertValidValue(char c) {
			//[!-~]
			if(c < 33 || c> 126){
				//not ! thru ~
				throw new IllegalArgumentException("invalid printable char ascii value : " + (int)c);
			}
		}
		@Override
		public String encode(Object o) {
			try{
				return ((Character)o).toString();
			}catch(ClassCastException e){
				throw new IllegalArgumentException("not a character", e);
			}
		}

		@Override
		public Object decode(String value) {
			if(value.length() !=1){
				throw new IllegalArgumentException("length must be 1");
			}
			char firstChar = value.charAt(0);
			assertValidValue(firstChar);
			return firstChar;
		}
		
	},
	SIGNED_INT('i'){

		@Override
		public String encode(Object o) {
			try{
				return ((Integer)o).toString();
			}catch(ClassCastException e){
				throw new IllegalArgumentException("not an int", e);
			}
		}

		@Override
		public Object decode(String value) {
			return Integer.parseInt(value);
		}
		
	},
	FLOAT('f'){

		@Override
		public String encode(Object o) {
			try{
				return ((Float)o).toString();
			}catch(ClassCastException e){
				throw new IllegalArgumentException("not an float", e);
			}
		}

		@Override
		public Object decode(String value) {
			return Float.parseFloat(value);
		}
		
	},
	STRING('Z'){

		@Override
		public String encode(Object o) {
			return o.toString();
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
		public String encode(Object o) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object decode(String value) {
			// TODO Auto-generated method stub
			return null;
		}
		
	},
	//B [cCsSiIf](,[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)+ Integer or numeric array
	SIGNED_BYTE_ARRAY('B','c'){

		@Override
		public String encode(Object o) {
			// TODO Auto-generated method stub
			return null;
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

		
		
	}
	;
	private static Pattern SPLIT_ARRAY_PATTERN = Pattern.compile(",");
	private static Pattern ARRAY_PATTERN = Pattern.compile("[cCsSiIf](,[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)+");
	char value;
	Character optionalArrayType;
	
	SamAttributeType(char c){
		this(c,null);
	}
	SamAttributeType(char c, Character optionalArrayType){
		this.value = c;
		this.optionalArrayType = optionalArrayType;
	}
	
	public abstract String encode(Object o);
	
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
}
