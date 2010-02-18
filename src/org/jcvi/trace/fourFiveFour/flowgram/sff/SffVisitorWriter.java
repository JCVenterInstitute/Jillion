/*
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.io.IOUtil;

public class SffVisitorWriter implements SffFileVisitor{

    private final OutputStream out;
    private static final byte[] SFF_MAGIC_NUMBER = new byte[]{
        0x2E,
        0x73,
        0x66,
        0x66,
        0,
        0,
        0,
        1
    };
    /**
     * Currently SFF only has 1 format code which has a value of <code>1</code>.
     */
    private static final byte FORMAT_CODE = 1;
    
    private static final byte[] EMPTY_CLIP = new byte[]{0,0,0,0};
    /**
     * @param out
     */
    public SffVisitorWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public boolean visitCommonHeader(SFFCommonHeader header) {
        try {         
            int keyLength = header.getKeySequence().length();
            int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
            int padding =SFFUtil.caclulatePaddedBytes(size);
            int headerLength = size+padding;
            out.write(SFF_MAGIC_NUMBER);
            out.write(IOUtil.convertUnsignedLongToByteArray(header.getIndexOffset()));
            out.write(IOUtil.convertUnsignedIntToByteArray(header.getIndexLength()));
            out.write(IOUtil.convertUnsignedIntToByteArray(header.getNumberOfReads()));
            out.write(IOUtil.convertUnsignedShortToByteArray(headerLength));
            out.write(IOUtil.convertUnsignedShortToByteArray(keyLength));
            out.write(IOUtil.convertUnsignedShortToByteArray(header.getNumberOfFlowsPerRead()));
            out.write(FORMAT_CODE);
            out.write(header.getFlow().getBytes());
            out.write(header.getKeySequence().getBytes());
            out.write(new byte[padding]);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("error writing Sff Common Header ",e);
        }
        return true;
        
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
        final short[] flowgramValues = readData.getFlowgramValues();
        ByteBuffer flowValues= ByteBuffer.allocate(flowgramValues.length*2);
        for(int i=0; i<flowgramValues.length; i++){
            flowValues.putShort(flowgramValues[i]);
        }
        try {
            out.write(flowValues.array());
            out.write(readData.getFlowIndexPerBase());
            final String basecalls = readData.getBasecalls();
            out.write(basecalls.getBytes());
            out.write(readData.getQualities());
            int readDataLength = SFFUtil.getReadDataLength(flowgramValues.length, basecalls.length());
            int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
            out.write(new byte[padding]);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("error writing Sff read Data ",e);
        }
        return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
       try {
        final short headerLength = readHeader.getHeaderLength();
        out.write(IOUtil.convertUnsignedShortToByteArray(headerLength));
        String name =readHeader.getName();
        final int nameLength = name.length();
        out.write(IOUtil.convertUnsignedShortToByteArray(nameLength));
        out.write(IOUtil.convertUnsignedIntToByteArray(readHeader.getNumberOfBases()));
        writeClip(readHeader.getQualityClip());
        writeClip(readHeader.getAdapterClip());
        out.write(name.getBytes());
        int padding = headerLength - (16+nameLength);
        out.write(new byte[padding]);
        out.flush();
    } catch (IOException e) {
        throw new IllegalStateException("error writing Sff read header ",e);
    }
    return true;
        
    }

    private void writeClip(Range clip) throws IOException{
        if(clip==null){
            out.write(EMPTY_CLIP);
         }
         else{
             Range oneBasedClip = clip.convertRange(CoordinateSystem.RESIDUE_BASED);
        
            out.write(IOUtil.convertUnsignedShortToByteArray((int)oneBasedClip.getLocalStart()));
            out.write(IOUtil.convertUnsignedShortToByteArray((int)oneBasedClip.getLocalEnd()));
        }
        
    }
    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

}
