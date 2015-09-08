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
package org.jcvi.jillion.sam.header;

import java.util.Map;

public final class SamHeaderTag {

	private final Map<String,String> properties;
	
	private final SamHeaderTagKey key;

	private SamHeaderTag(SamHeaderTagKey key, Map<String, String> properties) {
		
		if(key ==null){
			throw new NullPointerException("key can not be null");
		}
		if(properties ==null){
			throw new NullPointerException("properties can not be null");
		}
		this.key = key;
		this.properties = properties;
	}
	
	public boolean hasProperty(String propertyTag){
		return properties.containsKey(propertyTag);
	}
	
	public String getProperty(String propertyTag){
		return properties.get(propertyTag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result +  key.hashCode();
		result = prime * result + properties.hashCode();
		return result;
	}

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
		SamHeaderTag other = (SamHeaderTag) obj;
		if (!key.equals(other.key)){
			return false;
		}
		if (!properties.equals(other.properties)){
			return false;
		}
		return true;
	}
	
	
}
