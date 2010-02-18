/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

public class DefaultCasContigDescription implements CasContigDescription {
    private final boolean isCircular;
    private long contigLength;
    
    
    /**
     * @param contigLength
     * @param isCircular
     */
    public DefaultCasContigDescription(long contigLength, boolean isCircular) {
        this.contigLength = contigLength;
        this.isCircular = isCircular;
    }

    @Override
    public long getContigLength() {
        return contigLength;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public String toString() {
        return "DefaultCasContigDescription [contigLength=" + contigLength
                + ", isCircular=" + isCircular + "]";
    }

}
