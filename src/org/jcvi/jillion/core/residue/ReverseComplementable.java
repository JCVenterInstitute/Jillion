package org.jcvi.jillion.core.residue;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public interface ReverseComplementable<R extends Residue & Complementable<R>> extends Iterable<R>{
    /**
     * Create an Iterator that iterates over the reverse complement
     * of this sequence.
     * @return a new Iterator.
     *
     * @implNote the default implementation uses copies all the base's complements into a List
     * then reverses it and returns the iterator to that list
     * @since 6.0
     */
    default Iterator<R> reverseComplementIterator(){
        List<R> list = new ArrayList<>((int) getLength());
        Iterator<R> iter = iterator();
        while(iter.hasNext()) {
            list.add(iter.next().complement());
        }
        Collections.reverse(list);
        return list.iterator();

    }

    long getLength();
}
