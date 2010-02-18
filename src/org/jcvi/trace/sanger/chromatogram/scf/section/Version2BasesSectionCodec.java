/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.sequence.Confidence;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;

public class Version2BasesSectionCodec extends AbstractBasesSectionCodec{

    @Override
    protected void readBasesData(DataInputStream in, SCFChromatogramBuilder c,
            int numberOfBases) throws IOException {
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        byte[][] probability = new byte[4][numberOfBases];
        ByteBuffer substitutionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer insertionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer deletionConfidence = ByteBuffer.allocate(numberOfBases);

        StringBuilder bases = new StringBuilder();
        populateFields(in, numberOfBases, peaks, probability,
                substitutionConfidence, insertionConfidence,
                deletionConfidence, bases);
        setConfidences(c, probability)
        .substitutionConfidence(substitutionConfidence.array())
        .insertionConfidence(insertionConfidence.array())
        .deletionConfidence(deletionConfidence.array())
        .peaks(peaks.array())
        .basecalls(bases.toString());


    }

    private void populateFields(DataInputStream in, int numberOfBases,
            ShortBuffer peaks, byte[][] probability,
            ByteBuffer substitutionConfidence, ByteBuffer insertionConfidence,
            ByteBuffer deletionConfidence, StringBuilder bases)
            throws IOException {
        for(int i=0; i<numberOfBases; i++){
            peaks.put((short)in.readInt());
            for(int channel =0; channel<4; channel++){
                probability[channel][i]=(byte)(in.readUnsignedByte());
            }
            bases.append((char)in.readUnsignedByte());
            substitutionConfidence.put((byte)(in.readUnsignedByte()));
            insertionConfidence.put((byte)(in.readUnsignedByte()));
            deletionConfidence.put((byte)(in.readUnsignedByte()));
        }
        peaks.flip();
        substitutionConfidence.flip();
        insertionConfidence.flip();
        deletionConfidence.flip();
    }



    protected void writeBasesDataToBuffer(ByteBuffer buffer, SCFChromatogram c, int numberOfBases) {
        
        EncodedGlyphs<ShortGlyph> peaks = c.getPeaks().getData();
        final ChannelGroup channelGroup = c.getChannelGroup();
        final ByteBuffer aConfidence = ByteBuffer.wrap(channelGroup.getAChannel().getConfidence().getData());
        final ByteBuffer cConfidence = ByteBuffer.wrap(channelGroup.getCChannel().getConfidence().getData());
        final ByteBuffer gConfidence = ByteBuffer.wrap(channelGroup.getGChannel().getConfidence().getData());
        final ByteBuffer tConfidence = ByteBuffer.wrap(channelGroup.getTChannel().getConfidence().getData());

        final EncodedGlyphs<NucleotideGlyph> basecalls = c.getBasecalls();
        final ByteBuffer substitutionConfidence = getOptionalField(c.getSubstitutionConfidence());
        final ByteBuffer insertionConfidence = getOptionalField(c.getInsertionConfidence());
        final ByteBuffer deletionConfidence = getOptionalField(c.getDeletionConfidence());

        for(int i=0; i<numberOfBases; i++){
           buffer.putInt(peaks.get(i).getNumber().intValue());
           buffer.put(aConfidence.get());
           buffer.put(cConfidence.get());
           buffer.put(gConfidence.get());
           buffer.put(tConfidence.get());
           buffer.put((byte)basecalls.get(i).getCharacter().charValue());
           handleOptionalField(buffer, substitutionConfidence);
           handleOptionalField(buffer, insertionConfidence);
           handleOptionalField(buffer, deletionConfidence);
        }       
       
    }

    private ByteBuffer getOptionalField(Confidence confidence){
        
        if(confidence !=null){
            final byte[] data = confidence.getData();
            if(data != null && data.length !=0){
                return ByteBuffer.wrap(data);
            }
        }
        return ByteBuffer.allocate(0);
        
    }
    private void handleOptionalField(ByteBuffer buffer,
            final ByteBuffer optionalConfidence) {
        if(optionalConfidence.hasRemaining()){
            buffer.put(optionalConfidence.get());
        }
        else{
            buffer.put((byte)0);
        }
    }

}
