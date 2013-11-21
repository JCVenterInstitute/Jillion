package org.jcvi.jillion_experimental.align.blast;

import java.io.IOException;
/**
 * {@code BlastParser}
 * is an interface for parsing
 * Blast output using a {@link BlastVisitor}.
 * @author dkatzel
 *
 */
public interface BlastParser {
	/**
	 * Can the parser (re)parse the blast output.
	 * Some implementations may only be able to 
	 * parse the output a few times (or even only once).
	 * This method allows clients to programatically
	 * check if a call to {@link #parse(BlastVisitor)}
	 * will fail. 
	 * @return {@code true} if {@link #parse(BlastVisitor)}
	 * can be called without throwing an {@link IllegalStateException};
	 * {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the Blast output and call the appropriate
	 * visitXXX methods on the given visitor.
	 * @param visitor the visitor to call the visit 
	 * methods on; can not be null.
	 * @throws IOException if there is a problem parsing
	 * the blast output.
	 * @throws NullPointerException if the visitor
	 * is null.
	 * @throws IllegalStateException if {@link #canParse()}
	 * returns {@code false}.
	 */
	void parse(BlastVisitor visitor) throws IOException;
}
