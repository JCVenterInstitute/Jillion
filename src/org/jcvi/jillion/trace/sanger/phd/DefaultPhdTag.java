/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

public class DefaultPhdTag implements PhdTag{

    private final String name;
    private final String value;
    
    
    /**
     * @param name
     * @param value
     */
    public DefaultPhdTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getTagName() {
        return name;
    }

    @Override
    public String getTagValue() {
        return value;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof DefaultPhdTag)){
            return false;
        }
        DefaultPhdTag other = (DefaultPhdTag) obj;
        if (name == null) {
            if (other.name != null){
                return false;
            }
        } else if (!name.equals(other.name)){
            return false;
        }
        if (value == null) {
            if (other.value != null){
                return false;                
            }
        } else if (!value.equals(other.value)){
            return false;            
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultPhdTag [name=" + name + ", value=" + value + "]";
    }
    

}
