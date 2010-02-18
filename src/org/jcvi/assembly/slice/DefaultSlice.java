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
