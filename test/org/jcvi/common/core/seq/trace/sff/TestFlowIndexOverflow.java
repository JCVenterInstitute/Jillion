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
 * Created on Feb 23, 2009,*
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sff.DefaultSffFileDataStore;
import org.jcvi.common.core.seq.trace.sff.SffFlowgram;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;
/**
 * In rare cases the index of a flow is more than 127, 
 * this will cause an overflow since java uses signed bytes
 * the given sff file has a read that has such a condition,
 * this test makes sure decoding is done correctly.
 * @author dkatzel
 *
 *
 */
public class TestFlowIndexOverflow{

    private static final String FILE = "files/indexOverflow.sff";
    
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestFlowIndexOverflow.class);
   
    private final SffFlowgram FCPRO0N01A48YO = new SffFlowgram("FCPRO0N01A48YO",
    		new NucleotideSequenceBuilder(
                      "TCAGCGATACACATAGCGCGTACATCCACATCGTGGCGTCTCAAGGCACACAGGGGGATAGGN").build(),
                      new QualitySequenceBuilder(new byte[]{36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,38,36,38,38,38,37,36,36,34,33,33,31,36,36,31,31,31,31,31,31,23,23,23,23,31,36,37,35,31,26,20,20,35,35,35,36,36,36,36,36,0}).build(),
                        new short[]{ 101, 101, 102, 103, 106, 109, 87, 106,  103, 102, 103, 99, 101, 98, 101, 96, 95, 103,  114, 106, 103, 105, 96, 100, 94, 188, 101,  95, 110, 110, 123, 66, 95, 194, 86, 113, 68, 73, 110,  121, 237, 240, 84, 96, 80, 102, 109, 56, 484, 102, 110, 103, 222, 8},
                        Range.of(CoordinateSystem.RESIDUE_BASED, 25,62),
                Range.of(CoordinateSystem.RESIDUE_BASED,0,0)
        );
    
    @Test
    public void validDecode() throws TraceDecoderException, DataStoreException, IOException{
        FlowgramDataStore dataStore = DefaultSffFileDataStore.create(RESOURCES.getFile(FILE));
        assertEquals(1, dataStore.getNumberOfRecords());
        
        Flowgram actual =dataStore.get("FCPRO0N01A48YO");
        assertEquals(FCPRO0N01A48YO, actual);
    }

}
