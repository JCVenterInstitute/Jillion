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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

final class DefaultCasFileInfo implements CasFileInfo{

    private final List<String> names;
    private final BigInteger numberOfResidues;
    private final long numberOfSequences;
   
    public DefaultCasFileInfo(List<String> names, long numberOfSequences,
            BigInteger numberOfResidues) {
        this.names = new ArrayList<String>(names);
        this.numberOfSequences = numberOfSequences;
        this.numberOfResidues = numberOfResidues;
    }

    @Override
    public List<String> getFileNames() {
        return names;
    }

    @Override
    public BigInteger getNumberOfResidues() {
        return numberOfResidues;
    }

    @Override
    public long getNumberOfSequences() {
        return numberOfSequences;
    }

    @Override
    public String toString() {
        return "DefaultCasFileInfo [numberOfSequences=" + numberOfSequences
                + ", numberOfResidues=" + numberOfResidues + ", names=" + names
                + "]";
    }

}
