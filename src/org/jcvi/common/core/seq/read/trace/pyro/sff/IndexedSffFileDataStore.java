package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code IndexedSffFileDataStore} is an implementation 
 * of {@link SffDataStore} that only stores an index containing
 * byte offsets to the various {@link Flowgram}s contained
 * in a single sff file.  This allows for large files to provide
 * random access without taking up much memory. The down side is each flowgram
 * must be re-parsed each time and the sff file must exist and not
 * get altered during the entire lifetime of this object.
 * <p/>
 * {@link IndexedSffFileDataStore} is limited to only {@link Integer#MAX_VALUE}
 * of reads.  Attempting to create an instance of
 * {@link IndexedSffFileDataStore} using an sff file with more than that many reads will
 * cause an {@link IllegalArgumentException} to be thrown {@link #create(File)}
 * and {@link #createVisitorBuilder(File)} for more information about when
 * the exception will be thrown.
 * @see #canCreateIndexedDataStore(File)
 * @author dkatzel
 *
 */
public final class IndexedSffFileDataStore{
	/**
	 * Partially parses the given sff file to get the number of 
	 * reads in the header and checks to make sure the total number
	 * of reads can be correctly indexed.
	 * @param sffFile the sff file to check.
	 * @return {@code true} if the sff file can be used
	 * to create a valid {@link IndexedSffFileDataStore}; {@code false}
	 * otherwise.
	 * @throws IOException if there is a problem parsing the sff file.
	 * 
	 */
	public static boolean canCreateIndexedDataStore(File sffFile) throws IOException{
		NumberOfReadChecker numReadChecker = new NumberOfReadChecker();
		SffFileParser.parseSFF(sffFile, numReadChecker);
		return numReadChecker.numberOfReads <=Integer.MAX_VALUE;
	}
	/**
     * Create a new empty {@link SffFileVisitorDataStoreBuilder}
     * that will create an {@link IndexedSffFileDataStore} 
     * once the builder has been built.  Only the 
     * given sff file should be used with to populate/index
     * the returned builder.  Please make sure that the given
     * sffFile can be used to create an indexedSffFileDataStore.
     * A valid datastore can not be created if there are 
     * more than {@link Integer#MAX_VALUE} reads.  If such
     * a file is given to the builder when the file is visited
     * downstream, it will throw an {@link IllegalArgumentException}.
     * 
     * @param sffFile the sffFile to parse.  NOTE: 
     * the file isn't actually parsed in this method.  The builder
     * will only store a reference to this file for future
     * use when it needs to re-parse after indexing has occurred.
     * @return a new SffFileVisitorDataStoreBuilder, never null.
	 * @throws FileNotFoundException if sffFile does not exist.
     * @throws NullPointerException if sffFile is null.
     * @see #canCreateIndexedDataStore(File)
     */
	public static SffFileVisitorDataStoreBuilder createVisitorBuilder(File sffFile) throws FileNotFoundException{
		return new FullPassIndexedSffVisitorBuilder(sffFile);
	}
	/**
	 * Create a new {@link SffDataStore} instance which only indexes
	 * byte offsets for each read.
	 * @param sffFile the sff file to create a datastore for.
	 * @return a new {@link SffDataStore} instance; never null.
	 * @throws IOException if there is a problem reading the file.
	 * @throws IllegalArgumentException if the given sffFile
	 * has more than {@link Integer#MAX_VALUE} reads.
	 * @throws NullPointerException if sffFile is null.
	 * @throws IllegalArgumentException if sffFile does not exist.
	 * @see #canCreateIndexedDataStore(File)
	 */
	public static SffDataStore create(File sffFile) throws IOException{
		//TODO put manifest datastore here
		
		//do full pass if can't use manifest
		FullPassIndexedSffVisitorBuilder builder= new FullPassIndexedSffVisitorBuilder(sffFile);
		SffFileParser.parseSFF(sffFile, builder);
		return builder.build();
	}
	private static final class NumberOfReadChecker implements SffFileVisitor{
		private long numberOfReads=Long.MAX_VALUE;
		@Override
		public void visitFile() {}

		@Override
		public void visitEndOfFile() {}

		@Override
		public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
			numberOfReads= commonHeader.getNumberOfReads();
			return CommonHeaderReturnCode.STOP;
		}

		@Override
		public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
			return ReadHeaderReturnCode.STOP;
		}

		@Override
		public ReadDataReturnCode visitReadData(SffReadData readData) {
			return ReadDataReturnCode.STOP;
		}
		
	}
	/**
	 * {@code FullPassIndexedSffVisitorBuilder} parses
	 * all the read data in the file and computes
	 * byte offsets of each record
	 * by re-encoding each header and read data  to temp arrays
	 * and accumulating resulting byte lengths.  We need to do this
	 * if there isn't a manifest in the sff file or
	 * we don't know how to parse the manifest.
	 * @author dkatzel
	 *
	 */
	private static final class FullPassIndexedSffVisitorBuilder implements SffFileVisitorDataStoreBuilder{
	 private int numberOfFlowsPerRead=0;
	  private long currentOffset;
	  
	  private IndexedFileRange indexRanges;
	  private int encodedReadLength=0;
	  private String currentReadId;
	  private final File sffFile;
	  
	  private FullPassIndexedSffVisitorBuilder(File sffFile) throws FileNotFoundException{
		  if(sffFile==null){
			  throw new NullPointerException("sff file can not be null");
		  }
		  if(!sffFile.exists()){
	    		throw new FileNotFoundException("sff file does not exist");
	    	}
		  this.sffFile = sffFile;
	  }
	  @Override
	public void visitFile() {
		  currentOffset=0;
		
	}
	@Override
	public void visitEndOfFile() {
		//ignore
		
	}
	@Override
	public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
		this.numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
		if(commonHeader.getNumberOfReads() > Integer.MAX_VALUE){
			throw new IllegalArgumentException("too many reads in sff file to index > Integer.MAX_VALUE");
		}
		indexRanges = new DefaultIndexedFileRange((int)commonHeader.getNumberOfReads());
		try {
			currentOffset +=SffWriter.writeCommonHeader(commonHeader, new ByteArrayOutputStream());
		} catch (IOException e) {
			throw new IllegalStateException("error computing number of bytes", e);
		}
		
		return CommonHeaderReturnCode.PARSE_READS;
	}
	@Override
	public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
		try {
			encodedReadLength=SffWriter.writeReadHeader(readHeader, new ByteArrayOutputStream());
			currentReadId=readHeader.getId();
			return ReadHeaderReturnCode.PARSE_READ_DATA;
		} catch (IOException e) {
			throw new IllegalStateException("error computing number of bytes", e);
		}
		
	}
	@Override
	public ReadDataReturnCode visitReadData(SffReadData readData) {
		try {
			encodedReadLength += SffWriter.writeReadData(readData, new ByteArrayOutputStream());
		} catch (IOException e) {
			throw new IllegalStateException("error computing number of bytes", e);
		}
		indexRanges.put(currentReadId, Range.createOfLength(currentOffset, encodedReadLength));
		currentOffset+=encodedReadLength;
		return ReadDataReturnCode.PARSE_NEXT_READ;
	}
	@Override
	public SffDataStore build() {
		return new FullPassIndexedSffFileDataStore(sffFile, numberOfFlowsPerRead, indexRanges);
	}
	@Override
	public SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram) {
		throw new UnsupportedOperationException("index builder can not add arbitrary flowgrams");
	}
	    
	}
	
	private static class FullPassIndexedSffFileDataStore implements SffDataStore{
		private static final SffReadHeaderDecoder READ_HEADER_CODEC =DefaultSffReadHeaderDecoder.INSTANCE;
		private static final SffReadDataDecoder READ_DATA_CODEC =DefaultSffReadDataDecoder.INSTANCE;
		    
		private final IndexedFileRange fileRanges;
		private final int numberOfFlowsPerRead;
		private final File sffFile;
		
		private FullPassIndexedSffFileDataStore(File sffFile,  int numberOfFlowsPerRead,
				IndexedFileRange fileRanges) {
			this.sffFile = sffFile;
			this.numberOfFlowsPerRead = numberOfFlowsPerRead;
			this.fileRanges = fileRanges;
		}
	
		@Override
		public CloseableIterator<String> getIds() throws DataStoreException {
			try {
				return LargeSffFileDataStore.create(sffFile).getIds();
			} catch (FileNotFoundException e) {
				throw new DataStoreException("error creating id iterator",e);
			}
		}
	
		@Override
		public Flowgram get(String id) throws DataStoreException {
			
			SffFileVisitorDataStoreBuilder builder = DefaultSffFileDataStore.createVisitorBuilder();
			builder.visitFile();
			try {
				InputStream in = IOUtil.createInputStreamFromFile(sffFile, fileRanges.getRangeFor(id));
				 DataInputStream dataIn = new DataInputStream(in);
				 SffReadHeader readHeader = READ_HEADER_CODEC.decodeReadHeader(dataIn);
				 final int numberOfBases = readHeader.getNumberOfBases();
	             SffReadData readData = READ_DATA_CODEC.decode(dataIn,
	                             numberOfFlowsPerRead,
	                             numberOfBases);
	             builder.visitReadHeader(readHeader);
	             builder.visitReadData(readData);
	             return builder.build().get(id);
			} catch (IOException e) {
				throw new DataStoreException("error trying to get flowgram "+ id);
			}
		}
	
		@Override
		public boolean contains(String id) throws DataStoreException {
			return fileRanges.contains(id);
		}
	
		@Override
		public int size() throws DataStoreException {
			return fileRanges.size();
		}
	
		@Override
		public boolean isClosed() throws DataStoreException {
			return fileRanges.isClosed();
		}
	
		@Override
		public void close() throws IOException {
			fileRanges.close();
			
		}
	
		@Override
		public CloseableIterator<Flowgram> iterator() {
			try {
				return LargeSffFileDataStore.create(sffFile).iterator();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("error creating iterator",e);
			}
		}
	}
	
}
