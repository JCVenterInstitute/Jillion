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

package org.jcvi.common.core.seq.read.trace.frg.afg;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultAmosFragment implements AmosFragment{

    private final String id;
    private final int index;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    
    private final Range clearRange;
    private final Range vectorRange;
    private final Range qualityRange;
    
    
    /**
     * @param id
     * @param index
     * @param basecalls
     * @param qualities
     * @param clearRange
     * @param vectorRange
     * @param qualityRange
     */
    public DefaultAmosFragment(String id, int index,
            NucleotideSequence basecalls,
            QualitySequence qualities, Range clearRange,
            Range vectorRange, Range qualityRange) {
        this.id = id;
        this.index = index;
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.clearRange = clearRange;
        this.vectorRange = vectorRange;
        this.qualityRange = qualityRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return id;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getQualityClearRange() {
        return qualityRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getValidRange() {
        return clearRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getVectorClearRange() {
        return vectorRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getBasecalls() {
        return basecalls;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public QualitySequence getQualities() {
        return qualities;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        return basecalls.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((basecalls == null) ? 0 : basecalls.asList().hashCode());
        result = prime * result
                + ((clearRange == null) ? 0 : clearRange.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + index;
        result = prime * result
                + ((qualities == null) ? 0 : qualities.asList().hashCode());
        result = prime * result
                + ((qualityRange == null) ? 0 : qualityRange.hashCode());
        result = prime * result
                + ((vectorRange == null) ? 0 : vectorRange.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultAmosFragment)) {
            return false;
        }
        DefaultAmosFragment other = (DefaultAmosFragment) obj;
        if (basecalls == null) {
            if (other.basecalls != null) {
                return false;
            }
        } else if (!basecalls.asList().equals(other.basecalls.asList())) {
            return false;
        }
        if (clearRange == null) {
            if (other.clearRange != null) {
                return false;
            }
        } else if (!clearRange.equals(other.clearRange)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (index != other.index) {
            return false;
        }
        if (qualities == null) {
            if (other.qualities != null) {
                return false;
            }
        } else if (!qualities.asList().equals(other.qualities.asList())) {
            return false;
        }
        if (qualityRange == null) {
            if (other.qualityRange != null) {
                return false;
            }
        } else if (!qualityRange.equals(other.qualityRange)) {
            return false;
        }
        if (vectorRange == null) {
            if (other.vectorRange != null) {
                return false;
            }
        } else if (!vectorRange.equals(other.vectorRange)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultAmosFragment [basecalls=" + basecalls + ", clearRange="
                + clearRange + ", id=" + id + ", index=" + index
                + ", qualities=" + qualities + ", qualityRange=" + qualityRange
                + ", vectorRange=" + vectorRange + "]";
    }

}
