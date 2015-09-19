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
/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core.util;

import org.jcvi.jillion.core.util.ObjectsUtil;

/**
 * {@code RunLength} is an object reprsentation
 * of a portion of run length encoded data.
 * @author dkatzel
 *
 * @param <T> the Type of object being encoded.
 */
public final class RunLength<T> {
    private final int length;
    private final  T value;
    /**
     * Create a new RunLength instance which represents the given value
     * repeated {@code length} times.
     * @param length the length of this run should be >=1.
     * @param value the value of this object.
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
