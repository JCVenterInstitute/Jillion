/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.io.LineParser;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code FastaFileParser} will parse a single 
 * fasta encoded file and call the appropriate
 * visitXXX methods on the given {@link FastaVisitor}.
 * 
 * As of Jillion 4.2, {@code FastaFileParser} supports
 * non-redundant text fasta files like
 * the ones described in
 * <a href="ftp://ftp.ncbi.nih.gov/blast/db/README">ftp://ftp.ncbi.nih.gov/blast/db/README</a>.
 * If non-redundant records are encountered, then the visitXXX methods will be called
 * in a way such that it will appear as if they were redundantly listed.  The non-redundant
 * defline will be split and each identical sequence will be visited separately with each
 * of the many ids for it.  Creating {@link org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento}s
 * (if supported by the {@link FastaParser} implementation) are also non-redundant aware
 * and will correctly only visit the subset of non-redundant records according to when
 * the memento was created.
 * @author dkatzel
 *
 */
public abstract class FastaFileParser implements FastaParser{
	/**
	 * Pattern to match to find the defline for each record in
	 * the fasta file.  Group 1 is the id and Group 3 is the optional
	 * comment which will return null if there is no comment. (Group 2 is not to be used)
	 */
	private static final Pattern DEFLINE_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
	
	private static final Pattern REDUNDANT_DEFLINE_LINE_PATTERN = Pattern.compile("^(\\S+)(\\s+(.*))?");

	private static final char CONTROL_A = 1;
	/**
	 * Create a new {@link FastaFileParser} instance
	 * that will parse the given fasta encoded
	 * file.
	 * @param fastaFile the file to parse.
	 * @throws NullPointerException if the file is null.
	 * @throws IOException if the file is not readable or does not exist.
	 */
	public static FastaParser create(File fastaFile) throws IOException{
		return new FileFastaParser(fastaFile);
	}
	/**
         * Create a new {@link FastaFileParser} instance
         * that will parse the fasta encoded
         * data from the given {@link InputStreamSupplier}.
         * 
         * @param inputStreamSupplier the {@link InputStreamSupplier} to use
         * to get the inputStreams of fasta encoded data.
         * @throws NullPointerException if the inputStreamSupplier is null.
         * 
         * @since 5.0
         */
        public static FastaParser create(InputStreamSupplier inputStreamSupplier) throws IOException{
                return new FileFastaParser(inputStreamSupplier);
        }
	/**
	 * Create a new {@link FastaFileParser} instance
	 * that will parse the given fasta encoded
	 * inputStream.  Please Note that inputStream implementations
	 * of the FastaFileParser can not create {@link FastaVisitorMemento}s
	 * or use {@link #parse(FastaVisitor, FastaVisitorMemento)}
	 * method.
	 * @param inputStream the {@link InputStream} to parse.
	 * @throws NullPointerException if inputStream is null.
	 */
	public static FastaParser create(InputStream inputStream){
		return new InputStreamFastaParser(inputStream);
	}
	/**
	 * Parse the fasta file starting from the beginning 
	 * of the file (or {@link InputStream}) and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if visitor is null.
	 */
	public void parse(FastaVisitor visitor) throws IOException{
		checkNotNull(visitor);
		InputStream in = null;		
		try{
			in = getInputStream();			
			TextLineParser parser = new TextLineParser(in);
			parseFile(parser, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	/**
	 * Create an inputStream that starts reading at the beginning
	 * of the fasta file.
	 * @return an {@link InputStream} can not be null.
	 * @throws IOException if there is a problem creating the {@link InputStream}.
	 */
	protected abstract InputStream getInputStream()  throws IOException;
	/**
	 * Get the original source file if it is known and exists.
	 * @return an {@link Optional} that wraps a the file that will be parsed;
	 * will never return null but may be empty if the file is not known
	 * or does not exist.  
	 * 
	 * @since 5.3
	 */
	public abstract Optional<File> getFile();
	
	protected void checkNotNull(FastaVisitor visitor) {
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
	}
	final void parseFile(TextLineParser parser, FastaVisitor visitor) throws IOException {
		parseFile(parser, visitor, 0);
	}
	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
	final void parseFile(TextLineParser parser, FastaVisitor visitor, int initialRedundantIndex) throws IOException {
		AtomicBoolean keepParsing=new AtomicBoolean(true);
		FastaRecordVisitor recordVisitor =null;
		long currentOffset=parser.getPosition();
		AbstractFastaVisitorCallback callback = createNewCallback(currentOffset, keepParsing);
		int currentInitialRedundantStartIndex= initialRedundantIndex;
		
		while(keepParsing.get() && parser.hasNextLine()){
			String line=parser.nextLine();
			String trimmedLine = line.trim();
			if(!trimmedLine.isEmpty()){
				Matcher matcher = DEFLINE_LINE_PATTERN.matcher(trimmedLine);
				if(matcher.find()){
					if(recordVisitor !=null){
						recordVisitor.visitEnd();
						//need to check again the keep parsing flag 
						//incase the callback was used to stop in the previous
						//called to visitEnd()
						if(!keepParsing.get()){
							//need to set recordVisitor to null
							//so we don't call visitEnd() again
							recordVisitor=null;
							continue;
						}
					}
					
					//2015-07-09: check for control-A characters
					//which is used in non-redundant fastas
					if(line.indexOf(CONTROL_A) == -1){
						//normal fasta record
						String id = matcher.group(1);
			            String comment = matcher.group(3);		            
			            callback = createNewCallback(currentOffset, keepParsing);
			            recordVisitor = visitor.visitDefline(callback, id, comment);
					}else{
						handleNonRedundantRecord(parser, trimmedLine, visitor, currentOffset, keepParsing, currentInitialRedundantStartIndex);
						currentInitialRedundantStartIndex = 0;
					}
					
				
				}else{
					//not a defline use current record visitor
					if(recordVisitor !=null){
						recordVisitor.visitBodyLine(line);
					}
				}
			}
			currentOffset =parser.getPosition();
		}
		
		handleEndOfFile(visitor, keepParsing, recordVisitor);

	}
	private void handleNonRedundantRecord(LineParser parser, String defline, 
			FastaVisitor visitor, long offsetOfBeginningOfDefline, 
			AtomicBoolean keepParsing, int initialRedundantStartIndex) throws IOException {
		//there are multiple redundant records included
		//on this defline that each have the same sequence but different ids.
		
		//read the whole record body and then call the visit methods
		//in case the visitor wants to skip some of the records
		//split on control-A character (ASCII value of 1)
		//substring(1) to remove '>' char
		String[] redundantDeflines = defline.substring(1).split("[\u0001]");
		List<String> bodyLines = new ArrayList<String>();
		
		while(parser.hasNextLine()){
			String nextLine = parser.peekLine();
			if(nextLine.charAt(0) == '>'){
				//next defline found
				break;
			}
			//consume next line we just peeked
			bodyLines.add(parser.nextLine());
		}
		
		for(int i=initialRedundantStartIndex; keepParsing.get() && i< redundantDeflines.length ; i++){
			String redundantDefline = redundantDeflines[i];
		
			Matcher matcher = REDUNDANT_DEFLINE_LINE_PATTERN.matcher(redundantDefline);
			if(!matcher.find()){
				throw new IOException("error parsing redundant defline [" + i + "] from " + defline);
			}
			String id = matcher.group(1);
            String comment = matcher.group(3);
            //need to make a new type of callback that notes which of the redundant records we
            //are parsing since the offset is the same!
            AbstractFastaVisitorCallback redundantCallback = createNewRedundantCallback(offsetOfBeginningOfDefline, i , keepParsing);
            
            FastaRecordVisitor recordVisitor = visitor.visitDefline(redundantCallback, id, comment);
            if(recordVisitor !=null){
            	for(int j=0; keepParsing.get() && j< bodyLines.size(); j++){
            		recordVisitor.visitBodyLine(bodyLines.get(j));
            	}
            	if(keepParsing.get()){
            		recordVisitor.visitEnd();
            	}else{
            		recordVisitor.halted();
            	}
            }
		}
		
		
	}
	protected void handleEndOfFile(FastaVisitor visitor,
			AtomicBoolean keepParsing, FastaRecordVisitor recordVisitor) {
		if(recordVisitor !=null){
			if(keepParsing.get()){
				recordVisitor.visitEnd();
			}else{
				recordVisitor.halted();
			}
		}
		//need to check keep parsing flag
		//for record visitor and visitor
		//separately in case the recordVisitor.visitEnd()
		//calls haltParsing
		if(keepParsing.get()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}

	protected abstract AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing);
	protected abstract AbstractFastaVisitorCallback createNewRedundantCallback(long offsetOfBeginningOfDefline, int i, AtomicBoolean keepParsing);
	
	private abstract static class AbstractFastaVisitorCallback implements FastaVisitorCallback{
		private final AtomicBoolean keepParsing;
		
		public AbstractFastaVisitorCallback(AtomicBoolean keepParsing) {
			this.keepParsing = keepParsing;
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
			
		}

	}
	
	private static class NoMementoCallback extends AbstractFastaVisitorCallback{

		
		
		public NoMementoCallback(AtomicBoolean keepParsing) {
			super(keepParsing);
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private static class MementoCallback extends AbstractFastaVisitorCallback{

		private final long offset;
		
		public MementoCallback(long offset, AtomicBoolean keepParsing){
			super(keepParsing);
			this.offset = offset;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
		
	}
	
	private static class RedundantMementoCallback extends AbstractFastaVisitorCallback{

		private final long offset;
		private final int redundantIndex;
		
		public RedundantMementoCallback(long offset, int redundantIndex, AtomicBoolean keepParsing){
			super(keepParsing);
			this.offset = offset;
			this.redundantIndex = redundantIndex;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			return new RedundantOffsetMemento(offset, redundantIndex);
		}
		
	}
	
	private static class OffsetMemento implements FastaVisitorMemento{
		private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		public final long getOffset() {
			return offset;
		}
		
	}
	
	private static class RedundantOffsetMemento extends OffsetMemento{
		private final int redundantIndex;

		public RedundantOffsetMemento(long offset, int redundantIndex) {
			super(offset);
			this.redundantIndex = redundantIndex;
		}

		public int getRedundantIndex() {
			return redundantIndex;
		}

		
	}
	
	private static class FileFastaParser extends FastaFileParser{
		private final InputStreamSupplier fileSupplier;
		
		private final File fastaFile;
		public FileFastaParser(File fastaFile) throws IOException{
			this.fileSupplier = InputStreamSupplier.forFile(fastaFile);
			this.fastaFile = fastaFile;
		}
		
		
		
		
		public FileFastaParser(InputStreamSupplier fileSupplier) {
		    Objects.requireNonNull(fileSupplier);
                    this.fileSupplier = fileSupplier;
                    this.fastaFile = null;
                }



        
                @Override
                public Optional<File> getFile() {
                    return Optional.ofNullable(fastaFile);
                }




        @Override
		public boolean isReadOnceOnly() {
			return false;
		}


		@Override
		public boolean canCreateMemento() {
			return true;
		}


		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing) {
			return new MementoCallback(currentOffset, keepParsing);
		}
		
		
		
		public void parse(FastaVisitor visitor, FastaVisitorMemento memento) throws IOException{
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalStateException("unknown memento instance : "+memento);
			}
			
			long startOffset = ((OffsetMemento)memento).getOffset();
			
                    try (InputStream inputStream = fileSupplier.get(InputStreamSupplier.InputStreamReadOptions.builder()
                    													.start(startOffset)
                    													.nestedDecompress(true)
                    													.build())) {
        
                        TextLineParser parser = new TextLineParser(inputStream,
                                startOffset);
                        if (memento instanceof RedundantOffsetMemento) {
                            int redundantIndex = ((RedundantOffsetMemento) memento)
                                    .getRedundantIndex();
                            parseFile(parser, visitor, redundantIndex);
                        } else {
                            parseFile(parser, visitor);
                        }
                    }
		}
		@Override
		protected InputStream getInputStream() throws IOException {
			//start parsing from beginning of file.
			return fileSupplier.get(InputStreamSupplier.InputStreamReadOptions.builder()
					.nestedDecompress(true)
					.build());
		}
		@Override
		public boolean canParse() {
			return true;
		}


		@Override
		protected AbstractFastaVisitorCallback createNewRedundantCallback(
				long offsetOfBeginningOfDefline, int redundantIndex,
				AtomicBoolean keepParsing) {
			return new RedundantMementoCallback(offsetOfBeginningOfDefline, redundantIndex, keepParsing);
		}
		
		
	}
	private static class InputStreamFastaParser extends FastaFileParser{
		private final OpenAwareInputStream inputStream;
		private boolean hasParsedBefore= false;
		
		public InputStreamFastaParser(InputStream inputStream) {
			this.inputStream = new OpenAwareInputStream(inputStream);
		}
		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing) {
			return new NoMementoCallback(keepParsing);
		}
		
		@Override
		protected AbstractFastaVisitorCallback createNewRedundantCallback(long offsetOfBeginningOfDefline, int i, AtomicBoolean keepParsing) {
			return new NoMementoCallback(keepParsing);
		}
		
		
		@Override
		public boolean isReadOnceOnly() {
			//can only parse Stream once
			return true;
		}
		@Override
		public synchronized void parse(FastaVisitor visitor) throws IOException {
			//wrap in synchronized block so we only
			//can parse one visitor at a time (probably at all)
			super.parse(visitor);
		}

		@Override
		public void parse(FastaVisitor visitor, FastaVisitorMemento memento)
				throws IOException {
			//we probably will never see this in real usage
			//since inputstream implementation can't make mementors...
			throw new UnsupportedOperationException("can not use mementos with inputstream");
		}

		@Override
		protected InputStream getInputStream() throws IOException {
			//this is a work around to fix a regression
			//where we give an empty stream
			//first time should not throw an error
			//even if there is nothing to parse.
			if(!hasParsedBefore){
				return inputStream;
			}
			hasParsedBefore = true;
			if(canParse()){
				return inputStream;
			}
			throw new IllegalStateException("can not accept visitor - inputstream is closed");			
		}
		@Override
		public boolean canParse() {
			return inputStream.isOpen();
		}
		@Override
		public boolean canCreateMemento() {
			return false;
		}
        @Override
        public Optional<File> getFile() {
            // no file? if we have a file we shouldn't use this implementation
            return Optional.empty();
        }
		
		
		
	}
}
