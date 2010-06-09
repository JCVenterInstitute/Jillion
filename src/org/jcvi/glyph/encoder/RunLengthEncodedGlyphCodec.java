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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.RunLength;
import org.jcvi.glyph.RunLengthEncoder;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class RunLengthEncodedGlyphCodec implements GlyphCodec<PhredQuality>{
    public static final RunLengthEncodedGlyphCodec DEFAULT_INSTANCE = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);
    
    private final byte guard;

    public RunLengthEncodedGlyphCodec( byte guard){
        this.guard = guard;
    }
    @Override
    public List<PhredQuality> decode(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int size = buf.getInt();
        byte guard = buf.get();
       
        return RunLengthEncoder.decode(decodeUpTo(buf, guard, size-1));
    }
    private List<RunLength<PhredQuality>> decodeUpTo(ByteBuffer buf, byte guard,int maxIndex) {
        //size of list initialized to 25% of max index
        //since we expect the run length to be pretty well compressed
        //tests on 454 sequences show rle takes up only 16% memory footprint
        List<RunLength<PhredQuality>> runLengthList = new ArrayList<RunLength<PhredQuality>>(maxIndex/4);
        int lengthDecoded=0;
        while(lengthDecoded<=maxIndex && buf.hasRemaining()){
            byte value = buf.get();
            //if not guard, just output token
            if( value != guard){                                  
                lengthDecoded += handleSingleToken(value, runLengthList);
            }
            else{
                lengthDecoded += handleRun(buf, guard, runLengthList);
            }
        }
        return runLengthList;
    }
    private int handleRun(ByteBuffer buf, byte guard,
            List<RunLength<PhredQuality>> runLengthList) {
        int count = buf.getShort();
        if(count !=0){
            byte repValue = buf.get();                   
            runLengthList.add(new RunLength<PhredQuality>(PhredQuality.valueOf(repValue),count));
            return count;
        }
            //count is 0 so guard byte must be actual value.
            return handleSingleToken(guard, runLengthList);
    }
    private int handleSingleToken(byte guard,
            List<RunLength<PhredQuality>> runLengthList) {
        runLengthList.add(new RunLength<PhredQuality>(PhredQuality.valueOf(guard),1));
        return 1;
    }

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, int index) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        buf.getInt();
        byte guard = buf.get();
        final List<RunLength<PhredQuality>> list = decodeUpTo(buf, guard, index);
        final PhredQuality result = RunLengthEncoder.decode(list, index);
        return result;
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int size= buf.getInt();
        return size;
    }

    @Override
    public byte[] encode(List<PhredQuality> glyphs) {
        List<RunLength<PhredQuality>> runLengthList = RunLengthEncoder.encode(glyphs);
        int size = computeSize(runLengthList);
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.putInt(glyphs.size());
        buf.put(guard);
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getNumber().byteValue() == guard){
                
                for(int repeatCount = 0; repeatCount<runLength.getLength(); repeatCount++){
                    buf.put(guard);
                    buf.putShort((byte)0);
                }
               
            }
            else{
                if(runLength.getLength() ==1){
                    buf.put(runLength.getValue().getNumber().byteValue());
                }
                else{
                    buf.put(guard);
                    buf.putShort((short)runLength.getLength());
                    buf.put(runLength.getValue().getNumber().byteValue());
                }
            }
        }
        return buf.array();
    }

    private int computeSize(List<RunLength<PhredQuality>> runLengthList) {
        int numGuards=0;
        int singletons=0;
        int nonSingletons=0;
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getNumber().byteValue() == guard){
                numGuards+=runLength.getLength();
            }
            else if(runLength.getLength() ==1){
                singletons++;
            }
            else{
                nonSingletons++;
            }
            
        }
        
        return 4+1+(numGuards *3)+ singletons+(nonSingletons *4);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + guard;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RunLengthEncodedGlyphCodec))
            return false;
        RunLengthEncodedGlyphCodec other = (RunLengthEncodedGlyphCodec) obj;
        if (guard != other.guard)
            return false;
        return true;
    }

    

}
