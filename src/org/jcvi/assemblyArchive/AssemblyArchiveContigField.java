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
