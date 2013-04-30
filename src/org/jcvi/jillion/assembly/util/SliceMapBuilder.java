package org.jcvi.jillion.assembly.util;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceMap;

public final class SliceMapBuilder<R extends AssembledRead> implements Builder<SliceMap>{

	private final Contig<R> contig;
	private QualitySequenceDataStore qualities;
	
	private PhredQuality defaultQuality;
	private GapQualityValueStrategy qualityValueStrategy = GapQualityValueStrategy.LOWEST_FLANKING;
	
	private ReadFilter<? super R> filter=null;
	
	public SliceMapBuilder(Contig<R> contig, PhredQuality defaultQuality){
		if(contig ==null){
			throw new NullPointerException("contig can not be null");
		}
		if(defaultQuality ==null){
			throw new NullPointerException("defaultQuality can not be null");
		}
		this.contig = contig;
		this.defaultQuality = defaultQuality;
	}
	
	public SliceMapBuilder(Contig<R> contig, QualitySequenceDataStore readQualities){
		if(contig ==null){
			throw new NullPointerException("contig can not be null");
		}
		if(readQualities ==null){
			throw new NullPointerException("readQualities can not be null");
		}
		this.contig = contig;
		this.qualities = readQualities;
	}
	
	public SliceMapBuilder<R> gapQualityValueStrategy(GapQualityValueStrategy strategy){
		if(strategy==null){
			throw new NullPointerException("GapQualityValueStrategies can not be null");
		}
		this.qualityValueStrategy = strategy;
		return this;
	}
	
	
	/**
	 * Apply the given {@link ReadFilter} to each {@link AssembledRead}
	 * in the contig, only reads that are accepted by the filter
	 * will be considered when building the {@link SliceMap}.
	 * The filter will be called by the {@link #build()}.
	 * @param filter the {@link ReadFilter} to use, can not be null;
	 * if no filter is required, do not call this method.
	 * @return this
	 * @see #maxAllowedCoverage(int)
	 * @throws NullPointerException if filter is null.
	 */
	public SliceMapBuilder<R> filter(ReadFilter<? super R> filter){
		if(filter==null){
			throw new IllegalArgumentException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	
	@Override
	public SliceMap build() {
		StreamingIterator<R> iter=null;
		try {			
			if(filter ==null){
				iter = contig.getReadIterator();
			}else{
				iter = new FilteredIterator(contig.getReadIterator());
			}
			if(qualities == null){
				//no quality datastore set use default quality
				return CompactedSliceMap.create(iter, (int)contig.getConsensusSequence().getLength(), defaultQuality, qualityValueStrategy );			
			}
			return CompactedSliceMap.create(iter,(int)contig.getConsensusSequence().getLength(), qualities, qualityValueStrategy );
		} catch (DataStoreException e) {
			throw new IllegalStateException("error building SliceMap",e);
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	private class FilteredIterator implements StreamingIterator<R>{
		private final Object endToken = new Object();
		
		private final StreamingIterator<R> delegate;
		private Object current;
		
		public FilteredIterator(StreamingIterator<R> delegate) {
			this.delegate = delegate;
			updateCurrent();
		}

		private void updateCurrent(){
			current=endToken;
			while(delegate.hasNext()){
				R next = delegate.next();
				if(filter.accept(next)){
					current=next;
					break;
				}
			}
		}
		@Override
		public boolean hasNext() {
			return current !=endToken;
		}

		@Override
		public void close() throws IOException {
			current = endToken;
			delegate.close();
			
		}

		@Override
		public R next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			@SuppressWarnings("unchecked")
			R ret = (R) current;
			updateCurrent();
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}

}
