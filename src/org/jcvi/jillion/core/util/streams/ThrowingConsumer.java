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

public /**
 * A {@link java.util.function.Consumer} that can throw an exception.
 * @author dkatzel
 *
 * @param <T> the type the consumer accepts.
 * @param <E> the exception that can be thrown.
 * 
 * @since 5.3
 */
interface ThrowingConsumer<T, E extends Throwable>{
    void accept(T t) throws E;

}
