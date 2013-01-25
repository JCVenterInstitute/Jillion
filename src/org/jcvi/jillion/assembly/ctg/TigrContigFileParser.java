package org.jcvi.jillion.assembly.ctg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.ctg.TigrContigFileVisitor.TigrContigVisitorCallback;
import org.jcvi.jillion.assembly.ctg.TigrContigFileVisitor.TigrContigVisitorCallback.TigrContigVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public abstract class TigrContigFileParser {

	  private static final Pattern NEW_CONTIG_PATTERN = Pattern.compile("##(\\S+).+");
	    private static final Pattern NEW_READ_PATTERN = Pattern.compile("#(\\S+)\\((-?\\d+)\\)\\s+\\[(.*)\\].+\\{(-?\\d+) (-?\\d+)\\}.+");
	  
	    
	public static TigrContigFileParser create(File contigFile){
		return new FileBasedTigrContigParser(contigFile);
	}
	
	public static TigrContigFileParser create(InputStream contigFileStream){
		return new InputStreamBasedTigrContigParser(contigFileStream);
	}
	private TigrContigFileParser(){
		//can not instantiate outside of this file
	}
	
	public void accept(TigrContigFileVisitor visitor) throws IOException{
		InputStream inputStream = getInputStream();
		try{
			parse(visitor, inputStream, 0L);
		}finally{
			IOUtil.closeAndIgnoreErrors(inputStream);
		}
	}
	
	public abstract void accept(TigrContigFileVisitor visitor,TigrContigVisitorMemento memento) throws IOException;

	protected final void parse(TigrContigFileVisitor visitor,
			InputStream inputStream, long currentOffset) throws IOException {
		TextLineParser parser = new TextLineParser(inputStream);
		boolean inConsensus =true;
		TigrContigVisitor contigVisitor=null;
		TigrContigReadVisitor readVisitor=null;
		NucleotideSequenceBuilder currentBasesBuilder =new NucleotideSequenceBuilder();
		
		boolean keepParsing=true;
		AbstractTigrContigVisitorCallback callback=null;
		while(keepParsing && parser.hasNextLine()){
			String line = parser.nextLine();
			Matcher newContigMatcher = NEW_CONTIG_PATTERN.matcher(line);
			if (newContigMatcher.find()) {
				if (readVisitor != null) {
					readVisitor.visitBasecalls(currentBasesBuilder.build());
					readVisitor.visitEnd();
				}
				if(contigVisitor !=null){
					contigVisitor.visitEnd();
				}
				inConsensus = true;
				String contigId = newContigMatcher.group(1);
				callback = createCallback(currentOffset);
				contigVisitor = visitor.visitContig(callback, contigId);
				currentBasesBuilder = new NucleotideSequenceBuilder();
				readVisitor=null;				
			} else {
				Matcher newSequenceMatcher = NEW_READ_PATTERN.matcher(line);
				if (newSequenceMatcher.find()) {
					if (inConsensus && contigVisitor != null) {
						contigVisitor.visitConsensus(currentBasesBuilder.build());
					}
					if(readVisitor !=null){
						readVisitor.visitBasecalls(currentBasesBuilder.build());
						readVisitor.visitEnd();
					}
					currentBasesBuilder = new NucleotideSequenceBuilder();
					inConsensus = false;
					readVisitor = fireVisitNewRead(newSequenceMatcher,
							contigVisitor);
					
				} else {
					currentBasesBuilder.append(line);
				}
			}
			if(callback !=null){
				keepParsing = callback.keepParsing();
			}
			currentOffset +=line.length();
		}
		
		if (readVisitor != null) {
			if(keepParsing){
				readVisitor.visitBasecalls(currentBasesBuilder.build());
				readVisitor.visitEnd();
			}else{
				readVisitor.visitIncompleteEnd();
			}
		}
		if(contigVisitor !=null){
			if(keepParsing){
				contigVisitor.visitEnd();
			}else{
				contigVisitor.visitIncompleteEnd();
			}
		}
		if(keepParsing){
			visitor.visitEnd();
		}else{
			visitor.visitIncompleteEnd();
		}
	}
	
	protected abstract AbstractTigrContigVisitorCallback createCallback(long currentOffset);

	private static TigrContigReadVisitor fireVisitNewRead(Matcher newSequenceMatcher,  TigrContigVisitor contigVisitor) {
		if(contigVisitor==null){
			return null;
		}
        String seqId = newSequenceMatcher.group(1);
        int offset = Integer.parseInt(newSequenceMatcher.group(2));
        Direction dir= parseComplimentedFlag(newSequenceMatcher)?Direction.REVERSE: Direction.FORWARD;
        TigrContigReadVisitor readVisitor =contigVisitor.visitRead(seqId, offset, dir);
        if(readVisitor !=null){
        	 Range validRange = parseValidRange(newSequenceMatcher, dir);
        	 readVisitor.visitValidRange(validRange);
        }
       return readVisitor;
 }
	
	 private static Range parseValidRange(Matcher newSequenceMatcher,
	            Direction dir) {
	            int left = Integer.parseInt(newSequenceMatcher.group(4));
	           int right = Integer.parseInt(newSequenceMatcher.group(5));
	           Range validRange;
	           if(dir == Direction.REVERSE){
	               validRange = Range.of(CoordinateSystem.RESIDUE_BASED,right, left);
	           }
	           else{
	               validRange = Range.of(CoordinateSystem.RESIDUE_BASED,left, right);
	           }
	        return validRange;
	    }
	 
	 private static boolean parseComplimentedFlag(Matcher newSequenceMatcher) {
	        return !newSequenceMatcher.group(3).isEmpty();
	    }
	
	protected abstract InputStream getInputStream() throws IOException;
	
	private static class InputStreamBasedTigrContigParser extends TigrContigFileParser{
		private final OpenAwareInputStream in;
		
		public InputStreamBasedTigrContigParser(InputStream in){
			this.in = new OpenAwareInputStream(in);
		}

		@Override
		public void accept(TigrContigFileVisitor visitor,
				TigrContigVisitorMemento memento) throws IOException {
			throw new UnsupportedOperationException("inputstream parser does not support mementos");
			
		}

		@Override
		protected AbstractTigrContigVisitorCallback createCallback(
				long currentOffset) {
			return NoMementoCallback.INSTANCE;
		}

		@Override
		protected InputStream getInputStream() throws IOException {
			if(!in.isOpen()){
				throw new IOException("inputstream is closed");
			}
			return in;
		}
		
	}
	
	private static class FileBasedTigrContigParser extends TigrContigFileParser{
		private final File contigFile;

		public FileBasedTigrContigParser(File contigFile) {
			this.contigFile = contigFile;
		}

		@Override
		protected InputStream getInputStream() throws IOException {
			return new BufferedInputStream(new FileInputStream(contigFile));
		}

		@Override
		protected AbstractTigrContigVisitorCallback createCallback(long currentOffset) {
			return new MementoCallback(currentOffset);
		}

		@Override
		public void accept(TigrContigFileVisitor visitor,
				TigrContigVisitorMemento memento) throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type, must use instance created by this parser");
			}
			long startOffset = ((OffsetMemento)memento).getOffset();
			RandomAccessFile randomAccessFile = new RandomAccessFile(contigFile, "r");
			InputStream in=null;
			try{
				randomAccessFile.seek(startOffset);
				in = new RandomAccessFileInputStream(randomAccessFile);
				parse(visitor, in, startOffset);
			}finally{
				IOUtil.closeAndIgnoreErrors(in, randomAccessFile);
			}
			
		}
		
	}
	
	private static abstract class AbstractTigrContigVisitorCallback implements TigrContigVisitorCallback{
		private volatile boolean keepParsing=true;
		
		@Override
		public void stopParsing() {
			keepParsing=false;
			
		}

		public final boolean keepParsing() {
			return keepParsing;
		}
	}
	
	private static class NoMementoCallback extends AbstractTigrContigVisitorCallback{

		static NoMementoCallback INSTANCE = new NoMementoCallback();
		
		
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public TigrContigVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private static class MementoCallback extends AbstractTigrContigVisitorCallback{

		private final long offset;
		
		public MementoCallback(long offset){
			this.offset = offset;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public TigrContigVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
		
	}
	
	private static class OffsetMemento implements TigrContigVisitorMemento{
		private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		public final long getOffset() {
			return offset;
		}
		
	}
}
