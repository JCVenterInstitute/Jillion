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

package org.jcvi.assembly.tasm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.assembly.DefaultPlacedRead;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultTigrAssemblerPlacedRead extends DefaultPlacedRead implements TigrAssemblerPlacedRead{

    private final Map<TigrAssemblerReadAttribute,String> attributes;
    /**
     * @param read
     * @param start
     * @param sequenceDirection
     */
    public DefaultTigrAssemblerPlacedRead(
            Read<ReferencedEncodedNucleotideGlyphs> read, long start,
            SequenceDirection sequenceDirection) {
        this(read, start, sequenceDirection,new EnumMap<TigrAssemblerReadAttribute,String>(TigrAssemblerReadAttribute.class));
    }
    /**
     * @param read
     * @param start
     * @param sequenceDirection
     */
    public DefaultTigrAssemblerPlacedRead(
            Read<ReferencedEncodedNucleotideGlyphs> read, long start,
            SequenceDirection sequenceDirection, EnumMap<TigrAssemblerReadAttribute, String> attributes) {
        super(read, start, sequenceDirection);
        Map<TigrAssemblerReadAttribute, String> map = new EnumMap<TigrAssemblerReadAttribute, String>(attributes);
        this.attributes = Collections.unmodifiableMap(map);
    }
    @Override
    public Map<TigrAssemblerReadAttribute, String> getAttributes() {
        return attributes;
    }
	@Override
	public String getAttributeValue(TigrAssemblerReadAttribute attribute) {
		if(!hasAttribute(attribute)){
			throw new NoSuchElementException("read does not contain attribute "+attribute);
		}
		return attributes.get(attribute);
	}
	@Override
	public boolean hasAttribute(TigrAssemblerReadAttribute attribute) {
		return attributes.containsKey(attribute);
	}
    

}
