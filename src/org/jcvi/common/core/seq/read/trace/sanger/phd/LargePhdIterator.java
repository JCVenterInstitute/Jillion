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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySymbolCodec;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

/**
 * {@code LargePhdIterator} is a {@link CloseableIterator}
 * implementation that iterates over a phd ball (potentially large)
 * file while parsing it.  This should be the fastest and most
 * memory efficient way to iterate over a phd ball file.
 * @author dkatzel
 *
 */
public class LargePhdIterator extends AbstractBlockingCloseableIterator<Phd>{
    private static final QualitySymbolCodec QUALITY_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    private final File phdFile;
        
    
    
    public static LargePhdIterator createNewIterator(File phdFile){
        LargePhdIterator iter= new LargePhdIterator(phdFile);
        iter.start();
        return iter;
    }
    private LargePhdIterator(File phdFile) {
        this.phdFile = phdFile;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        PhdFileVisitor visitor = new AbstractPhdFileVisitor() {
            
            @Override
            protected boolean visitPhd(String id, List<Nucleotide> bases,
                    List<PhredQuality> qualities, List<ShortSymbol> positions,
                    Properties comments, List<PhdTag> tags) {
                Phd phd = new DefaultPhd(id,
                        NucleotideSequenceFactory.create(bases),
                        new EncodedQualitySequence(QUALITY_CODEC, qualities),
                        new SangerPeak(positions),
                        comments,
                        tags);
                blockingPut(phd);
                return !LargePhdIterator.this.isClosed();                
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public synchronized void visitEndOfFile() {                
                super.visitEndOfFile();
                LargePhdIterator.this.finishedIterating();
            }            
            
        };
        
        try {
            PhdParser.parsePhd(phdFile, visitor);
        } catch (FileNotFoundException e) {
           throw new RuntimeException(
                   String.format("phd file %s does not exist",phdFile.getAbsolutePath()),
                   e);
        }
    }

}
