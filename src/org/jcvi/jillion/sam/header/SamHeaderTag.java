package org.jcvi.jillion.sam.header;

import java.util.Map;

public class SamHeaderTag {

	private final Map<String,String> properties;
	
	private SamHeaderTagKey key;

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
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
