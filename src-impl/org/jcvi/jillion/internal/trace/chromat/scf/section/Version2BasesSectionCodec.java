/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileVisitor;

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
        	putQualityValue(buffer, aQualities);
        	putQualityValue(buffer, cQualities);
        	putQualityValue(buffer, gQualities);
        	putQualityValue(buffer, tQualities);
        	buffer.put((byte)bases.next().getCharacter().charValue());
			handleOptionalField(buffer, substitutionConfidence);
			handleOptionalField(buffer, insertionConfidence);
			handleOptionalField(buffer, deletionConfidence);
        }
             
       
    }

    private void putQualityValue(ByteBuffer dest, Iterator<PhredQuality> iter){
    	if(iter.hasNext()){
    		dest.put(iter.next().getQualityScore());
    	}else{
    		dest.put((byte)0);
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
