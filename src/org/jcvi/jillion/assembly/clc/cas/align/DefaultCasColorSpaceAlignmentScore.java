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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas.align;

public class DefaultCasColorSpaceAlignmentScore implements CasColorSpaceAlignmentScore{

    private final CasAlignmentScore delegate;
    
    private final int colorSpaceError;

    /**
     * @param firstInsertion
     * @param insertionExtension
     * @param firstDeletion
     * @param deletionExtension
     * @param match
     * @param transition
     * @param transversion
     * @param unknown
     * @param delegate
     * @param colorSpaceError
     */
    public DefaultCasColorSpaceAlignmentScore(CasAlignmentScore delegate, int colorSpaceError) {
        this.delegate = delegate;
        this.colorSpaceError = colorSpaceError;
    }

    @Override
    public int getDeletionExtensionCost() {
        return delegate.getDeletionExtensionCost();
    }

    @Override
    public int getFirstDeletionCost() {
        return delegate.getFirstDeletionCost();
    }

    @Override
    public int getFirstInsertionCost() {
        return delegate.getFirstInsertionCost();
    }

    @Override
    public int getInsertionExtensionCost() {
        return delegate.getInsertionExtensionCost();
    }

    @Override
    public int getMatchScore() {
        return delegate.getMatchScore();
    }

    @Override
    public int getTransitionScore() {
        return delegate.getTransitionScore();
    }

    @Override
    public int getTransversionScore() {
        return delegate.getTransversionScore();
    }

    @Override
    public int getUnknownScore() {
        return delegate.getUnknownScore();
    }

    @Override
    public int getColorSpaceErrorCost() {
        return colorSpaceError;
    }

    @Override
    public String toString() {
        return "DefaultCasColorSpaceAlignmentScore [delegate=" + delegate
                + ", colorSpaceError=" + colorSpaceError + "]";
    }
    
}
