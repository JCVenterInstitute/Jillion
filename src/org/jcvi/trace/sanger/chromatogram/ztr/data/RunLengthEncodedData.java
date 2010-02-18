/*
 * Created on Oct 30, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.glyph.RunLength;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRUtil;



/**
 * <code>RunLengthEncodedData</code> replaces 
 * a series of N identical bytes of value V with the guard byte G followed
 * by N and V. Non-series are stored as normal unless it the value
 * happens to be the guard byte, which is stored G 0.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class RunLengthEncodedData implements Data {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {
       //read uncompressed length
        int uncompressedLength = computeUncompressedLength(data);
        ByteBuffer in = ByteBuffer.wrap(data);
        in.position(5);
       //read guard
        byte guard = in.get();
        ByteBuffer out = ByteBuffer.allocate(uncompressedLength);
        parse(in, guard, out);
        return out.array();
    }

    private void parse(ByteBuffer in, byte guard, ByteBuffer out) {
        List<RunLength<Byte>> runLengthList = parseIntoRunLength(in, guard);        
        write(runLengthList,out);
    }

    private List<RunLength<Byte>> parseIntoRunLength(ByteBuffer in, byte guard) {
        List<RunLength<Byte>> runLengthList = new ArrayList<RunLength<Byte>>();
        while(in.hasRemaining()){
            byte value = in.get();
            //if not guard, just output token
            if( value != guard){                                  
                runLengthList.add(new RunLength<Byte>(Byte.valueOf(value),1));
            }
            //else it is the guard token
            else{
                int count = getCount(in);
                if(count !=0){
                    byte repValue = in.get();                   
                    runLengthList.add(new RunLength<Byte>(Byte.valueOf(repValue),count));
                }
                else{
                    //count is 0 so guard byte must be actual value.
                    runLengthList.add(new RunLength<Byte>(Byte.valueOf(guard),1));
                }
            }
        }
        return runLengthList;
    }

    private void write(List<RunLength<Byte>> runLengthList, ByteBuffer out) {
        for(RunLength<Byte> runLength : runLengthList){
            putConsecutiveValues(out, runLength.getLength(), runLength.getValue().byteValue());
        }
        
    }

    private void putConsecutiveValues(ByteBuffer out, int count, byte repValue) {
        byte[] consecutiveValues = new byte[count];
        Arrays.fill(consecutiveValues, repValue);
        out.put(consecutiveValues);
    }

    private int computeUncompressedLength(byte[] data) {
        byte uncompressedLengthArray[] = new byte[4];
        /* dkatzel :
         * uncompressed length is wrong endian in IO_Lib source code
         * compared to the 1.2 ZTR spec but I am going to use IO_Lib version 
         * because it seems to work.
         */
        for(int i=1; i< 5; i++){
            uncompressedLengthArray[4-i] = data[i];
        }        
        return (int)ZTRUtil.readInt(uncompressedLengthArray);
    }

    private int getCount(ByteBuffer in) {
        return IOUtil.convertToUnsignedByte(in.get());
    }
}
