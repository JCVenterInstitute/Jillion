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
package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * Utility class that wraps a single element inside an iterator.
 * NOTE : This class is not Threadsafe.
 * 
 * @author dkatzel
 *
 * @param <T> the Type of the element
 * 
 */
public final class SingleElementIterator<T> implements Iterator<T>{

	private final T element;
	private boolean hasNext = true;
	
	public SingleElementIterator(T element) {
		this.element = element;
	}
	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if(hasNext){
			hasNext = false;
			return element;
		}
		throw new NoSuchElementException();
	}

}
