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
/*
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi;
/**
 * {@code IdConverter} converts an Id of
 * one type into the Id of another
 * ( which might be a different type)
 * that represents the same thing.
 * <p>
 * For Example, IdConverter can be used
 * to map ids to external ids used by a different
 * system.
 * 
 * @author dkatzel
 *
 *
 */
public interface IdConverter<K,V> {
    /**
     * Converts the given id into 
     * its corresponding other id.
     * @param id the id to convert.
     * @return another id that represents the same object.
     */
    V convertId(K id);
}
