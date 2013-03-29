/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jul 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.jcvi.jillion.trace.sanger.SangerTrace;
import org.jcvi.jillion.trace.sanger.SangerTraceParser;

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
