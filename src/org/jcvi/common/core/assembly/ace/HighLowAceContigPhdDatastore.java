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
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.datastore.DataStoreAdapter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code HighLowAceContigPhdDatastore} is a PhdDataStore
 * that only sets the quality values for each basecall
 * to either a high quality or a low quality. The threshold
 * for determining if the value is high or low depends on if the 
 * provided basecall in the ace file is upper or lower case.
 * This implementation is helpful if only approximate quality 
 * values are needed and speed and memory usage demands
 * that the actual phd file can not be used.
 * @author dkatzel
 */
public final class HighLowAceContigPhdDatastore implements PhdDataStore{
    public static final PhredQuality DEFAULT_LOW_QUALITY = PhredQuality.valueOf(15);
    public static final PhredQuality DEFAULT_HIGH_QUALITY = AceFileUtil.ACE_DEFAULT_HIGH_QUALITY_THRESHOLD;
    private final PhdDataStore delegate;
    
    public static PhdDataStore create(File aceContigFile, final String contigId) throws IOException{
        return new HighLowAceContigPhdDatastore(aceContigFile, contigId);
    }
    public static PhdDataStore create(File aceContigFile) throws IOException{
        return new HighLowAceContigPhdDatastore(aceContigFile,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    public static PhdDataStore create(InputStream aceContigFile) throws IOException{
        return new HighLowAceContigPhdDatastore(aceContigFile,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    public static PhdDataStore create(InputStream aceContigFile, String contigId) throws IOException{
        return new HighLowAceContigPhdDatastore(aceContigFile,contigId, DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    private HighLowAceContigPhdDatastore(File aceContigFile, final String contigId) throws IOException{
        this(aceContigFile,contigId,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    private HighLowAceContigPhdDatastore(File aceContigFile, final String contigId, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(contigId, lowQuality,highQuality);
        
        AceFileParser.parse(aceContigFile, visitor);
        delegate = DataStoreAdapter.adapt(PhdDataStore.class, MapDataStoreAdapter.adapt(visitor.getPhds()));
        
    }
    private HighLowAceContigPhdDatastore(InputStream aceContigFile,
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(lowQuality,highQuality);
        
        AceFileParser.parse(aceContigFile, visitor);
        delegate = DataStoreAdapter.adapt(PhdDataStore.class, MapDataStoreAdapter.adapt(visitor.getPhds()));
        
    }
    private HighLowAceContigPhdDatastore(InputStream aceContigFile, String contigId,
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(contigId,lowQuality,highQuality);
        
        AceFileParser.parse(aceContigFile, visitor);
        delegate = DataStoreAdapter.adapt(PhdDataStore.class, MapDataStoreAdapter.adapt(visitor.getPhds()));
        
    }
    
    private HighLowAceContigPhdDatastore(File aceContigFile, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser visitor = new FullLengthPhdParser(lowQuality,highQuality);
        
        AceFileParser.parse(aceContigFile, visitor);
        delegate = DataStoreAdapter.adapt(PhdDataStore.class, MapDataStoreAdapter.adapt(visitor.getPhds()));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
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
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed(){
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
     * @throws DataStoreException 
    */
    @Override
    public StreamingIterator<Phd> iterator() throws DataStoreException {
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
        private QualitySequenceBuilder currentHiLowQualities;
        private Map<String, Phd> phds=null;
        private final String contigId;
        private final byte lowQuality;
        private final byte highQuality;
        private FullLengthPhdParser(final PhredQuality lowQuality, final PhredQuality highQuality) {
            this(null,lowQuality,highQuality);
        }
        private FullLengthPhdParser(String contigId,final PhredQuality lowQuality, final PhredQuality highQuality) {
            this.contigId = contigId;
            this.lowQuality = lowQuality.getQualityScore();
            this.highQuality = highQuality.getQualityScore();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs,
                int totalNumberOfReads) {
            if(contigId==null){
            	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(totalNumberOfReads);
                phds = new HashMap<String, Phd>(mapSize);
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
        protected void visitNewContig(String aceContigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complemented) {
            //no-op
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized EndContigReturnCode visitEndOfContig() {
            
            if(contigId==null){
                return EndContigReturnCode.KEEP_PARSING;
            }
            //keep parsing until we finish 
            //our contig of interest
            return contigOfInterest? EndContigReturnCode.STOP_PARSING:EndContigReturnCode.KEEP_PARSING;
        }

        @Override
		public synchronized boolean shouldVisitContig(String aceContigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
        	if(contigId !=null){
                contigOfInterest =aceContigId.equals(contigId);
                if(contigOfInterest){
                	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
                    phds = new HashMap<String, Phd>(mapSize);                    
                }
                return contigOfInterest;
            }
			return true;
		}
		

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitReadHeader(String readId,
                int gappedLength) {
            if(contigOfInterest){
                currentHiLowQualities = new QualitySequenceBuilder(gappedLength);
            }
            super.visitReadHeader(readId, gappedLength);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitBasesLine(String bases) {
            if(contigOfInterest && currentHiLowQualities !=null){
            	addQualitiesToBuilder(bases.trim());               
            }
            super.visitBasesLine(bases);
        }

        private void addQualitiesToBuilder(String bases){
        	
            String ungappedGappedBases =ConsedUtil.convertAceGapsToContigGaps(bases).replaceAll("-", "");
            char[] chars = ungappedGappedBases.toCharArray();
            byte[] qualities = new byte[chars.length];
            for(int i=0; i<chars.length; i++){
                if(Character.isUpperCase(chars[i])){
                    qualities[i]=highQuality;
                }else{
                	qualities[i]=lowQuality;
                }
            }
            currentHiLowQualities.append(qualities);
        }
        /**
         * If this is a read we care about, get the full length
         * mixed-case basecalls via {@link #getCurrentFullLengthBasecalls()}
         * and use those to figure out high vs low quality.
         * {@inheritDoc}
         */
        @Override
        protected synchronized void visitAceRead(String readId, NucleotideSequence ignored,
                int offset, Direction dir, Range validRange,
                PhdInfo phdInfo, int ungappedFullLength) {
            if(contigOfInterest){
                NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(getCurrentFullLengthBasecalls())
                									.ungap();
               
                if(dir==Direction.REVERSE){
                   currentHiLowQualities.reverse();
                    builder.reverseComplement();
                }
                Phd phd = new ArtificialPhd(readId, 
                		 builder.build(),
                		 currentHiLowQualities.build(),19);
                 phds.put(readId,phd);
                 currentHiLowQualities=null;
            }
        }
    }
    
}
