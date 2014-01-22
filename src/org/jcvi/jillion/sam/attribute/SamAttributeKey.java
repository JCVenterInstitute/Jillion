package org.jcvi.jillion.sam.attribute;

public final class SamAttributeKey{
	/**
	 * The two letters of our key
	 * stored as primitives to save memory.
	 */
	char key1,key2;

	public SamAttributeKey(char key1, char key2) {
		 if (!assertValid1(key1) || !assertValid2(key2)){
			 throw new IllegalArgumentException("invalid key " + key1 + key2);
		 }
		
		this.key1 = key1;
		this.key2 = key2;
	}
	/**
	 * Assert that the first key character is a valid letter
	 * [A-Za-z].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private boolean assertValid1(char c) {
		//A-Zz-z
		if(c<65 || c>122){
			return false;
		}
		//check for special chars between Z-a
		//[91-96] invalid
		if(c > 90 && c<97){
			return false;
		}
		return true;
	}
	/**
	 * Assert that the second key character is a valid letter or digit
	 * [A-Za-z0-9].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private boolean assertValid2(char c) {
		if(c >=48 && c<=57){
			//digit is good
			return true;
		}
		return assertValid1(c);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key1;
		result = prime * result + key2;
		return result;
	}
	/**
	 * Two Keys are equal if they have the same
	 * 2 characters.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SamAttributeKey other = (SamAttributeKey) obj;
		if (key1 != other.key1)
			return false;
		if (key2 != other.key2)
			return false;
		return true;
	}
	/**
	 * The two letter key as a String.
	 * @return
	 */
	@Override
	public String toString(){
		return new StringBuilder(2).append(key1).append(key2).toString();
	}
	/**
	 * Get the first character of the key.
	 * @return a char
	 */
	public char getFirstChar() {
		return key1;
	}
	/**
	 * Get the second character of the key.
	 * @return a char
	 */
	public char getSecondChar() {
		return key2;
	}
	
	
	
	
}