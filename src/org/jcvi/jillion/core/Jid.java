/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;
/**
 * {@code Jid} is an object that 
 * represents some kind of Id.
 * 
 * @author dkatzel
 *
 */
public interface Jid extends Comparable<Jid>{

	/**
	 * Return this Id as a String.
	 * @return This Jid as a String.
	 */
	@Override
	String toString();
	/**
	 * Two JIds are equal if they
	 * have the same String representation.
	 * @param obj
	 * @return
	 */
	@Override
	boolean equals(Object obj);
	/**
	 * Returns the same hashcode
	 * as if this Id were a String.
	 * @return
	 */
	@Override
	int hashCode();

}
