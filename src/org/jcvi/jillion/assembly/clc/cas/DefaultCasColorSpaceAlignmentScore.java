/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasColorSpaceAlignmentScore implements CasColorSpaceAlignmentScore{

    private final CasAlignmentScore delegate;
    
    private final int colorSpaceError;

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
