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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.assembly.cas.CasMatch;

public class DefaultCasMatch implements CasMatch{
    private final boolean hasMatch,
     hasMultipleMatches,
     hasMultipleAlignments,
     isPartOfPair;
    private final List<CasAlignment> alignments;
    
    /**
     * @param hasMatch
     * @param hasMultipleMatches
     * @param hasMultipleAlignments
     * @param isPartOfPair
     */
    public DefaultCasMatch(boolean hasMatch, boolean hasMultipleMatches,
            boolean hasMultipleAlignments, boolean isPartOfPair,List<CasAlignment> alignments) {
        this.hasMatch = hasMatch;
        this.hasMultipleMatches = hasMultipleMatches;
        this.hasMultipleAlignments = hasMultipleAlignments;
        this.isPartOfPair = isPartOfPair;
        this.alignments = new ArrayList<CasAlignment>(alignments);
    }

    @Override
    public boolean hasMultipleAlignments() {
        return hasMultipleAlignments;
    }

    @Override
    public boolean matchReported() {
        return hasMatch;
    }

    @Override
    public boolean readHasMutlipleMatches() {
        return hasMultipleMatches;
    }

    @Override
    public boolean readIsPartOfAPair() {
        return isPartOfPair;
    }

    @Override
    public List<CasAlignment> getAlignments() {
        return Collections.unmodifiableList(alignments);
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder("DefaultCasMatch [hasMatch=" + hasMatch
                + ", hasMultipleAlignments=" + hasMultipleAlignments
                + ", hasMultipleMatches=" + hasMultipleMatches
                + ", isPartOfPair=" + isPartOfPair + ", alignments=");
        for(CasAlignment alignment : alignments){
            result.append(String.format("%n%n\t%s", alignment));
        }
               result.append(String.format("%n]"));
        
        return result.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((alignments == null) ? 0 : alignments.hashCode());
        result = prime * result + (hasMatch ? 1231 : 1237);
        result = prime * result + (hasMultipleAlignments ? 1231 : 1237);
        result = prime * result + (hasMultipleMatches ? 1231 : 1237);
        result = prime * result + (isPartOfPair ? 1231 : 1237);
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
        if (!(obj instanceof DefaultCasMatch)) {
            return false;
        }
        DefaultCasMatch other = (DefaultCasMatch) obj;
        if (alignments == null) {
            if (other.alignments != null) {
                return false;
            }
        } else if (!alignments.equals(other.alignments)) {
            return false;
        }
        if (hasMatch != other.hasMatch) {
            return false;
        }
        if (hasMultipleAlignments != other.hasMultipleAlignments) {
            return false;
        }
        if (hasMultipleMatches != other.hasMultipleMatches) {
            return false;
        }
        if (isPartOfPair != other.isPartOfPair) {
            return false;
        }
        return true;
    }

}
