package org.jcvi.jillion.fasta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaVisitorCallback.Memento;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code FastaFileParser} will parse a single 
 * fasta encoded file and call the appropriate
 * visitXXX methods on the given {@link FastaFileVisitor}.
 * @author dkatzel
 *
 */
public class FastaFileParser {
	private static final Pattern DEFLINE_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
	 
	private final File fastaFile;	
	private InputStream inputStream;
	
	
	public FastaFileParser(File fastaFile) throws IOException {
		if(fastaFile==null){
			throw new NullPointerException("fasta file can not be null");
		}
		this.fastaFile = fastaFile;
		this.inputStream = new BufferedInputStream(new FileInputStream(fastaFile));
	}
	public FastaFileParser(InputStream inputStream) throws IOException {
		if(inputStream==null){
			throw new NullPointerException("inputStream can not be null");
		}
		this.fastaFile = null;
		this.inputStream = inputStream;
	}
	
	public void accept(FastaFileVisitor visitor) throws IOException{
		checkNotNull(visitor);
		TextLineParser parser = new TextLineParser(inputStream);
		try{
			parseFile(parser, 0L, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(inputStream);
		}
	}
	protected void checkNotNull(FastaFileVisitor visitor) {
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
	}
	public void accept(Memento memento, FastaFileVisitor visitor) throws IOException{
		if(!(memento instanceof OffsetMemento)){
			throw new IllegalStateException("unknown memento instance : "+memento);
		}
		inputStream = new FileInputStream(fastaFile);
		long startOffset = ((OffsetMemento)memento).getOffset();
		IOUtil.blockingSkip(inputStream, startOffset);
		TextLineParser parser = new TextLineParser(inputStream);
		try{
			parseFile(parser, startOffset, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(inputStream);
		}
	}
	
	private void parseFile(TextLineParser parser, long currentOffset,
			FastaFileVisitor visitor) throws IOException {
		boolean keepParsing=true;
		FastaRecordVisitor recordVisitor =null;
		AbstractFastaVisitorCallback callback = createNewCallback();
		while(keepParsing && parser.hasNextLine()){
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
						keepParsing=callback.keepParsing();
						if(!keepParsing){
							//need to set recordVisitor to null
							//so we don't call visitEnd() again
							recordVisitor=null;
							continue;
						}
					}
					String id = matcher.group(1);
		            String comment = matcher.group(3);		            
		            callback.updateOffset(currentOffset);
		            recordVisitor = visitor.visitDefline(callback, id, comment);
		            keepParsing=callback.keepParsing();
				}else{
					//not a defline use current record visitor
					if(recordVisitor !=null){
						recordVisitor.visitBodyLine(line);
					}
				}
			}
			currentOffset +=line.length();
		}
		if(recordVisitor !=null){
			recordVisitor.visitEnd();
		}
		visitor.visitEnd();
	}

	private AbstractFastaVisitorCallback createNewCallback() {
		if(fastaFile==null){
			return NoMementoCallback.INSTANCE;
		}
		return new MementoCallback();
	}
	
	private static abstract class AbstractFastaVisitorCallback implements FastaVisitorCallback{
		private volatile boolean keepParsing=true;
		
		@Override
		public void stopParsing() {
			keepParsing=false;
			
		}

		public final boolean keepParsing() {
			return keepParsing;
		}
		abstract void updateOffset(long offset);
	}
	
	private static class NoMementoCallback extends AbstractFastaVisitorCallback{

		private static NoMementoCallback INSTANCE = new NoMementoCallback();
		
		
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public Memento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}

		@Override
		void updateOffset(long offset) {
			//no-op			
		}
		
	}
	
	private class MementoCallback extends AbstractFastaVisitorCallback{

		private long offset=0L;
		
		@Override
		public void updateOffset(long offset) {
			this.offset = offset;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public Memento createMemento() {
			return new OffsetMemento(offset);
		}
		
	}
	
	private static class OffsetMemento implements Memento{
		private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		public final long getOffset() {
			return offset;
		}
		
	}
}
