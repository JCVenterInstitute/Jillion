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
 * Created on Jul 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.InputStream;

import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.phredQuality.QualitySequence;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.SangerTraceCodec;
import org.jcvi.trace.sanger.SangerTraceParser;

public class TraceFileTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    private static final SangerTraceCodec TRACE_CODEC = SangerTraceParser.INSTANCE;
    
    private final SangerTrace trace;
    public TraceFileTraceArchiveTrace(TraceArchiveRecord record,
            String rootDirPath) {
        super(record, rootDirPath);
        InputStream inputStream =null;
        try {
            inputStream = this.getInputStreamFor(TraceInfoField.TRACE_FILE);
            trace = TRACE_CODEC.decode(inputStream);
        } catch (Exception e) {
           throw new IllegalArgumentException("invalid trace file",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

    @Override
    public Peaks getPeaks() {
        return trace.getPeaks();
    }

    @Override
    public NucleotideSequence getBasecalls() {
        return trace.getBasecalls();
    }

    @Override
    public QualitySequence getQualities() {
        return trace.getQualities();
    }

}
