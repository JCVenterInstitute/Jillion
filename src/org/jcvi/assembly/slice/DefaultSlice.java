/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultSlice implements Slice{
    private final Map<String,SliceElement> elements;
    
    public DefaultSlice(List<SliceElement> elements){
        this.elements = new HashMap<String, SliceElement>(elements.size(),1);
        for(SliceElement element : elements){
            this.elements.put(element.getName(), element);
        }
        
    }
    @Override
    public List<SliceElement> getSliceElements() {
        return new ArrayList(elements.values());
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + elements.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultSlice))
            return false;
        DefaultSlice other = (DefaultSlice) obj;
        for(Entry<String,SliceElement> entry : elements.entrySet()){
            if(!other.containsElement(entry.getKey())){
                return false;
            }
            if(!entry.getValue().equals(other.getSliceElement(entry.getKey()))){
                return false;
            }
        }
       return true;
            
    }
    @Override
    public String toString() {
        return elements.toString();
    }
    @Override
    public int getCoverageDepth() {
        return elements.size();
    }
    @Override
    public Iterator<SliceElement> iterator() {
        return elements.values().iterator();
    }
    @Override
    public boolean containsElement(String elementId) {
        return getSliceElement(elementId)!=null;
    }
    @Override
    public SliceElement getSliceElement(String elementId) {
        return elements.get(elementId);
    }
    

}
