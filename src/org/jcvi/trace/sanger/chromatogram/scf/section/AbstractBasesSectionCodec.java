/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;

public abstract class AbstractBasesSectionCodec implements SectionCodec{
    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            SCFChromatogramBuilder c) throws SectionDecoderException {
        long bytesToSkip = header.getBasesOffset() - currentOffset;
        int numberOfBases = header.getNumberOfBases();
        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            readBasesData(in, c, numberOfBases);
            return currentOffset+bytesToSkip + numberOfBases*12;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading bases section",e);
        }
    }

    protected abstract void readBasesData(DataInputStream in, SCFChromatogramBuilder c,
            int numberOfBases) throws IOException;

    protected static SCFChromatogramBuilder setConfidences(SCFChromatogramBuilder c, byte[][] probability) {

        return (SCFChromatogramBuilder) c.aConfidence(probability[0])
            .cConfidence(probability[1])
            .gConfidence(probability[2])
            .tConfidence(probability[3]);
    }

    protected static void setBaseCalls(SCFChromatogramBuilder c, String basecalls){
        c.basecalls(basecalls);
    }

    protected static void setPeaks(SCFChromatogramBuilder c, short[] peaks){
        c.peaks(peaks);
    }

    protected static void setSubsitutionConfidence(SCFChromatogramBuilder c,
            ByteBuffer substitutionConfidence) {
       c.substitutionConfidence(substitutionConfidence.array());
    }
    protected static void setInsertionConfidence(SCFChromatogramBuilder c,
            ByteBuffer insertionConfidence) {
       c.insertionConfidence(insertionConfidence.array());
    }
    protected static void setDeletionConfidence(SCFChromatogramBuilder c,
            ByteBuffer deletionConfidence) {
       c.deletionConfidence(deletionConfidence.array());
    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        final int numberOfBases = (int)c.getBasecalls().getLength();
        header.setNumberOfBases(numberOfBases);
        ByteBuffer buffer = ByteBuffer.allocate(numberOfBases*12);
        writeBasesDataToBuffer(buffer, c,numberOfBases);
        buffer.flip();
        return new EncodedSection(buffer, Section.BASES);
    }

    protected abstract void writeBasesDataToBuffer(ByteBuffer buffer, SCFChromatogram c,int numberOfBases);
}
