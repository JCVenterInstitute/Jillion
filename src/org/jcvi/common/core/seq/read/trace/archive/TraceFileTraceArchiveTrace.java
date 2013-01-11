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
package org.jcvi.common.core.seq.read.trace.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.trace.sanger.SangerTrace;
import org.jcvi.common.core.seq.trace.sanger.SangerTraceParser;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TraceFileTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    private final SangerTrace trace;
    public TraceFileTraceArchiveTrace(TraceArchiveRecord record,
            String rootDirPath) {
        super(record, rootDirPath);
        InputStream inputStream =null;
        try {
        	File f =getFile();
            inputStream = new FileInputStream(f);
            trace = SangerTraceParser.INSTANCE.decode(f.getName(),inputStream);
        } catch (Exception e) {
           throw new IllegalArgumentException("invalid trace file",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

    

    @Override
	public PositionSequence getPositionSequence() {
		return trace.getPositionSequence();
	}



	@Override
    public NucleotideSequence getNucleotideSequence() {
        return trace.getNucleotideSequence();
    }

    @Override
    public QualitySequence getQualitySequence() {
        return trace.getQualitySequence();
    }

}
