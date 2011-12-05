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
package org.jcvi.common.core.assembly.contig.cas.align;

import org.jcvi.common.core.assembly.clc.cas.CasMatch;

public class DefaultCasMatch implements CasMatch{
    private final boolean hasMatch,
     isPartOfPair;
    private final CasAlignment alignment;
    private final long numberOfMatches,numberOfReportedAlignments;
    private final int score;
    /**
     * @param hasMatch
     * @param hasMultipleMatches
     * @param hasMultipleAlignments
     * @param isPartOfPair
     */
    public DefaultCasMatch(boolean hasMatch, long numberOfMatches,
            long numberOfReportedAlignments,
             boolean isPartOfPair,CasAlignment chosenAlignment, int score) {
        this.hasMatch = hasMatch;
        this.numberOfMatches = numberOfMatches;
        this.numberOfReportedAlignments = numberOfReportedAlignments;
        this.isPartOfPair = isPartOfPair;
        this.alignment = chosenAlignment;
        this.score = score;
    }

    @Override
    public boolean hasMultipleAlignments() {
        return numberOfReportedAlignments>1;
    }

    @Override
    public boolean matchReported() {
        return hasMatch;
    }

    @Override
    public boolean readHasMutlipleMatches() {
        return numberOfMatches>1;
    }

    @Override
    public boolean readIsPartOfAPair() {
        return isPartOfPair;
    }

    @Override
    public CasAlignment getChosenAlignment() {
        return alignment;
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder("DefaultCasMatch [hasMatch=" + hasMatch
                + ", numberOfAlignments=" + numberOfReportedAlignments
                + ", numberOfMatches=" + numberOfMatches
                + ", isPartOfPair=" + isPartOfPair + ", alignments=");
        if(alignment !=null){
            result.append(String.format("%n%n\t%s", alignment));
        }
               result.append(String.format("%n]"));
        
        return result.toString();
    }

    @Override
    public long getNumberOfReportedAlignments() {
        return numberOfReportedAlignments;
    }

    @Override
    public long getNumberOfMatches() {
        return numberOfMatches;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((alignment == null) ? 0 : alignment.hashCode());
        result = prime * result + (hasMatch ? 1231 : 1237);
        result = prime * result + (isPartOfPair ? 1231 : 1237);
        result = prime * result
                + (int) (numberOfMatches ^ (numberOfMatches >>> 32));
        result = prime * result + score;
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
        if (alignment == null) {
            if (other.alignment != null) {
                return false;
            }
        } else if (!alignment.equals(other.alignment)) {
            return false;
        }
        if (hasMatch != other.hasMatch) {
            return false;
        }
        if (isPartOfPair != other.isPartOfPair) {
            return false;
        }
        if (numberOfMatches != other.numberOfMatches) {
            return false;
        }
        if (score != other.score) {
            return false;
        }
        return true;
    }

    
   

  

}
