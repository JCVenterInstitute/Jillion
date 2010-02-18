/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DefaultCasFileInfo implements CasFileInfo{

    private final List<String> names;
    private final BigInteger numberOfResidues;
    private final long numberOfSequences;
    
    
    /**
     * @param names
     * @param numberOfSequences
     * @param numberOfResidues
     */
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
