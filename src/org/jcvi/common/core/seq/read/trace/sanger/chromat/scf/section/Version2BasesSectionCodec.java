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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Confidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

public class Version2BasesSectionCodec extends AbstractBasesSectionCodec{

    @Override
    protected void readBasesData(DataInputStream in, SCFChromatogramBuilder c,
            int numberOfBases) throws IOException {
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        byte[][] probability = new byte[4][numberOfBases];
        ByteBuffer substitutionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer insertionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer deletionConfidence = ByteBuffer.allocate(numberOfBases);

        NucleotideSequenceBuilder bases = new NucleotideSequenceBuilder();
        populateFields(in, numberOfBases, peaks, probability,
                substitutionConfidence, insertionConfidence,
                deletionConfidence, bases);
        setConfidences(c, probability)
        .substitutionConfidence(substitutionConfidence.array())
        .insertionConfidence(insertionConfidence.array())
        .deletionConfidence(deletionConfidence.array())
        .peaks(peaks.array())
        .basecalls(bases.build());


    }

    private void populateFields(DataInputStream in, int numberOfBases,
            ShortBuffer peaks, byte[][] probability,
            ByteBuffer substitutionConfidence, ByteBuffer insertionConfidence,
            ByteBuffer deletionConfidence, NucleotideSequenceBuilder bases)
            throws IOException {
        for(int i=0; i<numberOfBases; i++){
            peaks.put((short)in.readInt());
            for(int channel =0; channel<4; channel++){
                probability[channel][i]=(byte)(in.readUnsignedByte());
            }
            bases.append(Nucleotide.parse((char)in.readUnsignedByte()));
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
        
        Sequence<ShortSymbol> peaks = c.getPeaks().getData();
        final ChannelGroup channelGroup = c.getChannelGroup();
        final ByteBuffer aConfidence = ByteBuffer.wrap(channelGroup.getAChannel().getConfidence().getData());
        final ByteBuffer cConfidence = ByteBuffer.wrap(channelGroup.getCChannel().getConfidence().getData());
        final ByteBuffer gConfidence = ByteBuffer.wrap(channelGroup.getGChannel().getConfidence().getData());
        final ByteBuffer tConfidence = ByteBuffer.wrap(channelGroup.getTChannel().getConfidence().getData());

        final Sequence<Nucleotide> basecalls = c.getBasecalls();
        final ByteBuffer substitutionConfidence = getOptionalField(c.getSubstitutionConfidence());
        final ByteBuffer insertionConfidence = getOptionalField(c.getInsertionConfidence());
        final ByteBuffer deletionConfidence = getOptionalField(c.getDeletionConfidence());

        for(int i=0; i<numberOfBases; i++){
           buffer.putInt(peaks.get(i).getValue().intValue());
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

    /**
    * {@inheritDoc}
    */
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor c)
            throws SectionDecoderException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void readBasesData(DataInputStream in, ChromatogramFileVisitor visitor,
            int numberOfBases) throws IOException {
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        byte[][] probability = new byte[4][numberOfBases];
        ByteBuffer substitutionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer insertionConfidence = ByteBuffer.allocate(numberOfBases);
        ByteBuffer deletionConfidence = ByteBuffer.allocate(numberOfBases);

        NucleotideSequenceBuilder bases = new NucleotideSequenceBuilder();
        populateFields(in, numberOfBases, peaks, probability,
                substitutionConfidence, insertionConfidence,
                deletionConfidence, bases);
        visitor.visitAConfidence(probability[0]);
        visitor.visitCConfidence(probability[1]);
        visitor.visitGConfidence(probability[2]);
        visitor.visitTConfidence(probability[3]);

        visitor.visitPeaks(peaks.array());
        visitor.visitBasecalls(bases.build());
        if(visitor instanceof SCFChromatogramFileVisitor){
            SCFChromatogramFileVisitor scfVisitor = (SCFChromatogramFileVisitor) visitor;
            scfVisitor.visitSubstitutionConfidence(substitutionConfidence.array());
            scfVisitor.visitInsertionConfidence(insertionConfidence.array());
            scfVisitor.visitDeletionConfidence(deletionConfidence.array());
        }

        
    }

}
