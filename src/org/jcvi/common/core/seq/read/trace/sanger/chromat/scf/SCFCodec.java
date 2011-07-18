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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.common.core.seq.read.trace.sanger.SangerTrace;
import org.jcvi.common.core.seq.read.trace.sanger.SangerTraceCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramWriter;

/**
 * <code>SCFCodec</code> is used to encode and decode {@link SCFChromatogram}s.
 * @author dkatzel
 *
 *
 */
public interface SCFCodec extends SangerTraceCodec, ChromatogramWriter{
    /**
     * Encodes the given {@link SangerTrace} into SCF Format
     * and writes the encoded data to the given {@link OutputStream}.
     * @param chromatogram the {@link SangerTrace} to encode.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the chromatogram
     * or any problems writing to the {@link OutputStream}.
     */
    void encode(SangerTrace chromatogram, OutputStream out) throws IOException;
    /**
     * Decodes the given SCF Data Stream into an {@link SCFChromatogram}.
     * @param in the {@link InputStream} which has the SCF Data.
     * @return a {@link SCFChromatogram}.
     * @throws SCFDecoderException if there are any problems decoding
     * the SCF Data.
     */
    SCFChromatogram decode(InputStream in) throws SCFDecoderException;
    
    void parse(InputStream in, ChromatogramFileVisitor visitor) throws SCFDecoderException;
    void parse(File scfFile, ChromatogramFileVisitor visitor) throws IOException,SCFDecoderException;
}
