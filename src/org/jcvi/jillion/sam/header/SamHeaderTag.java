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
