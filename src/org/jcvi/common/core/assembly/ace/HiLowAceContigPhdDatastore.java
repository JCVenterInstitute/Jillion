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

package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStoreAdapter;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code HiLowAceContigPhdDatastore} is a PhdDataStore
 * that only sets the quality values for each basecall
 * to either a high quality or a low quality. The threshold
 * for determining if the value is high or low depends on if the 
 * provided basecall in the ace file is upper or lower case.
 * This implementation is helpful if only approximate quality 
 * values are needed and speed and memory usage demands
 * that the actual phd file can not be parsed.
 * @author dkatzel
 */
public final class HiLowAceContigPhdDatastore implements PhdDataStore{
    public static final PhredQuality DEFAULT_LOW_QUALITY = PhredQuality.valueOf(15);
    public static final PhredQuality DEFAULT_HIGH_QUALITY = AceFileUtil.ACE_DEFAULT_HIGH_QUALITY_THRESHOLD;
    private final PhdDataStore delegate;
    
    public static HiLowAceContigPhdDatastore create(File aceContigFile, final String contigId) throws IOException{
        return new HiLowAceContigPhdDatastore(aceContigFile, contigId);
    }
    public static HiLowAceContigPhdDatastore create(File aceContigFile) throws IOException{
        return new HiLowAceContigPhdDatastore(aceContigFile,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    public static HiLowAceContigPhdDatastore create(InputStream aceContigFile) throws IOException{
        return new HiLowAceContigPhdDatastore(aceContigFile,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    private HiLowAceContigPhdDatastore(File aceContigFile, final String contigId) throws IOException{
        this(aceContigFile,contigId,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    private HiLowAceContigPhdDatastore(File aceContigFile, final String contigId, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(contigId, lowQuality,highQuality);
        
        AceFileParser.parseAceFile(aceContigFile, visitor);
        delegate = new PhdDataStoreAdapter(new SimpleDataStore<Phd>(visitor.getPhds()));
        
    }
    private HiLowAceContigPhdDatastore(InputStream aceContigFile,
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(lowQuality,highQuality);
        
        AceFileParser.parseAceFile(aceContigFile, visitor);
        delegate = new PhdDataStoreAdapter(new SimpleDataStore<Phd>(visitor.getPhds()));
        
    }
    
    
    public HiLowAceContigPhdDatastore(File aceContigFile, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(lowQuality,highQuality);
        
        AceFileParser.parseAceFile(aceContigFile, visitor);
        delegate = new PhdDataStoreAdapter(new SimpleDataStore<Phd>(visitor.getPhds()));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Phd get(String id) throws DataStoreException {
        return delegate.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return delegate.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        delegate.close();        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<Phd> iterator() {
        return delegate.iterator();
    }
    
    /**
     * {@code FullLengthPhdParser} will parse full length
     * basecalls from an ace file and infer if the basecalls
     * are high or low quality based on upper vs lower case basecalls.
     * @author dkatzel
     */
    private static final class FullLengthPhdParser extends AbstractAceFileVisitor {
        private boolean contigOfInterest=false;
        private List<PhredQuality> currentHiLowQualities;
        private Map<String, Phd> phds=null;
        private final String contigId;
        private final PhredQuality lowQuality;
        private final PhredQuality highQuality;
        private FullLengthPhdParser(final PhredQuality lowQuality, final PhredQuality highQuality) {
            this(null,lowQuality,highQuality);
        }
        private FullLengthPhdParser(String contigId,final PhredQuality lowQuality, final PhredQuality highQuality) {
            this.contigId = contigId;
            this.lowQuality = lowQuality;
            this.highQuality = highQuality;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs,
                int totalNumberOfReads) {
            if(contigId==null){
                phds = new HashMap<String, Phd>(totalNumberOfReads+1, 1F);
                contigOfInterest=true;
            }
            super.visitHeader(numberOfContigs, totalNumberOfReads);
        }
        /**
         * @return the phds
         */
        public Map<String, Phd> getPhds() {
            return phds;
        }

        @Override
        protected void visitNewContig(String aceContigId, String consensus, boolean complimented) {
            //no-op
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean visitEndOfContig() {
            
            if(contigId==null){
                return true;
            }
            //keep parsing until we finish 
            //our contig of interest
            return !contigOfInterest;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean visitContigHeader(String aceContigId,
                int numberOfBases, int numberOfReads,
                int numberOfBaseSegments, boolean reverseComplimented) {
            if(contigId !=null){
                contigOfInterest =aceContigId.equals(contigId);
                if(contigOfInterest){
                    phds = new HashMap<String, Phd>(numberOfReads+1, 1F);
                }
            }
            return super.visitContigHeader(aceContigId, numberOfBases, numberOfReads,
                    numberOfBaseSegments, reverseComplimented);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitReadHeader(String readId,
                int gappedLength) {
            if(contigOfInterest){
                currentHiLowQualities = new ArrayList<PhredQuality>(gappedLength);
            }
            super.visitReadHeader(readId, gappedLength);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitBasesLine(String bases) {
            if(contigOfInterest && currentHiLowQualities !=null){
                currentHiLowQualities.addAll(parseQualities(bases.trim()));
            }
            super.visitBasesLine(bases);
        }

        private List<PhredQuality> parseQualities(String bases){
            List<PhredQuality> qualities = new ArrayList<PhredQuality>(bases.length());
            String gappedBases =ConsedUtil.convertAceGapsToContigGaps(bases);
            char[] chars = gappedBases.toCharArray();
            for(int i=0; i<chars.length; i++){
                if(chars[i] =='-'){
                    continue;
                }
                
                if(Character.isUpperCase(chars[i])){
                    qualities.add(highQuality);
                }else{
                    qualities.add(lowQuality);
                }
            }
            return qualities;
        }
        @Override
        protected synchronized void visitAceRead(String readId, String validBasecalls,
                int offset, Direction dir, Range validRange,
                PhdInfo phdInfo, int ungappedFullLength) {
            if(contigOfInterest){
                String currentFullLengthBasecalls = getCurrentFullLengthBasecalls();
                NucleotideSequence fullLengthBasecalls = NucleotideSequenceFactory.create(
                                        ConsedUtil.convertAceGapsToContigGaps(currentFullLengthBasecalls)
                                                            .replaceAll("-", ""));
                
                if(dir==Direction.REVERSE){
                    Collections.reverse(currentHiLowQualities);
                    fullLengthBasecalls = NucleotideSequenceFactory.create(Nucleotides.reverseCompliment(fullLengthBasecalls.asList()));
                }
                QualitySequence qualities = new EncodedQualitySequence(
                                            RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                                            currentHiLowQualities); 
                
                 
                 Phd phd = new ArtificialPhd(readId, 
                         fullLengthBasecalls,
                                     qualities,19);
                 phds.put(readId,phd);
                 currentHiLowQualities=null;
            }
        }
    }
    
}
