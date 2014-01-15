package org.jcvi.jillion_experimental.align;

import java.util.Set;
/**
 * {@code AlnVisitor} is a visitor interface
 * to visit aln formatted data like that produced
 * by multiple sequence alignment programs
 * such as ClustalW.
 * <p>
 * Usually .aln files split an alignment
 * into "groups".  Each group contains
 * a consecutive block of alignment data
 * for all the input that aligned in that region.
 * 
 * Visiting an alignment will therefore require
 * visiting several groups.  Sequences may span
 * consecutive groups if the sequence is 
 * longer than the group length. 
 * 
 * </p>
 * @author dkatzel
 *
 */
public interface AlnVisitor {    
    
    /**
     * {@code AlnVisitorCallback}
     * is a callback mechanism for the {@link AlnVisitor}
     * instance to communicate with the parser
     * that is parsing the aln data.
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
    /**
     * The entire alignment has been visited.
     */
    void visitEnd();
    /**
     * The alignment visiting has been halted
     * by a call to {@link AlnVisitorCallback#haltParsing()}.
     */
    void halted();
    /**
     * Visit the next group.
     * Usually .aln files split an alignment
	 * into "groups".  Each group contains
	 * a consecutive block of alignment data
	 * for all the input that aligned in that region.
	 * 
	 * Visiting an alignment will therefore require
	 * visiting several groups.  Sequences may span
	 * consecutive groups if the sequence is 
	 * longer than the group length. 
     * @param ids the sequence ids contained in this group;
     * will never be null and should never be empty.
     * @param callback the {@link AlnVisitorCallback}
     * for this group; will never be null.
     * @return a {@link AlnGroupVisitor} instance to visit
     * this group; or {@code null} to skip this group.
     */
    AlnGroupVisitor visitGroup(Set<String> ids, AlnVisitorCallback callback);
    /**
     * Visit the Header of this alignment data.
     * @param header a single line that will start with either 
     * "CLUSTAL W" or "CLUSTALW" but may have more information
     * than that (including version etc);
     * will never be null or empty.
     */
	void visitHeader(String header);
}
