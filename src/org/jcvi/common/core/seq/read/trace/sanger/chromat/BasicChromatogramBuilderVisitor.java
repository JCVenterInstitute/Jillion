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

package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.util.Map;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * {@code BasicChromatogramBuilderVisitor} is a 
 * {@link ChromatogramFileVisitor} implementation
 * that wraps a {@link BasicChromatogramBuilder} instance
 * to build up a single chromatogram object
 * as the chromatogram file is visited.
 * @author dkatzel
 *
 *
 */
public final class BasicChromatogramBuilderVisitor implements ChromatogramFileVisitor, Builder<Chromatogram>{


    private BasicChromatogramBuilder builder;
    
    public BasicChromatogramBuilderVisitor(){
        builder = new BasicChromatogramBuilder();
    }
    public final short[] peaks() {
        return builder.peaks();
    }
    
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
    public Chromatogram build() {
        Chromatogram chromo= builder.build();
        builder=null;
        return chromo;
    }

}
