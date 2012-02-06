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
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSffFileDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFFlowgram;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffParser;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
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
    
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestFlowIndexOverflow.class);
    
    
   private static final RunLengthEncodedGlyphCodec runLengthQualityCodec = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);
    short[] encodedValues = new short[]{213,0,2, 97, 120};
    
    private final SFFFlowgram FCPRO0N01A48YO = new SFFFlowgram("FCPRO0N01A48YO",
    		new NucleotideSequenceBuilder(
                      "TCAGCGATACACATAGCGCGTACATCCACATCGTGGCGTCTCAAGGCACACAGGGGGATAGGN").build(),
                      new EncodedQualitySequence(runLengthQualityCodec,
                              PhredQuality.valueOf(new byte[]{36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,38,36,38,38,38,37,36,36,34,33,33,31,36,36,31,31,31,31,31,31,23,23,23,23,31,36,37,35,31,26,20,20,35,35,35,36,36,36,36,36,0})),
                        Arrays.<Short>asList((short)101,(short)101,(short)102,(short)103,(short)106,(short)109,(short)87,(short)106, (short) 103,(short)102,(short)103,(short)99,(short)101,(short)98,(short)101,(short)96,(short)95,(short)103, (short)114,(short)106,(short)103,(short)105,(short)96,(short)100,(short)94,(short)188,(short)101, (short)95,(short) 110,(short)110,(short)123,(short)66,(short)95,(short)194,(short)86,(short)113,(short)68,(short)73,(short)110, (short)121,(short)237,(short)240,(short)84,(short)96,(short)80,(short)102,(short)109,(short)56,(short)484,(short)102,(short)110,(short)103,(short)222,(short) 8),
                        Range.buildRange(CoordinateSystem.RESIDUE_BASED, 25,62),
                Range.buildRange(CoordinateSystem.RESIDUE_BASED,0,0)
        );
    
    @Test
    public void validDecode() throws TraceDecoderException, DataStoreException, IOException{
        InputStream in = RESOURCES.getFileAsStream(FILE);
        DefaultSffFileDataStore dataStore = new DefaultSffFileDataStore();
        SffParser.parseSFF(in, dataStore);

        IOUtil.closeAndIgnoreErrors(in);
        assertEquals(1, dataStore.size());
        
        Flowgram actual =dataStore.get("FCPRO0N01A48YO");
        assertEquals(FCPRO0N01A48YO, actual);
    }

}
