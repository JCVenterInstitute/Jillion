/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileVisitor;

public class Version3BasesSectionCodec extends AbstractBasesSectionCodec{

    @Override
    protected void readBasesData(DataInputStream in, ScfChromatogramBuilder c,
            int numberOfBases) throws IOException {
        c.peaks( parsePeaks(in, numberOfBases));
        setConfidences(c, parseConfidenceData(in, numberOfBases));
        c.basecalls(parseBasecalls(in, numberOfBases));
        
        c.substitutionConfidence(parseSpareConfidence(in, numberOfBases).array())
        .insertionConfidence(parseSpareConfidence(in, numberOfBases).array())
        .deletionConfidence(parseSpareConfidence(in, numberOfBases).array());
        
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     protected void readBasesData(DataInputStream in, ChromatogramFileVisitor visitor,
             int numberOfBases) throws IOException {
         visitor.visitPeaks( parsePeaks(in, numberOfBases));
         byte[][] confidences =parseConfidenceData(in, numberOfBases);
         
         visitor.visitAConfidence(confidences[0]);
         visitor.visitCConfidence(confidences[1]);
         visitor.visitGConfidence(confidences[2]);
         visitor.visitTConfidence(confidences[3]);
         visitor.visitBasecalls(parseBasecalls(in, numberOfBases));
         
         if(visitor instanceof ScfChromatogramFileVisitor){
             ScfChromatogramFileVisitor scfVisitor = (ScfChromatogramFileVisitor) visitor;
             scfVisitor.visitSubstitutionConfidence(parseSpareConfidence(in, numberOfBases).array());
             scfVisitor.visitInsertionConfidence(parseSpareConfidence(in, numberOfBases).array());
             scfVisitor.visitDeletionConfidence(parseSpareConfidence(in, numberOfBases).array());
         }
        
         
     }

    private ByteBuffer parseSpareConfidence(DataInputStream in,
            int numberOfBases) throws IOException {
        byte[] spare = new byte[numberOfBases];
        try{
        	IOUtil.blockingRead(in, spare, 0, numberOfBases);
        }catch(EOFException e){
        	throw new IOException("could not read all the spare confidences", e);
        }
        
        return ByteBuffer.wrap(spare);
    }

    private short[] parsePeaks(DataInputStream in, int numberOfBases)
            throws IOException {
        short[] peaks = new short[numberOfBases];
        for(int i=0; i<numberOfBases; i++){
            peaks[i]=(short)in.readInt();
        }
        return peaks;
    }

    private byte[][] parseConfidenceData(DataInputStream in,
            int numberOfBases) throws IOException {
        byte[][] probability = new byte[4][numberOfBases];
       
        for(int i=0; i<4; i++){          
           try{
        	IOUtil.blockingRead(in, probability[i], 0, numberOfBases);
           }catch(EOFException e){
        	   throw new IOException("could not read all the confidences for channel "+ i, e);
           }
          
        }
        return probability;
    }

    
    private NucleotideSequence parseBasecalls(DataInputStream in,
            int numberOfBases) throws IOException {
        byte[] bases = new byte[numberOfBases];
        try{
        	IOUtil.blockingRead(in, bases, 0, numberOfBases);
        }catch(EOFException e){
        	throw new IOException(
                    "could not read all the bases", e);
        }        
        for(int i=0; i< numberOfBases; i++){
            if(bases[i]==0){
                bases[i] = (byte)'N';
            }
        }
        return new NucleotideSequenceBuilder(new String(bases,IOUtil.UTF_8))
        				.build();

    }

    @Override
    protected void writeBasesDataToBuffer(ByteBuffer buffer, Chromatogram c,
            int numberOfBases) {
        final ChannelGroup channelGroup = c.getChannelGroup();
        bulkPutPeaks(buffer, c.getPeakSequence());
        bulkPut(buffer,channelGroup.getAChannel().getQualitySequence(), numberOfBases);
        bulkPut(buffer,channelGroup.getCChannel().getQualitySequence(), numberOfBases);
        bulkPut(buffer,channelGroup.getGChannel().getQualitySequence(), numberOfBases);
        bulkPut(buffer,channelGroup.getTChannel().getQualitySequence(), numberOfBases);
        bulkPut(buffer, c.getNucleotideSequence());
        bulkPutWithPadding(buffer, getOptionalSubsitutionConfidence(c), numberOfBases);
        bulkPutWithPadding(buffer, getOptionalInsertionConfidence(c), numberOfBases);
        bulkPutWithPadding(buffer, getOptionalDeletionConfidence(c), numberOfBases);

    }

    private QualitySequence getOptionalSubsitutionConfidence(Chromatogram c){
    	if(c instanceof ScfChromatogram){
    		return ((ScfChromatogram)c).getSubstitutionConfidence();
    	}
    	return null;
    }
    private QualitySequence getOptionalInsertionConfidence(Chromatogram c){
    	if(c instanceof ScfChromatogram){
    		return ((ScfChromatogram)c).getInsertionConfidence();
    	}
    	return null;
    }
    private QualitySequence getOptionalDeletionConfidence(Chromatogram c){
    	if(c instanceof ScfChromatogram){
    		return ((ScfChromatogram)c).getDeletionConfidence();
    	}
    	return null;
    }
    
    private void bulkPut(ByteBuffer buffer,
            NucleotideSequence basecalls) {
       for(Nucleotide glyph : basecalls){
           buffer.put((byte)glyph.getCharacter().charValue());
       }
        
    }
    private void bulkPutPeaks(ByteBuffer buffer,
            PositionSequence peaks) {
       for(Position glyph : peaks){
           buffer.putInt(glyph.getValue());
       }
        
    }

    private void bulkPutWithPadding(ByteBuffer buffer,
            QualitySequence optionalConfidence, int numberOfBases) {
        if(optionalConfidence!=null && optionalConfidence.getLength()>0){
        	int length = (int)optionalConfidence.getLength();
        	for(PhredQuality qual : optionalConfidence){
        		buffer.put(qual.getQualityScore());
        	}
        	int padding = numberOfBases - length;
        	for(int i=0; i<padding; i++){
        		buffer.put((byte)0);
        	}
        }else{
            for(int i=0; i< numberOfBases; i++){
                buffer.put((byte)0);
            }
        }

    }
    private void bulkPut(ByteBuffer buffer, QualitySequence qualities, int expectedSize){
    	for(PhredQuality qual : qualities){
    		buffer.put(qual.getQualityScore());
    	}
    	int padding = expectedSize - (int)qualities.getLength();
    	for(int i=0; i<padding; i++){
    		 buffer.put((byte)0);
    	}
    }
   

    

}
