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
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.List;
/**
 * {@code SymbolFactory} abstracts
 * how a Symbol is created so that implementations
 * may cache often used values or use singletons.
 * @author dkatzel
 *
 *
 */
interface SymbolFactory<T extends Symbol, V> {
    /**
     * Get the corresponding Symbol for the given value
     * @param value the value to get the symbol of.
     * @return the Symbol instance, should not be null.
     */
    T getSymbolFor(V value);
    /**
     * Get a list of corresponding {@link Symbol}s for the given values
     * @param values the value to get the symbol of.
     * @return the Symbol instance, should not be null.
     */
    List<T> getSymbolsFor(List<V> values);
}
