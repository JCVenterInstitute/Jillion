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

package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.Builder;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
/**
 * {@code SCFChromatogramFile} is a {@link SCFChromatogramFileVisitor} implementation
 * that once populated can function as a {@link SCFChromatogram}.
 * @author dkatzel
 *
 *
 */
public final class SCFChromatogramFile {
    
    /**
     * Create a new {@link SCFChromatogram} instance from the given
     * SCF encoded file.
     * @param scfFile the SCF encoded file to parse
     * @return a new {@link SCFChromatogram} instance containing data
     * from the given SCF file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static SCFChromatogram create(File scfFile) throws TraceDecoderException, IOException{
        SCFChromatogramFileBuilderVisitor visitor = createNewBuilderVisitor();
        SCFChromatogramFileParser.parseSCFFile(scfFile, visitor);
        return visitor.build();
    }
    /**
     * Create a new {@link SCFChromatogram} instance from the given
     * SCF encoded InputStream, This method will close the input stream regardless
     * if this method returns or throws an exception.
     * @param scfInputStream the SCF encoded input stream to parse
     * @return a new {@link SCFChromatogram} instance containing data
     * from the given SCF file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static SCFChromatogram create(InputStream scfInputStream) throws TraceDecoderException, IOException{
        try{
            SCFChromatogramFileBuilderVisitor visitor = createNewBuilderVisitor();
            SCFChromatogramFileParser.parseSCFFile(scfInputStream, visitor);
            return visitor.build();
        }finally{
            IOUtil.closeAndIgnoreErrors(scfInputStream);
        }
    }
    /**
     * Creates a new {@code SCFChromatogramFileBuilderVisitor} instance
     * that will build a SCFChromatogram when visited.
     * @author dkatzel
     * @see SCFChromatogramFileBuilderVisitor
     *
     */
    public static SCFChromatogramFileBuilderVisitor createNewBuilderVisitor(){
        return new SCFChromatogramFileBuilderVisitor();
    }
    
    private SCFChromatogramFile(){
        throw new IllegalStateException("can not instantiate");
    }
    
    
    /**
     * {@code SCFChromatogramFileBuilderVisitor} is a helper class
     * that wraps a {@link SCFChromatogramBuilder} by a {@link SCFChromatogramFileVisitor}.
     * This way when a part of the SCF is visited, its corresponding objects get built 
     * by the builder.
     * @author dkatzel
     *
     *
     */
    public static final class SCFChromatogramFileBuilderVisitor implements SCFChromatogramFileVisitor, Builder<SCFChromatogram>{
        private SCFChromatogramBuilder builder = new SCFChromatogramBuilder();
        
        private SCFChromatogramFileBuilderVisitor(){}
        
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
          public void visitBasecalls(String basecalls) {
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
        public SCFChromatogram build() {
            checkNotYetBuilt();
            SCFChromatogram result= builder.build();
            builder =null;
            return result;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitPrivateData(byte[] privateData) {
            checkNotYetBuilt();
            builder.privateData(privateData);             
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitSubstitutionConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.substitutionConfidence(confidence);             
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitInsertionConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.insertionConfidence(confidence);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitDeletionConfidence(byte[] confidence) {
            checkNotYetBuilt();
            builder.deletionConfidence(confidence);
        }

        
    }

    
    
}
