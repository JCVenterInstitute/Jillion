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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.BasicChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.PrivateDataImpl;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFChromatogramImpl;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * {@code SCFChromatogramBuilder} uses the Builder pattern
 * to create a new {@link SCFChromatogram} instance.
 * @author dkatzel
 *
 *
 */
public final class SCFChromatogramBuilder implements Builder<SCFChromatogram>{

    
    private QualitySequence substitutionConfidence;
    private QualitySequence insertionConfidence;
    private QualitySequence deletionConfidence;

    private byte[] privateData;
   
    private final BasicChromatogramBuilder basicBuilder;
    /**
     * Create a new ScfChromatogramBuilder instance with
     * all fields except for the id unset.  In order
     * to successfully build a valid {@link SCFChromatogram}
     * object please use
     * the various setter methods
     * in this class to set everything
     * before calling {@link #build()}.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null.
     * @throws NullPointerException if either field is null.
     */
    public SCFChromatogramBuilder(String id){
        basicBuilder = new BasicChromatogramBuilder(id);
    }
    /**
     * Create a new ScfChromatogramBuilder instance
     * with the given id and all fields initially set
     * to the the values encoded in the given scf file.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param scfFile a SCF encoded file that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or if
     * the given file is not a valid scf encoded file.
     * @throws NullPointerException if either field is null.
     */
    public SCFChromatogramBuilder(String id, File scfFile) throws IOException{
		this(id);
		SCFChromatogramFileBuilderVisitor visitor = new SCFChromatogramFileBuilderVisitor(this);
	    SCFChromatogramFileParser.parse(scfFile, visitor);       
    }
    /**
     * Create a new ScfChromatogramBuilder instance
     * with the given id and all fields initially set
     * to the the values encoded in the given scf encoded inputStream.
     * The {@link InputStream} will NOT be closed by this constructor,
     * client code must close the stream themselves after returning 
     * from this method (preferably in a finally block).
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param scfInputStream a SCF encoded {@link InputStream} that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or
     * if the {@link InputStream} does not contain valid scf encoded data.
     * @throws NullPointerException if either field is null.
     */
    public SCFChromatogramBuilder(String id, InputStream scfInputStream) throws IOException{
		this(id);
		SCFChromatogramFileBuilderVisitor visitor = new SCFChromatogramFileBuilderVisitor(this);
	    SCFChromatogramFileParser.parse(scfInputStream, visitor);       
    }
    /**
     * Create a new instance of ScfChromatogramBuilder
     * using the given {@link Chromatogram} instance 
     * to set all the initial fields. Only the fields 
     * in the {@link Chromatogram} interface will be copied,
     * any scf specific fields will be null.  Use the various
     * scf specific setter methods to set those values.
     * @param copy a {@link Chromatogram} object
     * whose fields are used as the initial values of this
     * new Builder; can not be null;
     * @throws NullPointerException if copy is null.
     */
    public SCFChromatogramBuilder(Chromatogram copy){
       basicBuilder = new BasicChromatogramBuilder(copy);        
    }
    /**
     * Create a new instance of ScfChromatogramBuilder
     * using the given {@link SCFChromatogram} instance 
     * to set all the initial fields. All the fields 
     * in the {@link SCFChromatogram} interface including
     * scf specific fields will be copied.
     * @param copy a {@link SCFChromatogram} object
     * whose fields are used as the initial values of this
     * new Builder; can not be null;
     * @throws NullPointerException if copy is null.
     */
    public SCFChromatogramBuilder(SCFChromatogram copy){
        this((Chromatogram)copy);
        this.substitutionConfidence =copy.getSubstitutionConfidence();
        this.deletionConfidence =copy.getDeletionConfidence();
        this.insertionConfidence =copy.getInsertionConfidence();
        privateData(copy.getPrivateData().getBytes());
     }
    /**
     * @return the substitutionConfidence
     */
    public QualitySequence substitutionConfidence() {
        return substitutionConfidence;
    }

    /**
     * @param substitutionConfidence the substitutionConfidence to set
     */
    public SCFChromatogramBuilder substitutionConfidence(byte[] substitutionConfidence) {
        this.substitutionConfidence = substitutionConfidence ==null
        		? null
        		: new QualitySequenceBuilder(substitutionConfidence).build();
        return this;
    }

    /**
     * @return the insertionConfidence
     */
    public QualitySequence insertionConfidence() {
        return insertionConfidence;
    }

    /**
     * @param insertionConfidence the insertionConfidence to set
     */
    public SCFChromatogramBuilder insertionConfidence(byte[] insertionConfidence) {
        this.insertionConfidence = insertionConfidence ==null
        		? null
        	:  	new QualitySequenceBuilder(insertionConfidence).build();
        return this;
    }

    /**
     * @return the deletionConfidence
     */
    public QualitySequence deletionConfidence() {
        return deletionConfidence;
    }

    /**
     * @param deletionConfidence the deletionConfidence to set
     */
    public SCFChromatogramBuilder deletionConfidence(byte[] deletionConfidence) {
        this.deletionConfidence = deletionConfidence ==null?null:
        	new QualitySequenceBuilder(deletionConfidence).build();
        
        return this;
    }

    /**
     * @return the privateData
     */
    public byte[] privateData() {
        return privateData==null? null:Arrays.copyOf(privateData, privateData.length);
    }

    /**
     * @param privateData the privateData to set
     */
    public SCFChromatogramBuilder privateData(byte[] privateData) {
        this.privateData = privateData==null? null:Arrays.copyOf(privateData, privateData.length);
        return this;
    }

    public SCFChromatogram build() {
        Chromatogram basicChromo = basicBuilder.build();
        return new SCFChromatogramImpl(basicChromo,
                substitutionConfidence(),
                insertionConfidence(),
                deletionConfidence(),
                createPrivateData());
    }
   
    private PrivateData createPrivateData() {
        if(privateData() ==null){
            return null;
        }
        return new PrivateDataImpl(privateData());
    }

    public final PositionSequence peaks() {
        return basicBuilder.peaks();
    }

    public SCFChromatogramBuilder peaks(short[] peaks) {
        basicBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return basicBuilder.basecalls();
    }

    public SCFChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        basicBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aConfidence() {
        return basicBuilder.aConfidence();
    }

    public final SCFChromatogramBuilder aConfidence(byte[] confidence) {
        basicBuilder.aConfidence(confidence);
        return this;
    }

    public final byte[] cConfidence() {
        return basicBuilder.cConfidence();
    }

    public final SCFChromatogramBuilder cConfidence(byte[] confidence) {
        basicBuilder.cConfidence(confidence);
        return this;
    }

    public final byte[] gConfidence() {
        return basicBuilder.gConfidence();
    }

    public final SCFChromatogramBuilder gConfidence(byte[] confidence) {
        basicBuilder.gConfidence(confidence);
        return this;
    }

    public final byte[] tConfidence() {
        return basicBuilder.tConfidence();
    }

    public final SCFChromatogramBuilder tConfidence(byte[] confidence) {
        basicBuilder.tConfidence(confidence);
        return this;
    }

    public final short[] aPositions() {
        return basicBuilder.aPositions();
    }

    public final SCFChromatogramBuilder aPositions(short[] positions) {
        basicBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return basicBuilder.cPositions();
    }

    public final SCFChromatogramBuilder cPositions(short[] positions) {
        basicBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return basicBuilder.gPositions();
    }

    public final SCFChromatogramBuilder gPositions(short[] positions) {
        basicBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return basicBuilder.tPositions();
    }

    public final SCFChromatogramBuilder tPositions(short[] positions) {
        basicBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> properties() {
        return basicBuilder.properties();
    }

    public final SCFChromatogramBuilder properties(Map<String,String> properties) {
        basicBuilder.properties(properties);
        return this;
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
    private static final class SCFChromatogramFileBuilderVisitor implements SCFChromatogramFileVisitor{
        private final SCFChromatogramBuilder builder;
        private SCFChromatogramFileBuilderVisitor(SCFChromatogramBuilder builder){
        	this.builder = builder;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitAConfidence(byte[] confidence) {
            builder.aConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitCConfidence(byte[] confidence) {
            builder.cConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitGConfidence(byte[] confidence) {
            builder.gConfidence(confidence);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitTConfidence(byte[] confidence) {
            builder.tConfidence(confidence);            
        }
        
        /**
         * {@inheritDoc}
         */
         @Override
         public synchronized void visitNewTrace() { 
       	  	//no-op
         }
         /**
         * {@inheritDoc}
         */
         @Override
         public synchronized void visitEndOfTrace() { 
       	  	//no-op
         }
         
         /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitEndOfFile() {
        	  //no-op
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitBasecalls(NucleotideSequence basecalls) {  
              builder.basecalls(basecalls);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitPeaks(short[] peaks) {  
              builder.peaks(peaks);              
          }

         

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitComments(Map<String,String> comments) {  
              builder.properties(comments);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitAPositions(short[] positions) {  
              builder.aPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitCPositions(short[] positions) {  
             builder.cPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitGPositions(short[] positions) {  
              builder.gPositions(positions);              
          }

          /**
          * {@inheritDoc}
          */
          @Override
          public synchronized void visitTPositions(short[] positions) {  
              builder.tPositions(positions);              
          }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitFile() {
        	//no-op
        }

       

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitPrivateData(byte[] privateData) {
            builder.privateData(privateData);             
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitSubstitutionConfidence(byte[] confidence) {
            builder.substitutionConfidence(confidence);             
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitInsertionConfidence(byte[] confidence) {
            builder.insertionConfidence(confidence);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitDeletionConfidence(byte[] confidence) {
            builder.deletionConfidence(confidence);
        }

        
    }
}
