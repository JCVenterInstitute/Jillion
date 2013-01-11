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

package org.jcvi.common.core.seq.trace.sanger.chromat.scf;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.SCFCodecs;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestVersion2Parser {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestVersion2Parser.class);
    
    @Test
    public void version2MatchesVersion3() throws TraceDecoderException, FileNotFoundException, IOException{
        Chromatogram version2 = (Chromatogram) SCFCodecs.VERSION_2.decode(RESOURCES.getFile("files/version2.scf"));
        Chromatogram version3 = (Chromatogram) SCFCodecs.VERSION_3.decode(RESOURCES.getFile("files/version3.scf"));
        assertEquals(version3.getNucleotideSequence(),version2.getNucleotideSequence());
        assertEquals(version3.getQualitySequence(),version2.getQualitySequence());
        assertEquals(version3.getPositionSequence(),version2.getPositionSequence());
        assertEquals(version3.getNumberOfTracePositions(), version2.getNumberOfTracePositions());
    
        assertEquals(version3.getChannelGroup(), version2.getChannelGroup());
    }
}
