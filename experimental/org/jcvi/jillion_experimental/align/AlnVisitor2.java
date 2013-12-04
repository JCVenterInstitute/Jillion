package org.jcvi.jillion_experimental.align;

import java.util.Set;
/**
 * {@code AlnVisitor} is a visitor interface
 * to visit aln formatted data like that produced
 * by multiple sequence alignment programs
 * such as ClustalW.
 * 
 * @author dkatzel
 *
 */
public interface AlnVisitor2 {

	 /**
     * {@code ConservationInfo} contains information
     * about how each column (slice) in a group block
     * match.
     * @author dkatzel
     */
    public enum ConservationInfo{
        /**
         * The residues in the column are identical
         * in all sequences in the alignment.
         */
        IDENTICAL,
        /**
         * A conserved substitution has been
         * observed in this column.
         */
        CONSERVED_SUBSITUTION,
        /**
         * A semi-conserved substitution has been
         * observed in this column.
         */
        SEMI_CONSERVED_SUBSITUTION,
        /**
         * There is no conservation
         * in this column.  This could
         * mean that there are gaps
         * in the alignment at this column.
         */
        NOT_CONSERVED
        ;
    }
    
    
    /**
     * {@code FastqVisitorCallback}
     * is a callback mechanism for the {@link FastaVisitor}
     * instance to communicate with the parser
     * that is parsing the fasta data.
     * @author dkatzel
     *
     */
    public interface AlnVisitorCallback {
    	/**
    	 * {@code AlnVisitorMemento} is a marker
    	 * interface that {@link AlnParser}
    	 * instances can use to "rewind" back
    	 * to the position in its aln file
    	 * in order to revisit portions of the aln file. 
    	 * {@link AlnVisitorMemento} should only be used
    	 * by the {@link AlnParser} instance that
    	 * generated it.
    	 * @author dkatzel
    	 *
    	 */
    	interface AlnVisitorMemento{
    		
    	}
    	/**
    	 * Is this callback capable of
    	 * creating {@link AlnVisitorMemento}s
    	 * via {@link #createMemento()}.
    	 * @return {@code true} if this callback
    	 * can create mementos; {@code false} otherwise.
    	 */
    	boolean canCreateMemento();
    	/**
    	 * Create a {@link AlnVisitorMemento}
    	 * 
    	 * @return a {@link AlnVisitorMemento}; never null.
    	 * @see #canCreateMemento()
    	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
    	 * returns {@code false}.
    	 */
    	AlnVisitorMemento createMemento();
    	/**
    	 * Tell the {@link AlnParser} to stop parsing
    	 * the alignment.  {@link AlnVisitor#visitEnd()}
    	 * will still be called.
    	 */
    	void haltParsing();
    }
    
    void visitEnd();
    
    void halted();
    
    AlnGroupVisitor visitGroup(Set<String> ids, AlnVisitorCallback callback);
}
