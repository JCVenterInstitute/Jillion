/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core.residue;

import java.util.Objects;

public class Kmer<T extends ResidueSequence<?, ?, ?>> {

    private final T value;
    private final long offset;
    
    public Kmer(long offset, T value) {
        if(offset < 0){
            throw new IllegalArgumentException("offset can not be negative : " + offset);
        }
        this.offset = offset;
        this.value = Objects.requireNonNull(value);
    }

    public T getValue() {
        return value;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (offset ^ (offset >>> 32));
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Kmer [value=" + value + ", offset=" + offset + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Kmer)) {
            return false;
        }
        Kmer other = (Kmer) obj;
        if (offset != other.offset) {
            return false;
        }
        if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
    
    
}
