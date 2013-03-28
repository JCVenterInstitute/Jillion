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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * {@code JoinedStringBuilder}
 * is a Builder that builds a single String
 * object from a collection of Objects
 * that can optionally be concatenated with 
 * another "glue" Object and/or prefixed and suffixed
 * with other Objects. Objects
 * to be joined use their {@link Object#toString()}
 * method to get their String representation.
 * <br/>
 * <strong>This class is not thread-safe.</strong>
 * @author dkatzel
 *
 *
 */
public final class JoinedStringBuilder implements Builder<String>{
    private final Iterable<?> elements;
    private Object glue;
    private Object prefix;
    private Object suffix;
    /**
     * Join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     */
    public JoinedStringBuilder(Iterable<?> elements){
        this.elements = elements;
    }
    /**
     * Join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     */
    public JoinedStringBuilder(Object... elements){
        this(Arrays.asList(elements));
    }
    /**
     * The given glue's {@link Object#toString()} is what is concatenated
     * between the joined the elements.
     * @param glue the String to put between concatenated
     * elements, if glue is set to {@code null}
     * then no glue will be used.
     * @return this
     */
    public JoinedStringBuilder glue(Object glue){
        this.glue = glue;
        return this;
    }
    /**
     * Prefix the objects to be joined with the given
     * prefix.
     * @param prefix the prefix that will
     * be in the resulting String
     * before the objects are joined;
     * or {@code null} if there should be no prefix.
     * @return this
     */
    public JoinedStringBuilder prefix(Object prefix){
        this.prefix = prefix;
        return this;
    }  
    /**
     * Suffix the objects to be joined with the given
     * suffix.
     * @param suffix the suffix that will
     * be in the resulting String
     * after the objects are joined;
     * or {@code null} if there should be no suffix.
     * @return this
     */
    public JoinedStringBuilder suffix(Object suffix){
        this.suffix = suffix;
        return this;
    }  
    @Override
    public String toString(){
        return build();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public String build() {
        final StringBuilder joined = new StringBuilder();
        if(prefix!=null){
            joined.append(prefix);
        }
        Iterator<?> iter = elements.iterator();
        if(iter.hasNext()){
            Object firstElement= iter.next();
            if(firstElement!=null){
                joined.append(firstElement.toString());
            }
        }
        
        while(iter.hasNext()){
            
            Object item = iter.next();
            if (item != null)
            {   
                String itemString = item.toString();
                if (glue != null && joined.length() > 0 && itemString.length() > 0)
                {
                    joined.append(glue.toString());
                }
                joined.append(itemString);
            }
        }
        if(suffix!=null){
            joined.append(suffix);
        }
        return joined.toString();
    }
    
    
}
