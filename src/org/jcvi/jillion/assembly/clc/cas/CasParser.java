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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
/**
 * {@code CasParser} is an interface that will
 * parse a CLC cas formatted structures and call the appropriate 
 * visit methods on the given {@link CasFileVisitor}.
 * @author dkatzel
 *
 */
public interface CasParser {

	/**
	 * Can this handler accept new visit requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link CasParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the phd structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the cas structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link CasFileVisitor}.
	 * @param visitor the {@link CasFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the cas.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(CasFileVisitor visitor) throws IOException;	
	
	 /**
     * Get the cas file's working directory where all relative paths
     * in the cas are referenced from.
     * @return a File; may be null if working dir is the root
     * or local directory. (Similar to File#getParentFile())
     */
    File getWorkingDir();
	
}
