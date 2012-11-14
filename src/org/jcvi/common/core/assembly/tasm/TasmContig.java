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

package org.jcvi.common.core.assembly.tasm;

import java.util.Map;

import org.jcvi.common.core.assembly.Contig;

/**
 * {@code TasmContig} is a {@link Contig}
 * that contains extra metadata attributes specific
 * to the TigrAssembler format.
 * @author dkatzel
 *
 *
 */
public interface TasmContig extends Contig<TasmAssembledRead>{
    /**
     * Get the Tigr Assembler properties associated with this
     * Contig.
     * @return a non-null {@link Map} containing all
     * the attributes of this contig as Key Value pairs.
     */
    Map<TasmContigAttribute,String> getAttributes();
    /**
     * Does this {@link TasmContig} contain a value for
     * the given
     * {@link TasmContigAttribute}.
     * @param attribute the attribute to check;
     * can not be null.
     * @return {@code true} if this {@link TasmContig}
     * does have this attribute, {@code false}
     * otherwise.
     * @throws NullPointerException if attribute is null.
     */
    boolean hasAttribute(TasmContigAttribute attribute);
    /**
     * Get the value of the specified
     *  {@link TasmContigAttribute} from
     *  this {@link TasmContig}.
     * @param attribute the attribute whose value to get.
     * @return the value for this attribute
     * if this {@link TasmContig} contains this attriubte;
     * or {@code null} if the attribute does not 
     * exist for this {@link TasmContig}.
     * @see #hasAttribute(TasmContigAttribute)
     */
    String getAttributeValue(TasmContigAttribute attribute);
}
