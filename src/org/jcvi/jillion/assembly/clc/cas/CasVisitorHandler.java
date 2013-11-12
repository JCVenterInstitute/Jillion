package org.jcvi.jillion.assembly.clc.cas;

import java.io.IOException;
/**
 * {@code CasVisitorHandler} is an interface that will
 * traverse a CLC cas formatted structures and call the appropriate 
 * visit methods on the given {@link CasFileVisitor}.
 * @author dkatzel
 *
 */
public interface CasVisitorHandler {

	/**
	 * Can this handler accept new visit requests
	 * via accept() calls.
	 * 
	 * Some implementations of {@link CasVisitorHandler}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the phd structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new visit requests; {@code false} otherwise.
	 */
	boolean canAccept();
	/**
	 * Traverse the cas structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link CasFileVisitor}.
	 * @param visitor the {@link CasFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the cas.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(CasFileVisitor visitor) throws IOException;	
	
}
