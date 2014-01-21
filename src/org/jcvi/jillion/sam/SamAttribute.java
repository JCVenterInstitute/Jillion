package org.jcvi.jillion.sam;

public abstract class SamAttribute {
	/**
	 * The two letters of our key
	 * stored as primitives to save memory.
	 */
	char key1,key2;
	
	//type and value are maintained by subtypes
	
	private SamAttribute(char key1, char key2){
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public SamAttributeKey getKey(){
		return new SamAttributeKey(key1, key2);
	}
	
	public boolean isPrintableCharacter(){
		return false;
	}
	public char getPrintableCharacter(){
		throw new UnsupportedOperationException();
	}
	
	public boolean isSignedInt(){
		return false;
	}
	public int getSignedInt(){
		throw new UnsupportedOperationException();
	}
	
	public boolean isFloat(){
		return false;
	}
	public float getFloat(){
		throw new UnsupportedOperationException();
	}
	
	public boolean isString(){
		return false;
	}
	public String getString(){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * A [!-~] Printable character
i [-+]?[0-9]+ Singed 32-bit integer
f [-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)? Single-precision 
oating number
Z [ !-~]+ Printable string, including space
H [0-9A-F]+ Byte array in the Hex format2
B [cCsSiIf](,[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)+ Integer or numeric array
	 */
	public static final class SignedIntegerSamAttribute extends SamAttribute{
		int value;
		
		public SignedIntegerSamAttribute(char key1, char key2, int value){
			super(key1,key2);
			this.value = value;
		}


		@Override
		public boolean isSignedInt() {
			return true;
		}

		@Override
		public int getSignedInt() {
			return value;
		}
		
		
	}
	
	public static final class StringSamAttribute extends SamAttribute{
		String value;
		
		public StringSamAttribute(char key1, char key2, String value){
			super(key1,key2);
			if(value ==null){
				throw new NullPointerException("string can not be null");
			}
			this.value = value;
		}


		@Override
		public boolean isString() {
			return true;
		}

		@Override
		public String getString() {
			return value;
		}
		
		
	}
	public static final class FloatSamAttribute extends SamAttribute{
		float value;
		
		public FloatSamAttribute(char key1, char key2, float value){
			super(key1,key2);
			this.value = value;
		}


		@Override
		public boolean isFloat() {
			return true;
		}

		@Override
		public float getFloat() {
			return value;
		}
		
		
	}
	public static final class PrintableCharacterSamAttribute extends SamAttribute{
		char value;
		
		public PrintableCharacterSamAttribute(char key1, char key2, char value){
			super(key1,key2);
			assertValidValue(value);
			this.value = value;
		}

		private void assertValidValue(char c) {
			//[!-~]
			if(c < 33 || c> 126){
				//not ! thru ~
				throw new IllegalArgumentException("invalid printable char ascii value : " + (int)c);
			}
		}

		@Override
		public boolean isPrintableCharacter() {
			return true;
		}

		@Override
		public char getPrintableCharacter() {
			return value;
		}
		
		
	}
}
