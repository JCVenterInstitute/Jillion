/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.io.Serializable;
import java.util.Comparator;

public class PlacedStartComparator <T extends Placed> implements Comparator<T>,Serializable {       
    /**
     * 
     */
    private static final long serialVersionUID = -8517894363563047881L;

    @Override
    public int compare(T o1, T o2) {           
        return Long.valueOf(o1.getStart()).compareTo(o2.getStart());
    }

}
