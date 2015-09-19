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
package org.jcvi.jillion.trace.sff;
/**
 * {@code SffVisitorCallback}
 * is a callback mechanism for the {@link SffVisitor}
 * instance to communicate with the parser
 * that is parsing the sff data.
 * @author dkatzel
 *
 */
public interface SffVisitorCallback {
	/**
	 * {@code SffVisitorMemento} is a marker
	 * interface that {@link SffParser}
	 * instances can use to "rewind" back
	 * to the position in its sff structure
	 * in order to revisit portions of the data. 
	 * {@link SffVisitorMemento} should only be used
	 * by the {@link SffParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface SffVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
	 * creating {@link SffVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean mementoSupported();
	/**
	 * Create a {@link SffVisitorMemento}
	 * 
	 * @return a {@link SffVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	SffVisitorMemento createMemento();
	/**
	 * Tell the {@link SffParser} to stop parsing
	 * the sff.  {@link SffVisitor#visitEnd()}
	 * will still be called.
	 */
	void haltParsing();
}
