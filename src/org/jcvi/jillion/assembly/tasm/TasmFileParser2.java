package org.jcvi.jillion.assembly.tasm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.tasm.TasmFileVisitor2.TasmContigVisitorCallback;
import org.jcvi.jillion.assembly.tasm.TasmFileVisitor2.TasmContigVisitorCallback.TasmContigVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public abstract class TasmFileParser2 {
    /**
     * Each contig data is separated by a pipe ('|').
     */
    private static final String END_OF_CONTIG = "|";
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+.*$)");
    
    public static TasmFileParser2 create(File tasmFile){
    	return new FileBasedTasmFileParser(tasmFile);
    }
    public static TasmFileParser2 create(InputStream in){
    	return new InputStreamBasedTasmFileParser(in);
    }
    private TasmFileParser2(){
    	//can not instantiate outside of this file
    }
    
    protected abstract void accept(TasmFileVisitor2 visitor) throws IOException;
    
    protected abstract void accept(TasmFileVisitor2 visitor, TasmContigVisitorMemento memento) throws IOException;
    
    protected final void parseTasm(TextLineParser parser, TasmFileVisitor2 visitor, long initialOffset) throws IOException{
         
    	long currentOffset=initialOffset;
    	long currentBeginContigOffset = initialOffset;
         ContigState currentContigState=new ContigState();
         ReadState currentReadState = null;
         AbstractCallback callback=null;
         TasmContigVisitor contigVisitor=null;
         
         while(parser.hasNextLine() && (callback==null || callback.keepParsing())) {
             String line = parser.nextLine();
             currentOffset+=line.length();
             Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
             
             if(matcher.find()){
                 String key = matcher.group(1);
                 String value = matcher.group(2).trim();
                 if(currentContigState!=null){
                	currentContigState.handleAttribute(key, value);                	 
                 }else{
                     currentReadState.handleAttribute(key, value);
                 }
             }else{
            	 if(currentContigState!=null){
            		 callback = createCallback(currentBeginContigOffset);
            		 contigVisitor =visitor.visitContig(callback, currentContigState.contigId);
            		 handleContigHeader(currentContigState, callback, contigVisitor);
            		 currentReadState=null;
            	 }
            	 currentContigState=null;
        		 boolean endOfRecord = isEndOfRecord(line);
        		 boolean endOfContig = isEndOfContig(line);
        		 if(endOfRecord || endOfContig){                    
                     handleRead(currentReadState, callback, contigVisitor);
            		 if(endOfContig){
            			 if(contigVisitor !=null){
	            			 if(callback.keepParsing()){
	            				 contigVisitor.visitEnd();
	            			 }else{
	            				 contigVisitor.visitIncompleteEnd();
	            			 }
            			 }
            			 if(callback.keepParsing()){
	            			 currentContigState=new ContigState();
	            			 currentBeginContigOffset = currentOffset;
            			 }
            		 }
            		 if(endOfRecord){
            			 currentReadState = new ReadState();
            		 }
        		 }
             }
             
         }
         if(currentContigState!=null){
    		 callback = createCallback(currentBeginContigOffset);
    		 contigVisitor =visitor.visitContig(callback, currentContigState.contigId);
    		 handleContigHeader(currentContigState, callback, contigVisitor);
    	 }
         handleRead(currentReadState, callback, contigVisitor);
         if(contigVisitor !=null){
			 if(callback.keepParsing()){
				 contigVisitor.visitEnd();
			 }else{
				 contigVisitor.visitIncompleteEnd();
			 }
		 }
         visitor.visitEnd();
        
    }
	protected void handleContigHeader(ContigState currentContigState,
			AbstractCallback callback, TasmContigVisitor contigVisitor) {
		if(callback.keepParsing() && contigVisitor !=null){
			 contigVisitor.visitConsensus(currentContigState.consensus);
			 if(callback.keepParsing() && currentContigState.caContigId !=null){
				 contigVisitor.visitCeleraId(currentContigState.caContigId);
			 }else{
				 contigVisitor.visitIncompleteEnd();
			 }
			 if(callback.keepParsing()){
				 contigVisitor.visitComments(
						 currentContigState.bacId, 
						 currentContigState.comment, 
						 currentContigState.comName, 
						 currentContigState.assemblyMethod, 
						 currentContigState.isCircular);
			 }else{
				 contigVisitor.visitIncompleteEnd();
			 }
			 if(callback.keepParsing()){
				 contigVisitor.visitCoverageData(currentContigState.numberOfReads, currentContigState.avgCoverage);
			 }else{
				 contigVisitor.visitIncompleteEnd();
			 }
			 if(callback.keepParsing()){
				 contigVisitor.visitLastEdited(currentContigState.editPerson, new Date(currentContigState.editDate));
			 }else{
				 contigVisitor.visitIncompleteEnd();
			 }
		 }
	}

	protected void handleRead(ReadState currentReadState,
			AbstractCallback callback, TasmContigVisitor contigVisitor) {
		if(currentReadState ==null || !callback.keepParsing()){
			return;
		}
		//end of current read
		 if(contigVisitor!=null){
			 final Direction dir;
			 final Range validRange;
			 if(currentReadState.seqRight < currentReadState.seqLeft){
				 dir = Direction.REVERSE;
				 validRange = Range.of(CoordinateSystem.RESIDUE_BASED, currentReadState.seqRight, currentReadState.seqLeft);
			 }else{
				 dir = Direction.FORWARD;
				 validRange = Range.of(CoordinateSystem.RESIDUE_BASED, currentReadState.seqLeft, currentReadState.seqRight);
			 }
			 TasmContigReadVisitor readVisitor = contigVisitor.visitRead(currentReadState.id, currentReadState.gappedStartOffset, 
					 									dir, validRange);
			 if(readVisitor !=null && callback.keepParsing()){
				 readVisitor.visitBasecalls(currentReadState.sequence);
				 readVisitor.visitEnd();
			 }
		 }
	}
    
    
    
    /**
     * @param line
     * @return
     */
    private static boolean isEndOfContig(String line) {
        return line.trim().equals(END_OF_CONTIG);
    }

    private static final boolean isEndOfRecord(String line) {
        return line.trim().isEmpty();
    }
    protected abstract AbstractCallback createCallback(long currentOffset);
    
    
    private static class OffsetMementoCallback extends AbstractCallback{
    

		private final long offset;
		
		public OffsetMementoCallback(long offset) {
			this.offset = offset;
		}
		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public TasmContigVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
    	
    }
    
    private static final class OffsetMemento implements TasmContigVisitorMemento{
    	private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		private final long getOffset() {
			return offset;
		}
    	
    }
    private static class NoMementoCallback extends AbstractCallback{

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public TasmContigVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create mementos");
		}
    	
    }
    
    private static abstract class AbstractCallback implements TasmContigVisitorCallback{
    	private volatile boolean keepParsing=true;   	
    	
    	@Override
		public void stopParsing() {
    		keepParsing = false;
			
		}



		public boolean keepParsing(){
    		return keepParsing;
    	}
    }


	private static class ContigState{
    	private String contigId;
    	private float avgCoverage=0F;
    	private NucleotideSequence consensus;
    	private Long caContigId=null;
    	private String comment, comName, editPerson, assemblyMethod;
    	private long editDate;
    	private boolean isCircular=false;
    	private int numberOfReads=0;
    	private int bacId;
    	
    	public void handleAttribute(String key, String value) throws IOException{
    		 if("asmbl_id".equals(key)){
    			 contigId =value;
             }
             else if("lsequence".equals(key)){
            	 consensus = new NucleotideSequenceBuilder(value).build();
             }else{
	    		TasmContigAttribute attribute = TasmContigAttribute.getAttributeFor(key);
	    		switch(attribute){
	    			case ASMBL_ID : contigId = value;
	    							break;
	    			case AVG_COVERAGE : avgCoverage = Float.parseFloat(value);
	    							break;
	    			case CA_CONTIG_ID :caContigId = Long.parseLong(value);
	    							break;
	    			case BAC_ID : bacId = Integer.parseInt(value);
	    						break;
	    			case COM_NAME : comName = value;
	    					break;
	    			case COMMENT : comment = value;
	    				break;
	    			case EDIT_DATE : try {
										editDate = TasmUtil.EDIT_DATE_FORMAT.parse(value).getTime();
									} catch (ParseException e) {
										throw new IOException("error parsing edit date " + value);
									}
	    				break;
	    			case EDIT_PERSON : editPerson = value;
	    				break;
	    			case GAPPED_CONSENSUS : new NucleotideSequenceBuilder(value).build();
	    				break;
	    			case IS_CIRCULAR : isCircular =value.equals("1");
	    				break;
	    			case METHOD : assemblyMethod = value;
	    						break;
	    			case NUMBER_OF_READS : numberOfReads = Integer.parseInt(value);
	    								break;
					default : //do nothing
	    		}
	    	}
    	}
    }
    
	private static class ReadState{
		private NucleotideSequence sequence;
		private String id;
		private long gappedStartOffset;
		private int seqLeft;
		private int seqRight;
		public void handleAttribute(String key, String value) throws IOException{
			if("lsequence".equals(key)){
           	 sequence = new NucleotideSequenceBuilder(value).build();
            }else{
	    		TasmReadAttribute attribute = TasmReadAttribute.getAttributeFor(key);
	    		switch(attribute){
	    			case GAPPED_SEQUENCE : sequence = new NucleotideSequenceBuilder(value).build();
	    									break;
	    			case NAME : id = value.trim();
	    							break;
	    			case CONTIG_START_OFFSET : gappedStartOffset = Long.parseLong(value);
	    											break;
	    			case SEQUENCE_LEFT : seqLeft = Integer.parseInt(value);
	    										break;
	    			case SEQUENCE_RIGHT : seqRight = Integer.parseInt(value);
										break;
	    			default : //do nothing
	    		}
			}
		}
	}
	
	
	private static final class FileBasedTasmFileParser extends TasmFileParser2{
    	private final File tasmFile;
    	
    	public FileBasedTasmFileParser(File tasmFile){
    		this.tasmFile = tasmFile;
    	}

		@Override
		protected AbstractCallback createCallback(long offset) {
			return new OffsetMementoCallback(offset);
		}

		@Override
		protected void accept(TasmFileVisitor2 visitor) throws IOException {
			InputStream in = new BufferedInputStream(new FileInputStream(tasmFile));
			TextLineParser parser = new TextLineParser(in);
			try{
				parseTasm(parser, visitor, 0L);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser, in);
			}			
		}

		@Override
		protected void accept(TasmFileVisitor2 visitor,
				TasmContigVisitorMemento memento) throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type");
			}
			
			long startOffset = ((OffsetMemento)memento).getOffset();
			long fileLength =tasmFile.length();
			if(fileLength<=startOffset){
				throw new IllegalArgumentException("memento seeks beyond file");
			}
			RandomAccessFile randomAccessFile = new RandomAccessFile(tasmFile, "r");
			InputStream in=null;
			try{
				randomAccessFile.seek(startOffset);
				in = new BufferedInputStream( new RandomAccessFileInputStream(randomAccessFile));
				TextLineParser parser = new TextLineParser(in);
				parseTasm(parser, visitor, startOffset);
			}finally{
				IOUtil.closeAndIgnoreErrors(in,randomAccessFile);
			}
			
		}
		
		
    	
    }
	
	private static final class InputStreamBasedTasmFileParser extends TasmFileParser2{
    	private final OpenAwareInputStream in;
    	
    	public InputStreamBasedTasmFileParser(InputStream in){
    		this.in = new OpenAwareInputStream(new BufferedInputStream(in));
    	}

		@Override
		protected AbstractCallback createCallback(long offset) {
			return new NoMementoCallback();
		}

		@Override
		protected void accept(TasmFileVisitor2 visitor) throws IOException {
			if(!in.isOpen()){
				throw new IOException("inputstream is closed");
			}
			TextLineParser parser = new TextLineParser(in);
			try{
				parseTasm(parser, visitor, 0L);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser, in);
			}			
		}

		@Override
		protected void accept(TasmFileVisitor2 visitor,
				TasmContigVisitorMemento memento) throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}
    	
    }
}
