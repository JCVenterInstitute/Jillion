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
