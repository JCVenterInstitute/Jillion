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
 * Created on Sep 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

public enum AssemblyArchiveContigField {
    /**
     * Consensus base calls.
     */
    CONSENSUS("consensus"),
    /**
     * consensus quality scores
     */
    CONSENSUS_QUALITIES("conqualities"),
    /**
     * Number of contigs in the assembly.
     */
    NUMBER_OF_CONTIGS("ncontigs"),
    /**
     * Number of bases of consensus in the contigs of the assembly.
     */
    NUMBER_OF_CONSENSUS_BASES("nconbases"),
    /**
     * Number of gaps in the consensus in a specific contig of an assembly.
     */
    NUMBER_OF_CONSENSUS_GAPS("ncongaps"),
    /**
     * Listing of gap positions in a contig.
     */
    CONSENSUS_GAP_LIST("congaps"),
    /**
     * Total number of base calls used in the assembly.
     */
    NUMBER_OF_BASES("nbasecalls"),
    /**
     * Number of traces referred to in the assembly.
     */
    NUMBER_OF_TRACES("ntraces");
    
  private final String elementName;
    
    AssemblyArchiveContigField(String name){
        this.elementName = name;
    }

    @Override
    public String toString() {
        return elementName;
    }
}
