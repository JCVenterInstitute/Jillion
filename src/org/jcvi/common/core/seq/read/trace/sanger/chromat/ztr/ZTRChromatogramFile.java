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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * {@code ZTRChromatogramFile} is a helper class
 * that can create ZTRchromatogram objects from 
 * ZTR encoded files.
 * @author dkatzel
 *
 *
 */
public final class ZTRChromatogramFile{
    
    private ZTRChromatogramFile(){
        throw new IllegalStateException("can not instantiate");
    }
    /**
     * {@code ZTRChromatogramFileBuilderVisitor} is a helper class
     * that wraps a {@link ZTRChromatogramBuilder} by a {@link ZTRChromatogramFileVisitor}.
     * This way when a part of the ZTR is visited, its corresponding objects get built 
     * by the builder.
     * @author dkatzel
     *
     *
     */
    public static final class ZTRChromatogramFileBuilderVisitor implements ZTRChromatogramFileVisitor, Builder<ZTRChromatogram>{
        private ZTRChromatogramBuilder builder = new ZTRChromatogramBuilder();
        
        private ZTRChromatogramFileBuilderVisitor(){}
        
        private void checkNotYetBuilt(){
            if(builder==null){
                throw new IllegalStateException("builder already built");
            }
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.aConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitCConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.cConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitGConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.gConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitTConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.tConfidence(confidence);            
        }
        
        /**
         * {@inheritDoc}
         */
         @Override
         public void visitNewTrace() { checkNotYetBuilt();}
         /**
         * {@inheritDoc}
         */
         @Override
         public void visitEndOfTrace() { checkNotYetBuilt();}
         
         /**
          * {@inheritDoc}
          */
          @Override
          public void visitEndOfFile() { checkNotYetBuilt();}

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitBasecalls(NucleotideSequence basecalls) {
              checkNotYetBuilt();
              builder.basecalls(basecalls);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitPeaks(short[] peaks) {
              checkNotYetBuilt();
              builder.peaks(peaks);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitClipRange(Range clipRange) {
              checkNotYetBuilt();
              builder.clip(clipRange);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitComments(Map<String,String> comments) {
              checkNotYetBuilt();
              builder.properties(comments);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitAPositions(short[] positions) {
              checkNotYetBuilt();
              builder.aPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitCPositions(short[] positions) {
              checkNotYetBuilt();
             builder.cPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitGPositions(short[] positions) {
              checkNotYetBuilt();
              builder.gPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitTPositions(short[] positions) {
              checkNotYetBuilt();
              builder.tPositions(positions);              
          }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() { checkNotYetBuilt(); }

        /**
        * {@inheritDoc}
        */
        @Override
        public ZTRChromatogram build() {
            checkNotYetBuilt();
            ZTRChromatogram result= builder.build();
            builder =null;
            return result;
        }

        
    }
    /**
     * Create a new {@link ZTRChromatogram} instance from the given
     * ZTR encoded file.
     * @param ztrFile the ZTR encoded file to parse
     * @return a new {@link ZTRChromatogram} instance containing data
     * from the given ZTR file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static ZTRChromatogram create(File ztrFile) throws FileNotFoundException, TraceDecoderException{
        ZTRChromatogramFileBuilderVisitor visitor = createNewBuilderVisitor();
        ZTRChromatogramFileParser.parse(ztrFile, visitor);
        return visitor.build();
    }
    
    /**
     * Create a new {@link ZTRChromatogram} instance from the given
     * ZTR encoded InputStream, This method will close the input stream regardless
     * if this method returns or throws an exception.
     * @param ztrInputStream the ZTR encoded input stream to parse
     * @return a new {@link ZTRChromatogram} instance containing data
     * from the given ZTR file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static ZTRChromatogram create(InputStream ztrInputStream) throws FileNotFoundException, TraceDecoderException{
        try{
            ZTRChromatogramFileBuilderVisitor visitor = createNewBuilderVisitor();
            ZTRChromatogramFileParser.parse(ztrInputStream, visitor);
            return visitor.build();
        }finally{
            IOUtil.closeAndIgnoreErrors(ztrInputStream);
        }
    }
    /**
     * Creates a new {@code ZTRChromatogramFileBuilderVisitor} instance
     * that will build a ZTRChromatogram when visited.
     * @author dkatzel
     * @see ZTRChromatogramFileBuilderVisitor
     *
     */
    public static ZTRChromatogramFileBuilderVisitor createNewBuilderVisitor(){
        return new ZTRChromatogramFileBuilderVisitor();
    }
    
    
}
