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
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import org.jcvi.common.core.seq.trace.sanger.Position;
import org.jcvi.common.core.seq.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramFileVisitor;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.header.SCFHeader;

public class Version2BasesSectionCodec extends AbstractBasesSectionCodec{

    @Override
    protected void readBasesData(DataInputStream in, ScfChromatogramBuilder c,
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



    protected void writeBasesDataToBuffer(ByteBuffer buffer, ScfChromatogram c, int numberOfBases) {
        
     
        Iterator<Position> peaks = c.getPositionSequence().iterator();
        final ChannelGroup channelGroup = c.getChannelGroup();
        
        Iterator<PhredQuality> aQualities = channelGroup.getAChannel().getConfidence().iterator();
        Iterator<PhredQuality> cQualities = channelGroup.getCChannel().getConfidence().iterator();
        Iterator<PhredQuality> gQualities = channelGroup.getGChannel().getConfidence().iterator();
        Iterator<PhredQuality> tQualities = channelGroup.getTChannel().getConfidence().iterator();
      
        Iterator<Nucleotide> bases = c.getNucleotideSequence().iterator();
        
        final ByteBuffer substitutionConfidence = getOptionalField(c.getSubstitutionConfidence());
        final ByteBuffer insertionConfidence = getOptionalField(c.getInsertionConfidence());
        final ByteBuffer deletionConfidence = getOptionalField(c.getDeletionConfidence());
        while(bases.hasNext()){
        	buffer.putInt(peaks.next().getValue());
        	buffer.put(aQualities.next().getQualityScore());
        	buffer.put(cQualities.next().getQualityScore());
        	buffer.put(gQualities.next().getQualityScore());
        	buffer.put(tQualities.next().getQualityScore());
        	buffer.put((byte)bases.next().getCharacter().charValue());
			handleOptionalField(buffer, substitutionConfidence);
			handleOptionalField(buffer, insertionConfidence);
			handleOptionalField(buffer, deletionConfidence);
        }
             
       
    }

    private ByteBuffer getOptionalField(QualitySequence confidence){
        
        if(confidence !=null){
        	ByteBuffer buf = ByteBuffer.allocate((int)confidence.getLength());
        	for(PhredQuality qual : confidence){
        		buf.put(qual.getQualityScore());
        	}
        	buf.rewind();     
        	return buf;
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
        if(visitor instanceof ScfChromatogramFileVisitor){
            ScfChromatogramFileVisitor scfVisitor = (ScfChromatogramFileVisitor) visitor;
            scfVisitor.visitSubstitutionConfidence(substitutionConfidence.array());
            scfVisitor.visitInsertionConfidence(insertionConfidence.array());
            scfVisitor.visitDeletionConfidence(deletionConfidence.array());
        }

        
    }

}
