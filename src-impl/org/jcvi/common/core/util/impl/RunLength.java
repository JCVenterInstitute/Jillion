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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util.impl;

import org.jcvi.common.core.util.ObjectsUtil;


public final class RunLength<T> {
    private final int length;
    private final  T value;
    /**
     * @param length
     * @param value
     */
    public RunLength(T value,int length) {
        this.length = length;
        this.value = value;
    }
    public int getLength() {
        return length;
    }
    public T getValue() {
        return value;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
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
        if (!(obj instanceof RunLength)){
            return false;
        }
        RunLength<?> other = (RunLength<?>) obj;
        return length == other.length  
        		&& ObjectsUtil.nullSafeEquals(getValue(), other.getValue());
    }
    @Override
    public String toString() {
       StringBuilder builder = new StringBuilder();
       builder.append(value)
               .append("x ")
               .append(getLength());
        return builder.toString();
    }

}
