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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogramBuilder;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRChromatogramImpl;
import org.jcvi.jillion.trace.chromat.Chromatogram;

/**
 * <code>ZtrChromatogramBuilder</code> uses the Builder Pattern
 * to build a {@link ZtrChromatogram} instance.
 * @author dkatzel
 *
 *
 */
public final class ZtrChromatogramBuilder implements Builder<ZtrChromatogram>{
    
    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;

    private final BasicChromatogramBuilder basicBuilder;
    
    /**
     * Create a new {@link ZtrChromatogramBuilder} instance with
     * all fields except for the id unset.  In order
     * to successfully build a valid {@link ZtrChromatogram}
     * object please use
     * the various setter methods
     * in this class to set everything
     * before calling {@link #build()}.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null.
     * @throws NullPointerException if either field is null.
     */
    public ZtrChromatogramBuilder(String id){
        basicBuilder = new BasicChromatogramBuilder(id);
    }
    /**
     * Create a new {@link ZtrChromatogramBuilder} instance
     * with the given id and all fields initially set
     * to the the values encoded in the given ztr file.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param ztrFile a ZTR encoded file that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or if
     * the given file is not a valid ztr encoded file.
     * @throws NullPointerException if either field is null.
     */
    public ZtrChromatogramBuilder(String id, File ztrFile) throws IOException{
        this(id);
        ZTRChromatogramFileBuilderVisitor visitor = new ZTRChromatogramFileBuilderVisitor(this);
        ZtrChromatogramFileParser.parse(ztrFile, visitor); 
    }
    /**
     * Create a new {@link ZtrChromatogramBuilder} instance
     * with the given id and all fields initially set
     * to the the values encoded in the given ztr encoded inputStream.
     * The {@link InputStream} will NOT be closed by this constructor,
     * client code must close the stream themselves after returning 
     * from this method (preferably in a finally block).
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param ztrStream a ZTR encoded {@link InputStream} that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or
     * if the {@link InputStream} does not contain valid ztr encoded data.
     * @throws NullPointerException if either field is null.
     */
    public ZtrChromatogramBuilder(String id, InputStream ztrStream) throws IOException{
        this(id);
        ZTRChromatogramFileBuilderVisitor visitor = new ZTRChromatogramFileBuilderVisitor(this);
        ZtrChromatogramFileParser.parse(ztrStream, visitor); 
    }
    /**
     * Create a new instance of {@link ZtrChromatogramBuilder}
     * using the given {@link Chromatogram} instance 
     * to set all the initial fields. Only the fields 
     * in the {@link Chromatogram} interface will be copied,
     * any ztr specific fields will be null.  Use the various
     * ztr specific setter methods to set those values.
     * @param copy a {@link Chromatogram} object
     * whose fields are used as the initial values of this
     * new Builder; can not be null;
     * @throws NullPointerException if copy is null.
     */
    public ZtrChromatogramBuilder(Chromatogram copy){
       basicBuilder = new BasicChromatogramBuilder(copy);        
    }
    /**
     * Create a new instance of ZtrChromatogramBuilder
     * using the given {@link ZtrChromatogram} instance 
     * to set all the initial fields. All the fields 
     * in the {@link ZtrChromatogram} interface including
     * ztr specific fields will be copied.
     * @param copy a {@link ZtrChromatogram} object
     * whose fields are used as the initial values of this
     * new Builder; can not be null;
     * @throws NullPointerException if copy is null.
     */
    public ZtrChromatogramBuilder(ZtrChromatogram copy){
        this((Chromatogram)copy);
        clip(copy.getClip());
     }
   /**
    * Gets the ZTR's clip points..
    * @return a Clip, may be null.
    */
    public final Range clip() {
        return clip;
    }
    /**
     * Sets the clip.
     * @param clip the clip to set.
     * @return this.
     */
    public final ZtrChromatogramBuilder clip(Range clip) {
        this.clip = clip;
        return this;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public ZtrChromatogram build() {
        return new ZTRChromatogramImpl(basicBuilder.build(),
                clip());
    }
    
    public final PositionSequence peaks() {
        return basicBuilder.peaks();
    }

    public ZtrChromatogramBuilder peaks(short[] peaks) {
        basicBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return basicBuilder.basecalls();
    }

    public ZtrChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        basicBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aConfidence() {
        return basicBuilder.aConfidence();
    }

    public final ZtrChromatogramBuilder aConfidence(byte[] confidence) {
        basicBuilder.aConfidence(confidence);
        return this;
    }

    public final byte[] cConfidence() {
        return basicBuilder.cConfidence();
    }

    public final ZtrChromatogramBuilder cConfidence(byte[] confidence) {
        basicBuilder.cConfidence(confidence);
        return this;
    }

    public final byte[] gConfidence() {
        return basicBuilder.gConfidence();
    }

    public final ZtrChromatogramBuilder gConfidence(byte[] confidence) {
        basicBuilder.gConfidence(confidence);
        return this;
    }

    public final byte[] tConfidence() {
        return basicBuilder.tConfidence();
    }

    public final ZtrChromatogramBuilder tConfidence(byte[] confidence) {
        basicBuilder.tConfidence(confidence);
        return this;
    }

    public final short[] aPositions() {
        return basicBuilder.aPositions();
    }

    public final ZtrChromatogramBuilder aPositions(short[] positions) {
        basicBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return basicBuilder.cPositions();
    }

    public final ZtrChromatogramBuilder cPositions(short[] positions) {
        basicBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return basicBuilder.gPositions();
    }

    public final ZtrChromatogramBuilder gPositions(short[] positions) {
        basicBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return basicBuilder.tPositions();
    }

    public final ZtrChromatogramBuilder tPositions(short[] positions) {
        basicBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> properties() {
        return basicBuilder.properties();
    }

    public final ZtrChromatogramBuilder properties(Map<String,String> properties) {
        basicBuilder.properties(properties);
        return this;
    }
    
    /**
     * {@code ZTRChromatogramFileBuilderVisitor} is a helper class
     * that wraps a {@link ZtrChromatogramBuilder} by a {@link ZtrChromatogramFileVisitor}.
     * This way when a part of the ZTR is visited, its corresponding objects get built 
     * by the builder.
     * @author dkatzel
     *
     *
     */
    private static final class ZTRChromatogramFileBuilderVisitor implements ZtrChromatogramFileVisitor{
        private final ZtrChromatogramBuilder builder ;
        
        private ZTRChromatogramFileBuilderVisitor(ZtrChromatogramBuilder builder){
        	this.builder = builder;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAConfidence(byte[] confidence) {
            builder.aConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitCConfidence(byte[] confidence) {

            builder.cConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitGConfidence(byte[] confidence) {

            builder.gConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitTConfidence(byte[] confidence) {

            builder.tConfidence(confidence);            
        }
        
        /**
         * {@inheritDoc}
         */
         @Override
         public void visitNewTrace() {
        	 //no-op
         }
         /**
         * {@inheritDoc}
         */
         @Override
         public void visitEndOfTrace() {
        	 //no-op
         }


          /**
          * {@inheritDoc}
          */
          @Override
          public void visitBasecalls(NucleotideSequence basecalls) {
  
              builder.basecalls(basecalls);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitPeaks(short[] peaks) {
  
              builder.peaks(peaks);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitClipRange(Range clipRange) {
  
              builder.clip(clipRange);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitComments(Map<String,String> comments) {
  
              builder.properties(comments);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitAPositions(short[] positions) {
  
              builder.aPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitCPositions(short[] positions) {
  
             builder.cPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitGPositions(short[] positions) {
  
              builder.gPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public void visitTPositions(short[] positions) {
  
              builder.tPositions(positions);              
          }


    }
}
