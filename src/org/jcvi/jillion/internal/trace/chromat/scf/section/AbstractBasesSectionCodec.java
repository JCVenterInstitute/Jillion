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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

public abstract class AbstractBasesSectionCodec implements SectionCodec{
    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ScfChromatogramBuilder c) throws SectionDecoderException {
        long bytesToSkip = Math.max(0, header.getBasesOffset() - currentOffset);
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

    
    
    
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor visitor)
            throws SectionDecoderException {
        long bytesToSkip = header.getBasesOffset() - currentOffset;
        int numberOfBases = header.getNumberOfBases();
        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            readBasesData(in, visitor, numberOfBases);
            return currentOffset+bytesToSkip + numberOfBases*12;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading bases section",e);
        }
    }




    protected abstract void readBasesData(DataInputStream in, ScfChromatogramBuilder c,
            int numberOfBases) throws IOException;
    protected abstract void readBasesData(DataInputStream in, ChromatogramFileVisitor c,
            int numberOfBases) throws IOException;

    protected static ScfChromatogramBuilder setConfidences(ScfChromatogramBuilder c, byte[][] probability) {

        return c.aQualities(probability[0])
            .cQualities(probability[1])
            .gQualities(probability[2])
            .tQualities(probability[3]);
    }

    @Override
    public EncodedSection encode(Chromatogram c, SCFHeader header)
            throws IOException {
        final int numberOfBases = (int)c.getNucleotideSequence().getLength();
        header.setNumberOfBases(numberOfBases);
        ByteBuffer buffer = ByteBuffer.allocate(numberOfBases*12);
        writeBasesDataToBuffer(buffer, c,numberOfBases);
        buffer.flip();
        return new EncodedSection(buffer, Section.BASES);
    }

    protected abstract void writeBasesDataToBuffer(ByteBuffer buffer, Chromatogram c,int numberOfBases);
}
