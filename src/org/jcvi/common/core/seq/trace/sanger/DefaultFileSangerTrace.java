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
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger;

import java.io.File;
import java.io.IOException;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class DefaultFileSangerTrace implements FileSangerTrace {

    private final SangerTrace trace;
    private final File file;
    
    /**
     * @param trace
     * @param file
     */
    public DefaultFileSangerTrace(SangerTrace trace, File file) {
        this.trace = trace;
        this.file = file;
    }

    @Override
	public String getId() {
		return trace.getId();
	}

	@Override
    public File getFile() throws IOException {
        return file;
    }

    @Override
    public int getNumberOfTracePositions() {
        return trace.getNumberOfTracePositions();
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
