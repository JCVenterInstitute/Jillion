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
 * Created on Nov 6, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;


import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;


/**
 * The <code>CNF4Chunk</code> Chunk encodes the quality values for
 * all 4 channels.  The format of the data is:
 *  the confidence of the called
 * base followed by the confidences of the uncalled bases.
 * So for a sequence AGT we would store confidences 
 * A1 G2 T3 C1 G1 T1 A2 C2 T2 A3 C3 G3. Any call that is not A, C, G or T
 * is stored as a T.
 *
 * @author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class CNF4Chunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws TraceDecoderException {
        String basecalls = builder.basecalls();
        int numberOfBases = basecalls.length();
           
        ByteBuffer aConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer cConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer gConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer tConfidence = ByteBuffer.allocate(numberOfBases);
           
        ByteBuffer calledConfidence = ByteBuffer.wrap(unEncodedData);       
        ByteBuffer unCalledConfidence = calledConfidence.slice();
        //skip padding
        calledConfidence.position(1);
        unCalledConfidence.position(1+numberOfBases);
        populateConfidenceBuffers(basecalls, aConfidence, cConfidence,
                gConfidence, tConfidence, calledConfidence, unCalledConfidence);
           
        builder.aConfidence(aConfidence.array());
        builder.cConfidence(cConfidence.array());
        builder.gConfidence(gConfidence.array());
        builder.tConfidence(tConfidence.array());

    }

    private void populateConfidenceBuffers(String basecalls,
            ByteBuffer aConfidence, ByteBuffer cConfidence,
            ByteBuffer gConfidence, ByteBuffer tConfidence,
            ByteBuffer calledConfidence, ByteBuffer unCalledConfidence) {
        for (int i = 0; i < basecalls.length(); i++) {
            char currentChar = basecalls.charAt(i);
            populateConfidenceBuffers(currentChar, aConfidence, cConfidence,
                    gConfidence, tConfidence, calledConfidence,
                    unCalledConfidence);
        }
    }

    private void populateConfidenceBuffers(char currentChar,
            ByteBuffer aConfidence, ByteBuffer cConfidence,
            ByteBuffer gConfidence, ByteBuffer tConfidence,
            ByteBuffer calledConfidence, ByteBuffer unCalledConfidence) {
        if(matchesCharacterIgnoringCase(currentChar, 'A')){
            setConfidences(calledConfidence, unCalledConfidence, 
                    aConfidence, Arrays.asList(cConfidence, gConfidence, tConfidence));
        }
        else  if(matchesCharacterIgnoringCase(currentChar, 'C')){
           setConfidences(calledConfidence, unCalledConfidence, 
                   cConfidence, Arrays.asList(aConfidence, gConfidence, tConfidence));
        }
        else  if(matchesCharacterIgnoringCase(currentChar, 'G')){
               setConfidences(calledConfidence, unCalledConfidence, 
                       gConfidence, Arrays.asList(aConfidence, cConfidence, tConfidence));
        }
        //anything is is considered a "T"
        else{
               setConfidences(calledConfidence, unCalledConfidence, 
                       tConfidence, Arrays.asList(aConfidence, cConfidence, gConfidence));
        }
    }
    
    private boolean matchesCharacterIgnoringCase(char c, char charToMatch){
        return Character.toLowerCase(c) == charToMatch ||
            Character.toUpperCase(c) == charToMatch;
    }

    private static void setConfidences(ByteBuffer calledConfidence, ByteBuffer unCalledConfidence,
            ByteBuffer calledconfidenceChannel, List<ByteBuffer> uncalledConfidenceChannels){
        calledconfidenceChannel.put(calledConfidence.get());
        for(ByteBuffer uncalledBuf : uncalledConfidenceChannels){
            uncalledBuf.put(unCalledConfidence.get());
        }
    }
}
