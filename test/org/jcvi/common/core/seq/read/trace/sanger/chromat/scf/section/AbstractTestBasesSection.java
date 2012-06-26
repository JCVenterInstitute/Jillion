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
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;


import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Channel;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.DefaultChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.DefaultConfidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramImpl;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.AbstractBasesSectionCodec;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;


public abstract class AbstractTestBasesSection {
    protected static final String DECODED_BASES = "ACGTACGT";
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);

    protected NucleotideSequence encodedBases = new NucleotideSequenceBuilder(DECODED_BASES).build();
    protected SCFHeader mockHeader;
    protected SCFChromatogramImpl chromatogram;
    protected byte[] calledConfidence = new byte[]{40,40,40,40,63,38,38,38};
    protected QualitySequence encodedQualities = new EncodedQualitySequence(RUN_LENGTH_CODEC, 
            PhredQuality.valueOf(calledConfidence));
    protected byte[] aConfidence = new byte[]{40,3,4,2,38,0,2,1};
    protected byte[] cConfidence = new byte[]{0,40,3,4,2,38,0,2};
    protected byte[] gConfidence = new byte[]{1,1,40,3,4,2,38,0};
    protected byte[] tConfidence = new byte[]{0,2,1,40,3,4,2,38};
    protected short[] peaks = new short[]{10,20,30,40,50,58,69,80};

    protected byte[] insertionConfidence = new byte[]{2,3,4,5,4,3,2,1};
    protected byte[] deletionConfidence = new byte[]{40,20,30,5,9,1,2,0};
    protected byte[] subsitutionConfidence = new byte[]{6,5,12,31,6,0,8,60};

    protected byte[] EMPTY_CONFIDENCE = new byte[]{0,0,0,0,0,0,0,0};
    protected short[] positions = new short[]{10,20,30,40,50,60,70,80};
    protected AbstractBasesSectionCodec sut;

    public AbstractTestBasesSection(){
        mockHeader = createMock(SCFHeader.class);
        ChannelGroup channelGroup = new DefaultChannelGroup(
                new Channel(aConfidence,positions),
                new Channel(cConfidence,positions),
                new Channel(gConfidence,positions),
                new Channel(tConfidence,positions));
        BasicChromatogram basicChromatogram = new BasicChromatogram(
                "id",encodedBases,encodedQualities,
                new SangerPeak(peaks),channelGroup);
        chromatogram = new SCFChromatogramImpl(basicChromatogram);

        sut = createAbstractBasesSectionHandler();
    }
    protected abstract AbstractBasesSectionCodec createAbstractBasesSectionHandler() ;

    public AbstractBasesSectionCodec getHandler(){
        return sut;
    }
    /**
     * @return the bases
     */
    public Sequence<Nucleotide> getEncodedBases() {
        return encodedBases;
    }

    /**
     * @return the chromatogram
     */
    public SCFChromatogramImpl getChromatogram() {
        return chromatogram;
    }
    /**
     * @return the mockHeader
     */
    public SCFHeader getMockHeader() {
        return mockHeader;
    }

    protected  void expectPeakReads(InputStream mockInputStream)
    throws IOException {
        for(int i=0; i< peaks.length; i++){
             short peak = peaks[i];
             //each peak is being read as an int
             //so we have 4 calls to read() per peak
             expectPeakRead(mockInputStream, peak);
         }
    }
    protected void expectPeakRead(InputStream mockInputStream, short peak)
            throws IOException {
        expect(mockInputStream.read()).andReturn(0);
         expect(mockInputStream.read()).andReturn(0);
         expect(mockInputStream.read()).andReturn(peak<< 8 & 0xFF );
         expect(mockInputStream.read()).andReturn(peak & 0xFF );
    }
    protected void addOptionalConfidences(){

        chromatogram = new SCFChromatogramImpl(chromatogram,
                                        new DefaultConfidence(subsitutionConfidence),
                                        new DefaultConfidence(insertionConfidence),
                                        new DefaultConfidence(deletionConfidence),null
                                        );
    }
    protected void removeSubstitutionConfidence() {
        chromatogram = new SCFChromatogramImpl(chromatogram,
                null,
                new DefaultConfidence(insertionConfidence),
                new DefaultConfidence(deletionConfidence),null
                );

    }
    protected void removeInsertionConfidence() {
        chromatogram = new SCFChromatogramImpl(chromatogram,
                new DefaultConfidence(subsitutionConfidence),
                null,
                new DefaultConfidence(deletionConfidence),null
                );

    }
    protected void removeDeletionConfidence() {
        chromatogram = new SCFChromatogramImpl(chromatogram,
                new DefaultConfidence(subsitutionConfidence),
                new DefaultConfidence(insertionConfidence),
                null,null
                );

    }

    protected void bulkPutAsInts(ByteBuffer result, short[] shorts) {
        for(int i=0; i< shorts.length; i++){
            result.putInt(shorts[i]);
        }
    }

    protected abstract ByteBuffer createRequiredExpectedEncodedBases();

    protected abstract ByteBuffer createEncodedBasesWithAllOptionalData() ;

    protected abstract ByteBuffer createEncodedBasesWithoutSubstutionData();

    protected abstract ByteBuffer createEncodedBasesWithoutDeletionData() ;

    protected abstract ByteBuffer createEncodedBasesWithoutInsertionData() ;


    protected abstract InputStream createValidMockInputStreamWithoutOptionalConfidence(long skipDistance)
    throws IOException ;

    protected abstract InputStream createValidMockInputStreamWithOptionalConfidence(long skipDistance)
    throws IOException ;
}
