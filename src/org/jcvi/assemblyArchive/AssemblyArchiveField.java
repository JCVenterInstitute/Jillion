/*
 * Created on Sep 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

public enum AssemblyArchiveField {
    /**
     * Submitter's institution designation.
     */
    CENTER_NAME("center_name"),
    /**
     * Genbank taxonomic reference.
     */
    TAXON_ID("taxid"),
    /**
     * Date that the submission was prepared.
     */
    DATE("date"),
    /**
     * Freeform description of the assembly or the submission.
     */
    DESCRIPTION("description"),
    /**
     * Submitter's structural assignment, for example chromosome 3.
     * For some genomes these designations may have been standardized.
     */
    STRUCTURE("structure"),
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
    NUMBER_OF_TRACES("ntraces"),
    /**
     * Coverage ratio.
     */
    COVERAGE("coverage"),
    /**
     * The submission contains one or more contigs.
     */
    CONTIG("contig");
    
    
    private final String elementName;
    
    AssemblyArchiveField(String name){
        this.elementName = name;
    }

    @Override
    public String toString() {
        return elementName;
    }
    
    
}
