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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf.section.impl;

import java.io.DataInputStream;

import org.jcvi.common.core.seq.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.impl.DefaultSCFHeader;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.impl.SCFHeader;
/**
 * <code>SectionDecoder</code> is an interface that parses
 * a section of the SCF Data stream.
 * @author dkatzel
 *
 *
 */
public interface SectionDecoder {
    /**
     * Decode a {@link Section} of the SCF Data Stream, will set any Section specific
     * data in the given {@link DefaultSCFHeader} and {@link ScfChromatogram}.
     * @param in SCF Data Stream.
     * @param currentOffset the current offset into the SCF DataStream.
     * @param header the {@link DefaultSCFHeader} that explains where the Section data is for this
     * stream.  May be modified during method call to set new fields.
     * @param c SCFChromatogramStruct where newly decoded data will be set.  Will be modified.
     * @return the new currentOffset after decoding of this {@link Section} is complete.
     * @throws SectionDecoderException if there are any problems decoding this {@link Section}.
     */
    long decode(DataInputStream in, long currentOffset,SCFHeader header, ScfChromatogramBuilder c) throws SectionDecoderException;

    /**
     * Decode a {@link Section} of the SCF Data Stream, will set any Section specific
     * data in the given {@link DefaultSCFHeader} and {@link ScfChromatogram}.
     * @param in SCF Data Stream.
     * @param currentOffset the current offset into the SCF DataStream.
     * @param header the {@link DefaultSCFHeader} that explains where the Section data is for this
     * stream.  May be modified during method call to set new fields.
     * @param c ChromatogramFileVisitor whose visitXXX methods will be called.
     * @return the new currentOffset after decoding of this {@link Section} is complete.
     * @throws SectionDecoderException if there are any problems decoding this {@link Section}.
     */
    long decode(DataInputStream in, long currentOffset,SCFHeader header, ChromatogramFileVisitor c) throws SectionDecoderException;
}
