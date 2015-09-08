/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;


import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannel;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannelGroup;
import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.AbstractBasesSectionCodec;
import org.jcvi.jillion.trace.chromat.ChannelGroup;


public abstract class AbstractTestBasesSection {
    protected static final String DECODED_BASES = "ACGTACGT";
    
    protected NucleotideSequence encodedBases = new NucleotideSequenceBuilder(DECODED_BASES).build();
    protected SCFHeader mockHeader;
    protected ScfChromatogramImpl chromatogram;
    protected byte[] calledConfidence = new byte[]{40,40,40,40,63,38,38,38};
    protected QualitySequence encodedQualities =  new QualitySequenceBuilder(calledConfidence).build();
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
                new DefaultChannel(aConfidence,positions),
                new DefaultChannel(cConfidence,positions),
                new DefaultChannel(gConfidence,positions),
                new DefaultChannel(tConfidence,positions));
        BasicChromatogram basicChromatogram = new BasicChromatogram(
                "id",encodedBases,encodedQualities,
                new PositionSequenceBuilder(peaks).build(),
                channelGroup);
        chromatogram = new ScfChromatogramImpl(basicChromatogram);

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
    public ScfChromatogramImpl getChromatogram() {
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

        chromatogram = new ScfChromatogramImpl(chromatogram,
                                        new QualitySequenceBuilder(subsitutionConfidence).build(),
                                        new QualitySequenceBuilder(insertionConfidence).build(),
                                        new QualitySequenceBuilder(deletionConfidence).build(),null
                                        );
    }
    protected void removeSubstitutionConfidence() {
        chromatogram = new ScfChromatogramImpl(chromatogram,
                null,
                new QualitySequenceBuilder(insertionConfidence).build(),
                new QualitySequenceBuilder(deletionConfidence).build(),null
                );

    }
    protected void removeInsertionConfidence() {
        chromatogram = new ScfChromatogramImpl(chromatogram,
                new QualitySequenceBuilder(subsitutionConfidence).build(),
                null,
                new QualitySequenceBuilder(deletionConfidence).build(),null
                );

    }
    protected void removeDeletionConfidence() {
        chromatogram = new ScfChromatogramImpl(chromatogram,
                new QualitySequenceBuilder(subsitutionConfidence).build(),
                new QualitySequenceBuilder(insertionConfidence).build(),
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
