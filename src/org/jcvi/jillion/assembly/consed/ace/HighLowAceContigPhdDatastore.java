/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
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
 * values are needed and the actual phd file(s) either do not exist,
 * or speed and memory usage demands
 * that the actual phd files can not be used.
 * @author dkatzel
 */
public final class HighLowAceContigPhdDatastore implements PhdDataStore{
    private final PhdDataStore delegate;
    
    
    
    public static PhdDataStore create(File aceContigFile) throws IOException{
        return new Builder(aceContigFile).build();
    }
    public static PhdDataStore create(InputStream aceContigFileStream) throws IOException{
        return new Builder(aceContigFileStream).build();
    }
   
    
    
    private HighLowAceContigPhdDatastore(Builder builder) throws IOException{
        FullLengthPhdParser2 visitor = new FullLengthPhdParser2(builder);
        
        builder.parser.parse(visitor);
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

    @Override
    public StreamingIterator<Phd> iterator() throws DataStoreException {
        return delegate.iterator();
    }
    
   
    
    @Override
	public StreamingIterator<DataStoreEntry<Phd>> entryIterator()
			throws DataStoreException {
		return delegate.entryIterator();
	}



	/**
     * {@code FullLengthPhdParser} will parse full length
     * basecalls from an ace file and infer if the basecalls
     * are high or low quality based on upper vs lower case basecalls.
     * @author dkatzel
     */
    private static final class FullLengthPhdParser2 extends AbstractAceFileVisitor{

    	private Map<String, Phd> phds=null;
        private final Predicate<String> contigFilter, readFilter;
        private final byte lowQuality;
        private final byte highQuality;
        private final boolean oneContigOnly;
       
        FullLengthPhdParser2(Builder builder) {
            this.contigFilter = builder.contigFilter;
            this.readFilter = builder.readFilter;
            this.lowQuality = builder.lowQuality.getQualityScore();
            this.highQuality = builder.highQuality.getQualityScore();
            this.oneContigOnly = builder.oneContigOnly;
        }
        /**
         * @return the phds
         */
        public Map<String, Phd> getPhds() {
            return phds;
        }
        
		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
        	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(totalNumberOfReads);
            phds = new HashMap<String, Phd>(mapSize);            
		}
		@Override
		public AceContigVisitor visitContig(final AceFileVisitorCallback callback,
				String contigId, int numberOfBases, final int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			if(contigFilter.test(contigId)){				
				return new AbstractAceContigVisitor() {
					int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
					final Map<String, Direction> directions = new HashMap<String, Direction>(mapSize);
					
					
					@Override
					public void visitAlignedReadInfo(String readId,
							Direction dir, int gappedStartOffset) {
						if(readFilter.test(readId)){
							directions.put(readId, dir);
						}
					}


					@Override
					public void visitEnd() {						
						if(oneContigOnly){
							callback.haltParsing();
						}
					}


					@Override
					public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
						if(readFilter.test(readId)){
							return new IndividualReadPhdBuilderVisitor(readId, gappedLength, directions.get(readId));
						}
						return null;
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
    
    /**
     * {@code Builder} class to customize how creating the objects in the {@link PhdDataStore}
     * from the ace file.
     * @author dkatzel
     *
     */
    public static final class Builder{
    	
    	static final int DEFAULT_LOW_QUALITY = 15;
    	static final int DEFAULT_HIGH_QUALITY = 30;
    	
    	
    	private final AceParser parser;
    	private Predicate<String> readFilter = DataStoreFilters.alwaysAccept();
    	private Predicate<String> contigFilter = DataStoreFilters.alwaysAccept();
    	
    	private PhredQuality lowQuality = PhredQuality.valueOf(DEFAULT_LOW_QUALITY);
    	private PhredQuality highQuality = PhredQuality.valueOf(DEFAULT_HIGH_QUALITY);
    	
    	private boolean oneContigOnly=false;
    	/**
    	 * Create a new Builder instance that will use the given 
    	 * Ace encoded file as the source for all Phd objects
    	 * in the PhdDataStore to be built.
    	 * @param aceFile the ace file to use; can not be null and must exist.
    	 * @throws IOException if the ace file does not exist.
    	 * @throws NullPointerException if the aceFile is null.
    	 */
    	public Builder(File aceFile) throws IOException{
    		parser = AceFileParser.create(aceFile);
    	}
    	/**
    	 * Create a new Builder instance that will use the given 
    	 * Ace encoded {@link InputStream} as the source for all Phd objects
    	 * in the PhdDataStore to be built.
    	 * @param in the InputStream to use; can not be null and must exist.
    	 * @throws IOException if the ace file does not exist.
    	 * @throws NullPointerException if the InputStream is null.
    	 */
    	public Builder(InputStream in) throws IOException{
    		parser = AceFileParser.create(in);
    	}
    	/**
    	 * Set the {@link PhredQuality} score to use
    	 * for each lowercase base found. If this method 
    	 * is not called, then the default value of 
    	 * {@value org.jcvi.jillion.assembly.consed.ace.HighLowAceContigPhdDatastore.Builder#DEFAULT_LOW_QUALITY} will be used.
    	 * @param qv the PhredQuality value as an int to use for lowercase letters.
    	 * @return this
    	 * @throws IllegalArgumentException - if qualityScore < 0 or > Byte.MAX_VALUE.
    	 */
    	public Builder lowercaseQuality(int qv){
    		lowQuality = PhredQuality.valueOf(qv);
    		return this;
    	}
    	/**
    	 * Set the {@link PhredQuality} score to use
    	 * for each uppercase base found. If this method 
    	 * is not called, then the default value of 
    	 * {@value org.jcvi.jillion.assembly.consed.ace.HighLowAceContigPhdDatastore.Builder#DEFAULT_HIGH_QUALITY} will be used.
    	 * @param qv the PhredQuality value as an int to use for uppercase letters.
    	 * @return this
    	 * @throws IllegalArgumentException - if qualityScore < 0 or > Byte.MAX_VALUE.
    	 */
    	public Builder uppercaseQuality(int qv){
    		highQuality = PhredQuality.valueOf(qv);
    		return this;
    	}
    	/**
    	 * Set the {@link DataStoreFilter} to use to only 
    	 * include the reads that pass the given filter.  If a Contig Filter is also
    	 * used by setting {@link #contigFilter(DataStoreFilter)}
    	 * then only reads that match this filter <strong>from reads in contigs
    	 * that also pass the contig filter</strong> are included..  If this method
    	 * is not called, then all reads in accepted contigs are included in the datastore.
    	 * @param filter a {@link DataStoreFilter} to filter
    	 * by <em>contigId</em>; can not be null.
    	 * @return this.
    	 * @throws NullPointerException if filter is null.
    	 */
    	public Builder readFilter(DataStoreFilter filter){
    		if(filter ==null){
    			throw new NullPointerException("readFilter can not be null");
    		}
    		this.readFilter = filter;
    		return this;
    	}
    	/**
    	 * Set the {@link DataStoreFilter} to use to only 
    	 * consider contigs that pass the filter.  If this method
    	 * is not called, then all contigs are included in the datastore.
    	 * @param filter a {@link DataStoreFilter} to filter
    	 * by <em>contigId</em>; can not be null.
    	 * @return this.
    	 * @throws NullPointerException if filter is null.
    	 */
    	public Builder contigFilter(DataStoreFilter filter){
    		return contigFilter((Predicate<String>)filter);
    	}
    	
    	/**
    	 * Set the {@link DataStoreFilter} to use to only 
    	 * consider contigs that pass the filter.  If this method
    	 * is not called, then all contigs are included in the datastore.
    	 * @param filter a {@link DataStoreFilter} to filter
    	 * by <em>contigId</em>; can not be null.
    	 * @return this.
    	 * @throws NullPointerException if filter is null.
    	 */
    	public Builder contigFilter(Predicate<String> filter){
    		if(filter ==null){
    			throw new NullPointerException("contigFilter can not be null");
    		}
    		this.contigFilter = filter;
    		return this;
    	}
    	/**
    	 * Flag to set that this DataStore will only
    	 * include reads from the first contig
    	 * that passes the contig filter (as specified
    	 * by {@link #contigFilter(DataStoreFilter)}).
    	 * Once the first accepted contig is completely parsed,
    	 * the parser will halt parsing the ace file.  This
    	 * can be very helpful if you only quickly want to parse
    	 * only a portion of a very large ace file.
    	 * @return this
    	 */
    	public Builder forOneContigOnly(){
    		oneContigOnly = true;
    		return this;
    	}
    	/**
    	 * Create a new {@link PhdDataStore} that constructs
    	 * {@link Phd} objects from the given ace data 
    	 * using the current configuration set in this Builder. 
    	 * @return a new {@link PhdDataStore} will never be null.
    	 * @throws IOException if there is a problem parsing the ace data.
    	 */
    	public PhdDataStore build() throws IOException{
    		return new HighLowAceContigPhdDatastore(this);
    	}
    }
}
