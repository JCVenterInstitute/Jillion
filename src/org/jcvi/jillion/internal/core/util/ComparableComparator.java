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
