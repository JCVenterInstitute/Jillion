package org.jcvi.jillion.sam;

import java.io.IOException;
/**
 * {@code SamParser}
 * is an interface that can parse
 * SAM or BAM files and call the appropriate
 * methods on the given {@link SamVisitor}.
 * @author dkatzel
 *
 */
public interface SamParser {
	/**
	 * 
	 * @return
	 */
	boolean canAccept();
	/**
	 * Parse the given {@link SamVisitor}
	 * and call the appropriate visit methods
	 * on the given visitor.
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException
	 * @throws NullPointerException if visitor is null.
	 */
	void accept(SamVisitor visitor) throws IOException;
}
