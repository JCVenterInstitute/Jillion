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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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
    public static PhdDataStore create(File aceContigFile,DataStoreFilter filter) throws IOException{
        return new HighLowAceContigPhdDatastore(aceContigFile, filter,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
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
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(contigId, lowQuality,highQuality);
        
        AceFileParser.create(aceContigFile).accept(visitor);
        delegate = DataStoreUtil.adapt(PhdDataStore.class, visitor.getPhds());
        
    }
    
    private HighLowAceContigPhdDatastore(File aceContigFile, DataStoreFilter filter, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(filter,null, lowQuality,highQuality);
        
        AceFileParser.create(aceContigFile).accept(visitor);
        delegate = DataStoreUtil.adapt(PhdDataStore.class, visitor.getPhds());
        
    }
    private HighLowAceContigPhdDatastore(InputStream aceContigFile,
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(lowQuality,highQuality);
        
        AceFileParser.create(aceContigFile).accept(visitor);
        delegate = DataStoreUtil.adapt(PhdDataStore.class, visitor.getPhds());
        
    }
    private HighLowAceContigPhdDatastore(InputStream aceContigFile, String contigId,
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(contigId,lowQuality,highQuality);
        
        AceFileParser.create(aceContigFile).accept(visitor);
        delegate = DataStoreUtil.adapt(PhdDataStore.class, visitor.getPhds());
        
    }
    
    private HighLowAceContigPhdDatastore(File aceContigFile, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(lowQuality,highQuality);
        
        AceFileParser.create(aceContigFile).accept(visitor);
        delegate = DataStoreUtil.adapt(PhdDataStore.class, visitor.getPhds());
        
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
    private static final class FullLengthPhdParser2 extends AbstractAceFileVisitor{

    	private Map<String, Phd> phds=null;
        private final DataStoreFilter filter;
        private final String singleContigId;
        private final byte lowQuality;
        private final byte highQuality;
        
        
        private FullLengthPhdParser2(final PhredQuality lowQuality, final PhredQuality highQuality) {
            this(DataStoreFilters.alwaysAccept(),null,lowQuality,highQuality);
        }
        private FullLengthPhdParser2(String contigId, final PhredQuality lowQuality, final PhredQuality highQuality) {
            this(DataStoreFilters.newIncludeFilter(Collections.singleton(contigId)),contigId,lowQuality,highQuality);
        }
        private FullLengthPhdParser2(DataStoreFilter filter,String contigId, final PhredQuality lowQuality, final PhredQuality highQuality) {
            this.filter = filter;
            this.singleContigId = contigId;
            this.lowQuality = lowQuality.getQualityScore();
            this.highQuality = highQuality.getQualityScore();
        }
        /**
         * @return the phds
         */
        public Map<String, Phd> getPhds() {
            return phds;
        }
        
		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			if(singleContigId==null){
            	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(totalNumberOfReads);
                phds = new HashMap<String, Phd>(mapSize);
            }
		}
		@Override
		public AceContigVisitor visitContig(final AceFileVisitorCallback callback,
				String contigId, int numberOfBases, final int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			if(filter.accept(contigId)){
				//visit if this is our contig or we want all contigs
				if(this.singleContigId!=null){
					int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
	                phds = new HashMap<String, Phd>(mapSize);
				}
				return new AbstractAceContigVisitor() {
					int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
					final Map<String, Direction> directions = new HashMap<String, Direction>(mapSize);
					
					
					@Override
					public void visitAlignedReadInfo(String readId,
							Direction dir, int gappedStartOffset) {
						directions.put(readId, dir);
					}

					@Override
					public void visitEnd() {
						//we have finished the current contig
						//if this is the only contig we care about
						//halt parsing
						if(FullLengthPhdParser2.this.singleContigId!=null){
							callback.haltParsing();
						}
					}

					@Override
					public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
						return new IndividualReadPhdBuilderVisitor(readId, gappedLength, directions.get(readId));
					}
					
				};
			}
			//else skip
			return null;
		}    			
		
		private class IndividualReadPhdBuilderVisitor extends AbstractAceContigReadVisitor{
			private final QualitySequenceBuilder highLowQualities;
			private final NucleotideSequenceBuilder sequenceBuilder;
			private final Direction dir;
			private final String readId;
			
			public IndividualReadPhdBuilderVisitor(String readId, int gappedLength, Direction dir){
				this.readId = readId;
				this.dir = dir;
				highLowQualities = new QualitySequenceBuilder(gappedLength);
				sequenceBuilder = new NucleotideSequenceBuilder(gappedLength);
			}
			
			@Override
			public void visitBasesLine(String mixedCaseBasecalls) {
				highLowQualities.append(toHighLowQualities(mixedCaseBasecalls));
				sequenceBuilder.append(mixedCaseBasecalls);
			}
			
			private byte[] toHighLowQualities(String bases){
	        	
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
	           return qualities;
	        }
			@Override
			public void visitEnd() {
				sequenceBuilder.ungap();
				if(dir==Direction.REVERSE){
					sequenceBuilder.reverseComplement();
					highLowQualities.reverse();
				}
				 Phd phd = new PhdBuilder(readId, 
						 sequenceBuilder.build(),
						 highLowQualities.build())
				 				.fakePeaks()
				 				.build();
                 phds.put(readId,phd);
			}
		}
    }
}
