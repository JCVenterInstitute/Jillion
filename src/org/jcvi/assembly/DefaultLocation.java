/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;


public class DefaultLocation<T> implements Location<T> {

    private final T source;
    private final int index;
    
    
    /**
     * @param source
     * @param index
     */
    public DefaultLocation(T source, int index) {
        if(source==null){
            throw new IllegalArgumentException("source can not be null");
        }
        this.source = source;
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public T getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + source.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Location))
            return false;
        Location other = (Location) obj;
        return (index == other.getIndex()) && source.equals(other.getSource());
        
    }
    
    

}
