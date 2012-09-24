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

package org.jcvi.common.core.assembly.util.slice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.util.iter.ArrayIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 * 
 * 
 */
public final class CompactedSliceMap implements SliceMap {
    private final CompactedSlice[] slices;

    public static <PR extends AssembledRead> CompactedSliceMap create(Contig<PR> contig,QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(contig, qualityDataStore, qualityValueStrategy);
    }
    public static <PR extends AssembledRead> CompactedSliceMap create(Contig<PR> contig, PhredQuality defaultQuality) throws DataStoreException{
        return new CompactedSliceMap(contig, new NullQualityDataStore(defaultQuality), new FakeQualityValueStrategy(defaultQuality));
    }
    public static <PR extends AssembledRead> CompactedSliceMap create(CoverageMap<PR> coverageMap,QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    public static <PR extends AssembledRead> CompactedSliceMap create(CoverageMap<PR> coverageMap,PhredQuality defaultQuality) throws DataStoreException{
        return new CompactedSliceMap(coverageMap, new NullQualityDataStore(defaultQuality), new FakeQualityValueStrategy(defaultQuality));
    }
    private static final class NullQualityDataStore implements QualitySequenceDataStore{
        final QualitySequence fakeQualities;
        public NullQualityDataStore(final PhredQuality defaultQuality){
            fakeQualities = new QualitySequence(){
             

                /**
                * {@inheritDoc}
                */
                @Override
                public PhredQuality get(long index) {
                    return defaultQuality;
                }

                /**
                * {@inheritDoc}
                */
                @Override
                public long getLength() {
                    // TODO Auto-generated method stub
                    return 0;
                }


                @Override
                public Iterator<PhredQuality> iterator() {
                    // TODO Auto-generated method stub
                    return null;
                }

				@Override
				public Iterator<PhredQuality> iterator(Range range) {
					// TODO Auto-generated method stub
					return null;
				}
                
            };
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public StreamingIterator<String> idIterator() throws DataStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public QualitySequence get(String id) throws DataStoreException {

            return fakeQualities;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean contains(String id) throws DataStoreException {
            // TODO Auto-generated method stub
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getNumberOfRecords() throws DataStoreException {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isClosed(){
            // TODO Auto-generated method stub
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public StreamingIterator<QualitySequence> iterator() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    private static final class FakeQualityValueStrategy implements QualityValueStrategy{
        private final PhredQuality defaultQuality;
        
        private FakeQualityValueStrategy(PhredQuality defaultQuality) {
            super();
            this.defaultQuality = defaultQuality;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PhredQuality getQualityFor(AssembledRead placedRead,
        		QualitySequence fullQualities, int gappedReadIndex) {
            return defaultQuality;
        }
        
    }
    private <PR extends AssembledRead, M extends CoverageMap<PR>> CompactedSliceMap(
            M coverageMap, QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
        int size = (int)CoverageMapUtil.getLastCoveredOffsetIn(coverageMap)+1;
        this.slices = new CompactedSlice[size];
        for(CoverageRegion<PR> region : coverageMap){
            Map<String,QualitySequence> qualities = new HashMap<String,QualitySequence>(region.getCoverageDepth());
            for(AssembledRead read :region){
                final String id = read.getId();
                if(qualityDataStore==null){
                    qualities.put(id,null);
                }else{
                    qualities.put(id,qualityDataStore.get(id));
                }
            }
            Range range = region.asRange();
            for(int i=(int)range.getBegin(); i<=range.getEnd(); i++ ){
                
                this.slices[i] =createSlice(region, qualities,qualityValueStrategy,i);                
            }
        }
    }
   
    private <PR extends AssembledRead, C extends Contig<PR>>  CompactedSliceMap(
            C contig, QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
    	CompactedSlice.Builder builders[] = new CompactedSlice.Builder[(int)contig.getConsensusSequence().getLength()];
    	StreamingIterator<PR> readIter = null;
    	try{
    		readIter = contig.getReadIterator();
    		while(readIter.hasNext()){
    			PR read = readIter.next();
    			int start = (int)read.getGappedStartOffset();
    			int i=0;
    			String id =read.getId();
    			Direction dir = read.getDirection();
    			
    			QualitySequence fullQualities = qualityDataStore.get(id);
    			for(Nucleotide base : read.getNucleotideSequence()){
    				PhredQuality quality = qualityValueStrategy.getQualityFor(read, fullQualities, i);
    				if(builders[start+i] ==null){
    					builders[start+i] = new CompactedSlice.Builder();
    				}
    				builders[start+i].addSliceElement(id, base, quality, dir);
    				i++;
    			}
    		}
    		//done building
    		this.slices = new CompactedSlice[builders.length];
    		for(int i=0; i<slices.length; i++){
    			if(builders[i]==null){
    				slices[i] = CompactedSlice.EMPTY;
    			}else{
    				slices[i]= builders[i].build();
    			}
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
    }
    /**
     * {@inheritDoc}
     */
    protected CompactedSlice createSlice(
            CoverageRegion<? extends AssembledRead> region, 
            Map<String,QualitySequence> qualities,
            QualityValueStrategy qualityValueStrategy,
            int i) {
        CompactedSlice.Builder builder = new CompactedSlice.Builder();
        for (AssembledRead read : region) {
            String id=read.getId();
            int indexIntoRead = (int) (i - read.getGappedStartOffset());
            QualitySequence fullQualities = qualities.get(id);
            final PhredQuality quality;
            if(fullQualities==null){
                quality = PhredQuality.valueOf(30);
            }else{
                quality = qualityValueStrategy.getQualityFor(read, fullQualities, indexIntoRead);
            }
            builder.addSliceElement(id,
                    read.getNucleotideSequence().get(indexIntoRead),
                    quality, read.getDirection());
        }
        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<IdedSlice> iterator() {
        return new ArrayIterator<IdedSlice>(slices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdedSlice getSlice(long offset) {
        return slices[(int) offset];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return slices.length;
    }

}
