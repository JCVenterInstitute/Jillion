/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.attribute;

import java.io.ObjectInputStream;
import java.io.Serializable;

import org.jcvi.jillion.internal.sam.SamUtil;

public final class SamAttributeKey implements Serializable{
	
	private static final long serialVersionUID = -3273090126405748439L;
	/**
	 * The two letters of our key
	 * stored as primitives to save memory.
	 */
	private final char key1,key2;

	public SamAttributeKey(char key1, char key2) {
		 if(!SamUtil.isValidKey(key1, key2)){
			 throw new IllegalArgumentException("invalid key " + key1 + key2);
		 
		 }
		
		this.key1 = key1;
		this.key2 = key2;
	}
	
	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}
	private Object writeReplace(){
		return new KeyProxy(this);
	}
	
	private static class KeyProxy implements Serializable{
		
		private static final long serialVersionUID = 2405702978232081432L;
		private final char key1,key2;
		
		public KeyProxy(SamAttributeKey k) {
			this.key1 = k.key1;
			this.key2 = k.key2;
		}
		private Object readResolve(){
			return new SamAttributeKey(key1,key2);
		}
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
	 * @param obj the other object to compare.
	 * @return {@code true} if the other object is also a {@link SamAttributeKey}
	 * and the 2 character key is the same as this 2 character key.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}		
		if (getClass() != obj.getClass()){
			return false;
		}
		SamAttributeKey other = (SamAttributeKey) obj;
		if (key1 != other.key1){
			return false;
		}
		if (key2 != other.key2){
			return false;
		}
		return true;
	}
	/**
	 * The two letter key as a String.
	 * @return the 2 letter key.
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
