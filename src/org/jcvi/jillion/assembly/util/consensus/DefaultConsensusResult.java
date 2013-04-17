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
