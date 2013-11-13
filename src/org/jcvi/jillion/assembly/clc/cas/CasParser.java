package org.jcvi.jillion.assembly.clc.cas;

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
	
}
