package org.jcvi.jillion.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaVisitorCallback.Memento;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public class FastaFileParser2 {
	private static final Pattern DEFLINE_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
	 
	private final File fastaFile;	
	private InputStream inputStream;
	
	
	public FastaFileParser2(File fastaFile) throws IOException {
		this.fastaFile = fastaFile;
		this.inputStream = new FileInputStream(fastaFile);
	}
	public FastaFileParser2(InputStream inputStream) throws IOException {
		this.fastaFile = null;
		this.inputStream = inputStream;
	}
	
	public void accept(FastaFileVisitor2 visitor) throws IOException{
		TextLineParser parser = new TextLineParser(inputStream);
		try{
			parseFile(parser, 0L, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(inputStream);
		}
	}
	public void accept(Memento memento, FastaFileVisitor2 visitor) throws IOException{
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
			FastaFileVisitor2 visitor) throws IOException {
		boolean keepParsing=true;
		FastaRecordVisitor recordVisitor =null;
		while(keepParsing && parser.hasNextLine()){
			String line=parser.nextLine();
			Matcher matcher = DEFLINE_LINE_PATTERN.matcher(line);
			if(matcher.find()){
				if(recordVisitor !=null){
					recordVisitor.visitEnd();
				}
				String id = matcher.group(1);
	            String comment = matcher.group(3);
	            if(comment !=null){
	            	comment = comment.trim();
	            	//consider a comment of only whitespace to 
	            	//be not a comment
	            	if(comment.isEmpty()){
	            		comment=null;
	            	}
	            }
	            AbstractFastaVisitorCallback callback = createNewCallback(currentOffset);
	            recordVisitor = visitor.visitDefline(callback, id, comment);
	            keepParsing=callback.keepParsing();
			}else{
				//not a defline use current record visitor
				if(recordVisitor !=null){
					recordVisitor.visitBodyLine(line);
				}
			}
			currentOffset +=line.length();
		}
		if(recordVisitor !=null){
			recordVisitor.visitEnd();
		}
		visitor.visitEnd();
	}

	private AbstractFastaVisitorCallback createNewCallback(long currentOffset) {
		if(fastaFile==null){
			return new NoMementoCallback();
		}
		return new MementoCallback(currentOffset);
	}
	
	private abstract class AbstractFastaVisitorCallback implements FastaVisitorCallback{
		private volatile boolean keepParsing=true;
		
		@Override
		public void stopParsing() {
			keepParsing=false;
			
		}

		public final boolean keepParsing() {
			return keepParsing;
		}
	}
	
	private class NoMementoCallback extends AbstractFastaVisitorCallback{

		
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public Memento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private class MementoCallback extends AbstractFastaVisitorCallback{

		private final long offset;
		
		public MementoCallback(long offset) {
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
