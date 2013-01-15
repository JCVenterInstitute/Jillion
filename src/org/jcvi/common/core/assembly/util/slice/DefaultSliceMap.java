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
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class DefaultSliceMap extends AbstractSliceMap{
	
	private final IdedSlice[] slices;
    protected PhredQuality defaultQuality;
	    
    public static <R extends AssembledRead, C extends Contig<R>> SliceMap create(C contig, QualitySequenceDataStore qualityDataStore,
                        QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new DefaultSliceMap(contig, qualityDataStore, qualityValueStrategy);
    }
    
    public static <PR extends AssembledRead> DefaultSliceMap create(CoverageMap<PR> coverageMap,QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy){
        return new DefaultSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }

   
    
    
    private DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
                        QualitySequenceDataStore qualityDataStore,
                        QualityValueStrategy qualityValueStrategy){
        this(coverageMap,qualityDataStore, qualityValueStrategy,null);
    }
    
    private <PR extends AssembledRead,C extends Contig<PR>>  DefaultSliceMap(
            C contig, QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
    	DefaultSlice.Builder builders[] = new DefaultSlice.Builder[(int)contig.getConsensusSequence().getLength()];
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
    					builders[start+i] = new DefaultSlice.Builder();
    				}
    				builders[start+i].add(id, base, quality, dir);
    				i++;
    			}
    		}
    		//done building
    		this.slices = new IdedSlice[builders.length];
    		for(int i=0; i<slices.length; i++){
    			if(builders[i] ==null){
    				slices[i] = DefaultSlice.EMPTY;
    			}else{
    				slices[i]= builders[i].build();
    			}
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
    }
    protected DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
            QualitySequenceDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality){
    	int lengthOfMap = (int)CoverageMapUtil.getLastCoveredOffsetIn(coverageMap)+1;
    	this.slices = new IdedSlice[lengthOfMap];
        this.defaultQuality = defaultQuality;
        for(CoverageRegion<?  extends AssembledRead> region : coverageMap){
        	Range range = region.asRange();
            for(long i=range.getBegin(); i<=range.getEnd(); i++ ){
                List<IdedSliceElement> sliceElements = createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy);
                slices[(int)i] =new DefaultSlice.Builder()
                                            .addAll(sliceElements)
                                            .build();
            
            }
        }
    }

    
    
    /**
     * @return the defaultQuality
     */
    protected PhredQuality getDefaultQuality() {
        return defaultQuality;
    }

    public DefaultSliceMap(List<IdedSlice> slices){
    	this.slices = new IdedSlice[slices.size()];
        for(int i=0; i< this.slices.length; i++){
        	this.slices[i] = slices.get(i);
        }
    }
    @Override
    public IdedSlice getSlice(long offset) {
        return slices[(int)offset];
    }
    @Override
    public long getSize() {
        return slices.length;
    }
    @Override
    public Iterator<IdedSlice> iterator() {
        return Arrays.<IdedSlice>asList(slices).iterator();
    }

    
   
}
