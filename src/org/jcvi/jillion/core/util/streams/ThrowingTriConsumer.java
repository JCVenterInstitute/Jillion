/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core.util.streams;

/**
 * A functional interface that consumes 3 parameters
 * and can throw an Exception.
 * @author dkatzel
 *
 * @param <T> the first parameter.
 * @param <U> the second parameter.
 * @param <V> the second parameter.
 * @param <E> the exception that may be thrown.
 */
@FunctionalInterface
public interface ThrowingTriConsumer<T,U, V, E extends Throwable> {
    /**
     * Consume the given 3 parameters and throw an exception if needed.
     * @param t the first parameter.
     * @param u the second parameter.
     * @param v the thrid parameter.
     * @throws E the exception to throw if there is a problem.
     */
     void accept(T t, U u, V v) throws E;
}
