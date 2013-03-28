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
package org.jcvi.jillion.internal.core.util;

import java.io.Serializable;
import java.util.Comparator;


/**
 * {@code ComparableComparator} creates a
 * {@link Comparator} instance using the natural
 * ordering of the given Comparable.
 * @author dkatzel
 */
public final class ComparableComparator<T extends Comparable<? super T>> implements Comparator<T>, Serializable{

    private static final long serialVersionUID = 8748840566214201421L;

    /**
     * Static helper factory method to infer generic types for us.
     * @param <T> the Comparable to make a comparator for.
     * @return a new instance of a Comparator of type T.
     */
    public static <T extends Comparable<? super T>> ComparableComparator<T> create(){
        return new ComparableComparator<T>();
    }
    
    @Override
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }
    
}
