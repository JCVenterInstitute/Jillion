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
 * Created on Jan 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.SCFChromatogramFile;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFChromatogramImpl;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFCodec;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFCodecs;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogramFile;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestConvertZtr2Scf {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestConvertZtr2Scf.class);
    SCFCodec scfCodec = SCFCodecs.VERSION_3;
    
    @Test
    public void ztr2scf() throws TraceDecoderException, IOException{
        
        Chromatogram decodedZTR = ZTRChromatogramFile.create(
                RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.write(new SCFChromatogramImpl(decodedZTR), out);
        
        Chromatogram encodedScf = SCFChromatogramFile.create("id",new ByteArrayInputStream(out.toByteArray()));
        assertEquals(decodedZTR, encodedScf);
    }
    
    @Test
    public void scfequalsZtr() throws TraceDecoderException, IOException{
        Chromatogram decodedScf = SCFChromatogramFile.create("id",
        		RESOURCES.getFileAsStream("scf/files/GBKAK82TF.scf"));
        Chromatogram decodedZTR = ZTRChromatogramFile.create(
                RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"));
        assertEquals(decodedZTR, decodedScf);        
    }
    /**
     * ZTR files can have no qualities (ex: trash data)
     * but SCF requires the same # of qualities as basecalls
     * so just set them to 0.
     * @throws IOException 
     * @throws TraceDecoderException 
     */
    @Test
    public void ztrWithNoQualitiesShouldGetPaddedQualitiesInScf() throws TraceDecoderException, IOException{
        Chromatogram ztr = ZTRChromatogramFile.create(
                RESOURCES.getFile("ztr/files/515866_G07_AFIXF40TS_026.ab1.afg.trash.ztr"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.write(new SCFChromatogramImpl(ztr), out);
        
        Chromatogram encodedScf = SCFChromatogramFile.create("id",new ByteArrayInputStream(out.toByteArray()));
        
        int numberOfBases = (int)encodedScf.getNucleotideSequence().getLength();
        QualitySequence expectedQualities = new QualitySequenceBuilder(new byte[numberOfBases]).build();
        
        assertEquals(expectedQualities,encodedScf.getQualitySequence());
    }
    
    
}
