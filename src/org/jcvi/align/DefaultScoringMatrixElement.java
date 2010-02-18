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
 * Created on Nov 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;



public class DefaultScoringMatrixElement implements ScoringMatrixElement {

    private final Path dir;
    private final int score;
    
    /**
     * @param dir
     * @param score
     */
    public DefaultScoringMatrixElement(Path dir, int score) {
        this.dir = dir;
        this.score = score;
    }


    @Override
    public Path getDirection() {
        return dir;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        result = prime * result + score;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultScoringMatrixElement))
            return false;
        DefaultScoringMatrixElement other = (DefaultScoringMatrixElement) obj;
        if (dir == null) {
            if (other.dir != null)
                return false;
        } else if (!dir.equals(other.dir))
            return false;
        if (score != other.score)
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "DefaultScoringMatrixElement [dir=" + dir + ", score=" + score
                + "]";
    }
    

}
