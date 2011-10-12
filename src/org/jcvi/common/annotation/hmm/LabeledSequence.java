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

package org.jcvi.common.annotation.hmm;

import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

/**
 * {@code LabeledSequence} an object
 * that keeps track of a Sequence and its
 * path through an arbitrary HMM model.
 * @author dkatzel
 */
public final class LabeledSequence{
    private final Sequence<Nucleotide> sequence;
    private final List<Integer> path;
    
    
    public LabeledSequence(Sequence<Nucleotide> sequence, List<Integer> path) {
        this.sequence = sequence;
        this.path = path;
    }

    public Sequence<Nucleotide> getSequence() {
        return sequence;
    }

    public List<Integer> getPath() {
        return path;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return "LabeledSequence [sequence=" + sequence + ", path=" + path + "]";
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result
                + ((sequence == null) ? 0 : sequence.hashCode());
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LabeledSequence)) {
            return false;
        }
        LabeledSequence other = (LabeledSequence) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        if (sequence == null) {
            if (other.sequence != null) {
                return false;
            }
        } else if (!sequence.equals(other.sequence)) {
            return false;
        }
        return true;
    }
    
    
}
