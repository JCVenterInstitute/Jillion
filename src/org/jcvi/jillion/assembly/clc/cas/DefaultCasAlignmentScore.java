/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasAlignmentScore implements CasAlignmentScore {

    private final int firstInsertion, insertionExtension,
                firstDeletion, deletionExtension,
                match,
                transition, transversion,unknown;
    
    
    /**
     * @param firstInsertion
     * @param insertionExtension
     * @param firstDeletion
     * @param deletionExtension
     * @param match
     * @param transition
     * @param transversion
     * @param unknown
     */
    public DefaultCasAlignmentScore(int firstInsertion, int insertionExtension,
            int firstDeletion, int deletionExtension, int match,
            int transition, int transversion, int unknown) {
        this.firstInsertion = firstInsertion;
        this.insertionExtension = insertionExtension;
        this.firstDeletion = firstDeletion;
        this.deletionExtension = deletionExtension;
        this.match = match;
        this.transition = transition;
        this.transversion = transversion;
        this.unknown = unknown;
    }

    @Override
    public int getDeletionExtensionCost() {
        return deletionExtension;
    }

    @Override
    public int getFirstDeletionCost() {
        return firstDeletion;
    }

    @Override
    public int getFirstInsertionCost() {
        return firstInsertion;
    }

    @Override
    public int getInsertionExtensionCost() {
        return insertionExtension;
    }

    @Override
    public int getMatchScore() {
        return match;
    }

    @Override
    public int getTransitionScore() {
        return transition;
    }

    @Override
    public int getTransversionScore() {
        return transversion;
    }

    @Override
    public int getUnknownScore() {
        return unknown;
    }

    @Override
    public String toString() {
        return "DefaultCasAlignmentScore [deletionExtension="
                + deletionExtension + ", firstDeletion=" + firstDeletion
                + ", firstInsertion=" + firstInsertion
                + ", insertionExtension=" + insertionExtension + ", match="
                + match + ", transition=" + transition + ", transversion="
                + transversion + ", unknown=" + unknown + "]";
    }

}
