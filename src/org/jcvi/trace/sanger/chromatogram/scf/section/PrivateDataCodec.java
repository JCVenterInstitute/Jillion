/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.scf.PrivateData;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
/**
 * <code>PrivateDataCodec</code> is the SectionCodec implementation
 * that will encode/ decode the {@link PrivateData} of an {@link SCFChromatogramImpl}.
 * @author dkatzel
 *
 *
 */
public class PrivateDataCodec implements SectionCodec{

    private static final byte[] EMPTY = new byte[0];
    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        PrivateData privateData=c.getPrivateData();
        if(privateData ==null|| privateData.getData()==null){
            header.setPrivateDataSize(0);
            return new EncodedSection( ByteBuffer.wrap(EMPTY),Section.PRIVATE_DATA);
        }

        final byte[] rawArray = privateData.getData().array();
        header.setPrivateDataSize(rawArray.length);
        return new EncodedSection( ByteBuffer.wrap(rawArray),
                Section.PRIVATE_DATA);
    }

    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, SCFChromatogramBuilder c) throws SectionDecoderException {
        long bytesToSkip = header.getPrivateDataOffset() - currentOffset;
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            final int privateDataSize = header.getPrivateDataSize();
            if(privateDataSize ==0){
                c.privateData(null);
            }
            else{
                byte[] privateData = new byte[privateDataSize];
                int bytesRead = in.read(privateData);
                if(bytesRead != privateDataSize){
                    throw new SectionDecoderException("could not read entire private data section");
                }
                c.privateData(privateData);
            }
            return currentOffset+bytesToSkip+privateDataSize;
        } catch (IOException e) {
           throw new SectionDecoderException("error trying to decode Private Data",e);
        }
    }

}
