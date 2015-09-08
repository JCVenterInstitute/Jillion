/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.core.util.UnsignedByteArray;
import org.jcvi.jillion.core.util.UnsignedIntArray;
import org.jcvi.jillion.core.util.UnsignedShortArray;

public class SamAttribute {
	
	private final SamAttributeKey key;
	
	private final SamAttributeType type;
	
	private final Object value;
	
	//type and value are maintained by subtypes
	public SamAttribute(ReservedSamAttributeKeys key, Object value){
		this(key.getKey(), key.getType(), value);
	}
	public SamAttribute(SamAttributeKey key, SamAttributeType type, Object value){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		if(type==null){
			throw new NullPointerException("type can not be null");
		}
		if(value==null){
			throw new NullPointerException("value can not be null");
		}
		type.validate(value);
		this.key = key;
		this.type = type;
		this.value = value;
	}
	
	public SamAttributeType getType() {
		return type;
	}
	public Object getValue() {
		return value;
	}
	public SamAttributeKey getKey(){
		return key;
	}
	
	public boolean isPrintableCharacter(){
		return type.isPrintableCharacter();
	}
	public char getPrintableCharacter(){
		return type.getPrintableCharacter(value);
	}
	
	public boolean isSignedInt(){
		return type.isSignedInt();
	}
	public int getSignedInt(){
		return type.getSignedInt(value);
	}
	
	public boolean isFloat(){
		return type.isFloat();
	}
	public float getFloat(){
		return type.getFloat(value);
	}
	
	public boolean isString(){
		return type.isString();
	}
	public String getString(){
		return type.getString(value);
	}
	
	public boolean isByteArray(){
		return type.isByteArray();
	}
	public byte[] getByteArray(){
		return type.getByteArray(value);
	}
	public boolean isShortArray(){
		return type.isShortArray();
	}
	public short[] getShortArray(){
		return type.getShortArray(value);
	}
	public boolean isIntArray(){
		return type.isIntArray();
	}
	public int[] getIntArray(){
		return type.getIntArray(value);
	}
	public boolean isUnsignedByteArray(){
		return type.isUnsignedByteArray();
	}
	public UnsignedByteArray getUnsignedByteArray(){
		return type.getUnsignedByteArray(value);
	}
	public boolean isUnsignedShortArray(){
		return type.isUnsignedShortArray();
	}
	public UnsignedShortArray getUnsignedShortArray(){
		return type.getUnsignedShortArray(value);
	}
	public boolean isUnsignedIntArray(){
		return type.isUnsignedIntArray();
	}
	public UnsignedIntArray getUnsignedIntArray(){
		return type.getUnsignedIntArray(value);
	}
	
	public boolean isFloatArray(){
		return type.isFloatArray();
	}
	public float[] getFloatArray(){
		return type.getFloatArray(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key.hashCode();
		result = prime * result +  type.hashCode();
		result = prime * result + type.textEncode(value).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SamAttribute)) {
			return false;
		}
		SamAttribute other = (SamAttribute) obj;
		if (!key.equals(other.key)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		//need to do type conversion
		//because SAM vs BAM might have different 
		//Object for example "1" instead of 1.
		if (!type.textEncode(value).equals(type.textEncode(other.value))) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "SamAttribute [key=" + key + ", type=" + type + ", value="
				+ value + "]";
	}
	
	
}
