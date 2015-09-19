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
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

class DefaultConsensusResult implements ConsensusResult {

    private final Nucleotide consensus;
    private final int consensusQuality;
    
    /**
     * @param consensus
     * @param consensusQuality
     */
    public DefaultConsensusResult(Nucleotide consensus,
            int consensusQuality) {
        this.consensus = consensus;
        this.consensusQuality = consensusQuality;
    }

    @Override
    public Nucleotide getConsensus() {
        return consensus;
    }

    @Override
    public int getConsensusQuality() {
        return consensusQuality;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((consensus == null) ? 0 : consensus.hashCode());
        result = prime * result + consensusQuality;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof DefaultConsensusResult)){
            return false;
        }
        DefaultConsensusResult other = (DefaultConsensusResult) obj;
        if (consensus == null) {
            if (other.consensus != null){
                return false;
            }
        } else if (!consensus.equals(other.consensus)){
            return false;
        }
        if (consensusQuality != other.consensusQuality){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s[%d]",consensus, consensusQuality);
    }

}
