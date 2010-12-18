/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.util;

import java.util.Comparator;
/**
 * {@code InverseComparator} will invert the results
 * of a {@link Comparator}. 
 * @author dkatzel
 *
 * @param <T>
 * @param <C>
 */
public final class InverseComparator<T extends Comparable,C extends Comparator<T>> implements Comparator<T>{
	/**
	 * Create a new Comparator which will invert the results
	 * of the given {@link Comparator}. 
	 * @author dkatzel
	 *
	 * @param <T> the Object being compared.
	 * @param <C> the {@link Comparator} implementation to invert.
	 */
	public static <T extends Comparable,C extends Comparator<T>> Comparator<T> invert(C comparator){
		return new InverseComparator<T, C>(comparator);
	}
	private InverseComparator(Comparator<T> delegate) {
		super();
		this.delegate = delegate;
	}

	private final Comparator<T> delegate;

	@Override
	public int compare(T o1, T o2) {
		return delegate.compare(o2, o1);
	}
	
	
}
