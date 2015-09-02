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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.MergedSortedRecordIterator;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code ReSortSamFileWriter}
 * is a {@link SamWriter} implementation
 * that can take {@link SamRecord}s given to it via
 * the {@link #writeRecord(SamRecord)}
 * in ANY ORDER and write out the SAM or BAM file
 * sorted by the specified manner.
 * Subclasses handle the actual SAM/BAM encoding.
 * <p>
 * Algorithm: Keep an array of records in memory
 * (size of array specified by user).  When we
 * have filled the array, sort the in memory records
 * and write out a SAM/BAM file to a temp file and
 * clear the in memory array to make room for more
 * records.
 * When {@link #close()} is called,
 * write the combined sorted
 * records to the specified output file.
 * Since 
 * we know each temp file and the in memory array are 
 * each sorted, we can use a merge sort like algorithm
 * to combine the records into one giant sorted list. 
 * </p>
 * @author dkatzel
 *
 */
class ReSortSamFileWriter implements SamWriter {

	private final SamHeader header;
	private final Comparator<SamRecord> recordComparator;
	private final File tmpDir;
	private final File outputFile;
	
	private SamRecord[] inMemoryArray;
	private int currentInMemSize;
	private final int maxRecordsToKeepInMemory;
	private final List<File> tempFiles = new ArrayList<File>();
	private final SamAttributeValidator attributeValidator;
	
	private final Encoding encoding;
	private final BamIndexer indexer;
	
	/**
	 * 
	 * @param outputFile
	 * @param tmpDirRoot
	 * @param header
	 * @param maxRecordsToKeepInMemory
	 * @param attributeValidator
	 * @param tmpFileSuffix
	 * @throws IOException
	 */
	public ReSortSamFileWriter(File outputFile, File tmpDirRoot, SamHeader header, int maxRecordsToKeepInMemory, SamAttributeValidator attributeValidator, Encoding encodingToUse, BamIndexer indexer) throws IOException {
		
		if(maxRecordsToKeepInMemory <0){
			throw new IllegalArgumentException("max records to keep in memory must be >=1");
		}
		if(attributeValidator ==null){
			throw new NullPointerException("attribute valiator can not be null");
		}
		if(encodingToUse ==null){
			throw new NullPointerException("Encoding can not be null");
		}
		this.maxRecordsToKeepInMemory = maxRecordsToKeepInMemory;
		
		this.header = header;
		recordComparator = header.createRecordComparator();
		if(recordComparator ==null){
			throw new NullPointerException("SortOrder must create a non-null comparator " + header.getSortOrder());
		}
		
		IOUtil.mkdirs(outputFile.getParentFile());
		tmpDir = IOUtil.createTempDir("jillion", "samWriterTmp", tmpDirRoot);		
        inMemoryArray = new SamRecord[maxRecordsToKeepInMemory];
        currentInMemSize=0;
        this.outputFile = outputFile;
        this.attributeValidator = attributeValidator;
        this.encoding = encodingToUse;
        this.indexer = indexer;
	}


	@Override
	public void writeRecord(SamRecord record) throws IOException {
		if(record==null){
			throw new NullPointerException("record can not be null");
		}
		persistInMemoryCacheIfNeeded();
		try{
			header.validateRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		
		inMemoryArray[currentInMemSize++] = record;
	}

	/**
	 * Write all the records currently in memory
	 * to a temp file encoded using the given encoding
	 * @throws IOException
	 */
	private void persistInMemoryCacheIfNeeded() throws IOException {
		if(currentInMemSize == maxRecordsToKeepInMemory){
			
			sortInMemoryRecords();
			
			File tempFile= File.createTempFile(outputFile.getName(), encoding.getSuffix(), tmpDir);
			tempFiles.add(tempFile);
			//never pass indexer to temp files
			//only use in final if at all.
			SamWriter writer = encoding.createPreSortedNoValidationOutputWriter(tempFile, header,null);
			try{
				for(int i=0; i<currentInMemSize; i++){
					writer.writeRecord(inMemoryArray[i]);
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(writer);
				clearInMemoryArray();
			}
			
		}
		
	}


	private void clearInMemoryArray() {
		currentInMemSize=0;
		//clear references to free up GC
		Arrays.fill(inMemoryArray, null);
	}


	private void sortInMemoryRecords() {
		//sort records while they are still in memory
		//our comparators in SortOrder
		//handle nulls and sort them last
		//so we don't have to worry about those
		//throwing NPE.
		Arrays.sort(inMemoryArray, recordComparator);
	}


	
	
	@Override
	public void close() throws IOException {
		List<StreamingIterator<SamRecord>> iterators =new ArrayList<StreamingIterator<SamRecord>>(1 + tempFiles.size());
		SamWriter writer =null;
		try{
			sortInMemoryRecords();

			iterators.add(IteratorUtil.createStreamingIterator(new InMemoryStreamingIterator(currentInMemSize)));
			
			for(File tempFile : tempFiles){
				iterators.add(new StreamingSamRecordIterator(tempFile, encoding));
			}
			
			Iterator<SamRecord> sortedIterator = new MergedSortedRecordIterator<SamRecord>(iterators, recordComparator);
			writer = encoding.createPreSortedNoValidationOutputWriter(outputFile, header, indexer);
			while(sortedIterator.hasNext()){
				writer.writeRecord(sortedIterator.next());
			}
		}finally{
			for(StreamingIterator<?> iter : iterators){
				IOUtil.closeAndIgnoreErrors(iter);
			}
			IOUtil.closeAndIgnoreErrors(writer);
			clearInMemoryArray();
			//delete temp dir
			IOUtil.recursiveDelete(tmpDir);
		}
		
	}


	

	

	private final class InMemoryStreamingIterator implements StreamingIterator<SamRecord>{

		private final int length;
		private int counter=0;
		
		InMemoryStreamingIterator(int numOfInMemoryRecords){
			this.length = numOfInMemoryRecords;
		}
		
		@Override
		public boolean hasNext() {
			return counter<length;
		}

		@Override
		public void close() {
			//no-op
			
		}

		@Override
		public SamRecord next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			return inMemoryArray[counter++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}

	/**
	 * Iterates over a sam (or bam) encoded file as a {@link StreamingIterator}.
	 * @author dkatzel
	 *
	 */
	private static class StreamingSamRecordIterator extends AbstractBlockingStreamingIterator<SamRecord>{

		private final File samFile;
		private final Encoding encoding;
		
		public StreamingSamRecordIterator(File samFile, Encoding encoding) {
			this.samFile = samFile;
			this.encoding = encoding;
			this.start();
		}


		@Override
		protected void backgroundThreadRunMethod() throws RuntimeException {
			try {
				encoding.createNewNoValidationSamParser(samFile).accept(new SamVisitor() {
					
					@Override
					public void visitRecord(SamVisitorCallback callback, SamRecord record) {
						StreamingSamRecordIterator.this.blockingPut(record);
						
					}
					@Override
					public void visitRecord(SamVisitorCallback callback, SamRecord record,
							VirtualFileOffset start, VirtualFileOffset end) {
						StreamingSamRecordIterator.this.blockingPut(record);
						
					}
					
					
					@Override
					public void visitHeader(SamVisitorCallback callback, SamHeader header) {
						//no-op
						
					}
					
					@Override
					public void visitEnd() {
						//no-op
						
					}
					@Override
					public void halted() {
						
					}



					
				});
			} catch (IOException e) {
				throw new RuntimeException("error parsing temp sam file " + samFile.getAbsolutePath(), e);
			}
			
		}
		
	}

}
