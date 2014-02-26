package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableStreamingIterator;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;

abstract class AbstractReSortSamFileWriter implements SamWriter {

	


	private final SamHeader header;
	private final Comparator<SamRecord> recordComparator;
	private final File tmpDir;
	private final File outputFile;
	
	private SamRecord[] inMemoryArray;
	private int currentInMemSize;
	private final int maxRecordsToKeepInMemory;
	private final List<File> tempFiles = new ArrayList<File>();
	private final SamAttributeValidator attributeValidator;
	
	private final String tmpFileSuffix;
	public AbstractReSortSamFileWriter(File outputFile, File tmpDirRoot, SamHeader header, int maxRecordsToKeepInMemory, SamAttributeValidator attributeValidator, String tmpFileSuffix) throws IOException {
		
		if(maxRecordsToKeepInMemory <0){
			throw new IllegalArgumentException("max records to keep in memory must be >=1");
		}
		if(attributeValidator ==null){
			throw new NullPointerException("attribute valiator can not be null");
		}
		if(!".bam".equals(tmpFileSuffix)  && !".sam".equals(tmpFileSuffix)){
			throw new NullPointerException("tmpFileSuffix must be either .bam or .sam" + tmpFileSuffix);
		}
		this.maxRecordsToKeepInMemory = maxRecordsToKeepInMemory;
		
		this.header = header;
		recordComparator = header.createRecordComparator();
		if(recordComparator ==null){
			throw new NullPointerException("SortOrder must create a non-null comparator " + header.getSortOrder());
		}
		
		IOUtil.mkdirs(outputFile.getParentFile());
		tmpDir = File.createTempFile("jillion", "samWriterTmp", tmpDirRoot);
		 //now that we have a new empty file
        //we need to delete it and then create it again, but this
        //time as a directory
        if(!tmpDir.delete() || !tmpDir.mkdir()){
            throw new IOException("Could not create temp directory: " + tmpDir.getAbsolutePath());
        }
        inMemoryArray = new SamRecord[maxRecordsToKeepInMemory];
        currentInMemSize=0;
        this.outputFile = outputFile;
        this.attributeValidator = attributeValidator;
        this.tmpFileSuffix = tmpFileSuffix;
	}


	@Override
	public void writeRecord(SamRecord record) throws IOException {
		persistInMemoryCacheIfNeeded();
		try{
			header.validRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		
		inMemoryArray[currentInMemSize++] = record;

	}


	private void persistInMemoryCacheIfNeeded() throws IOException {
		if(currentInMemSize == maxRecordsToKeepInMemory){
			
			sortInMemoryRecords();
			
			File tempFile= File.createTempFile(outputFile.getName(), tmpFileSuffix, tmpDir);
			tempFiles.add(tempFile);
			SamWriter writer = createOutputWriter(tempFile, header);
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
		List<PeekableStreamingIterator<SamRecord>> iterators =null;
		SamWriter writer =null;
		try{
			sortInMemoryRecords();
			
			iterators = new ArrayList<PeekableStreamingIterator<SamRecord>>(1 + tempFiles.size());
			
			iterators.add(IteratorUtil.createPeekableStreamingIterator(new InMemoryStreamingIterator(currentInMemSize)));
			
			for(File tempFile : tempFiles){
				iterators.add(IteratorUtil.createPeekableStreamingIterator(new StreamingSamRecordIterator(tempFile)));
			}
			
			Iterator<SamRecord> sortedIterator = new MergedSortedRecordIterator(iterators, recordComparator);
			writer = createOutputWriter(outputFile, header);
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


	


	protected abstract SamWriter createOutputWriter(File out, SamHeader header) throws IOException;


	

	

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
	
	public static class MergedSortedRecordIterator implements Iterator<SamRecord> {
				private final List<PeekableStreamingIterator<SamRecord>> iterators;
				
				private SamRecord next;
				private final SortedSamRecordElementComparator comparator;
				private final List<SortedSamRecordElement> elementList;
				public MergedSortedRecordIterator(List<PeekableStreamingIterator<SamRecord>> iterators, Comparator<SamRecord> comparator) {
					this.iterators = iterators;
					this.comparator = new SortedSamRecordElementComparator(comparator);
					elementList = new ArrayList<SortedSamRecordElement>(iterators.size());
					
					next= getNext();
				}
				
				private SamRecord getNext(){
					elementList.clear();
					for(PeekableStreamingIterator<SamRecord> iter : iterators){
						if(iter.hasNext()){
							//we peek instead of next()
							//incase we don't pick this record yet
							elementList.add(new SortedSamRecordElement(iter.peek(), iter));
						}
					}
					if(elementList.isEmpty()){
						return null;
					}
					Collections.sort(elementList, comparator);
					SortedSamRecordElement element= elementList.get(0);
					//advance iterator
					element.source.next();
					return element.record;
				}

				@Override
				public boolean hasNext() {
					return next!=null;
				}
				
				
				@Override
				public SamRecord next() {
					//don't need to check has next
					//since we can make sure we don't call it incorrectly
					SamRecord ret= next;
					next = getNext();
					return ret;
				}
				
				@Override
				public void remove() {
					// TODO Auto-generated method stub
				
				}

}
	
	private static class SortedSamRecordElement{
		SamRecord record;
		PeekableStreamingIterator<SamRecord> source;
		
		public SortedSamRecordElement(SamRecord record,
				PeekableStreamingIterator<SamRecord> source) {
			this.record = record;
			this.source = source;
		}

		@Override
		public String toString() {
			return "SortedSamRecordElement [record=" + record + ", source="
					+ source + "]";
		}
		
	}
	
	private static class SortedSamRecordElementComparator implements Comparator<SortedSamRecordElement>{
		private final Comparator<SamRecord> comparator;
		

		public SortedSamRecordElementComparator(Comparator<SamRecord> comparator) {
			this.comparator = comparator;
		}


		@Override
		public int compare(SortedSamRecordElement o1, SortedSamRecordElement o2) {
			return comparator.compare(o1.record, o2.record);
		}
		
	}
	
	private static class StreamingSamRecordIterator extends AbstractBlockingStreamingIterator<SamRecord>{

		private final File samFile;
		
		
		public StreamingSamRecordIterator(File samFile) {
			this.samFile = samFile;
			this.start();
		}


		@Override
		protected void backgroundThreadRunMethod() throws RuntimeException {
			try {
				SamParserFactory.create(samFile, NullSamAttributeValidator.INSTANCE).accept(new SamVisitor() {
					
					@Override
					public void visitRecord(SamRecord record) {
						StreamingSamRecordIterator.this.blockingPut(record);
						
					}
					
					@Override
					public void visitHeader(SamHeader header) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void visitEnd() {
						// TODO Auto-generated method stub
						
					}
				});
			} catch (IOException e) {
				throw new RuntimeException("error parsing temp sam file " + samFile.getAbsolutePath(), e);
			}
			
		}
		
	}

}
