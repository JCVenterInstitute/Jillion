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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdParser;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdWriter;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdWriter extends AbstractTestPhd{
    private String id = "1095595674585";
    @Test
    public void write() throws IOException, DataStoreException{
        Phd phd = new DefaultPhd(
        		id,
                new DefaultNucleotideSequence(expectedBasecalls), 
                new EncodedQualitySequence(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, expectedQualities), 
                new SangerPeak(expectedPositions),
                expectedProperties);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PhdWriter.writePhd(phd, out);
        DefaultPhdFileDataStore expected = new DefaultPhdFileDataStore(RESOURCE.getFile(PHD_FILE));
        DefaultPhdFileDataStore actual = new DefaultPhdFileDataStore();
        PhdParser.parsePhd(new ByteArrayInputStream(out.toByteArray()), actual);
        assertEquals(expected.get(id),actual.get(id));
    }
}
