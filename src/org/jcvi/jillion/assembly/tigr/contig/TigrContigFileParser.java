/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor.TigrContigVisitorCallback;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor.TigrContigVisitorCallback.TigrContigVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code TigrContigFileParser} can parse
 * TIGR "{@literal .contig}" formatted files that are produced
 * by legacy TIGR assembly programs.
 * @author dkatzel
 *
 */
public abstract class TigrContigFileParser {

	private static final Pattern NEW_CONTIG_PATTERN = Pattern.compile("##(\\S+).+");
	private static final Pattern NEW_READ_PATTERN = Pattern.compile("#(\\S+)\\((-?\\d+)\\)\\s+\\[(.*)\\].+\\{(-?\\d+) (-?\\d+)\\}.+");
	  
	/**
	 * Creates a new {@link TigrContigFileParser}
	 * instance that will parse the given contig file.
	 * @param contigFile the contig file to parse.
	 * @return a new {@link TigrContigFileParser};
	 * will never be null.
	 * @throws IOException if the given contig file does
	 * not exist
	 * @throws NullPointerException if the contig file is null.
	 */
	public static TigrContigFileParser create(File contigFile) throws IOException{
		return new FileBasedTigrContigParser(contigFile);
	}
	/**
	 * Creates a new {@link TigrContigFileParser}
	 * instance that will parse the given {@link InputStream} 
	 * that contains {@literal .contig} formatted data.
	 * @param contigFileStream the {@link InputStream} 
	 * that containing {@literal .contig} formatted data.
	 * @return a new {@link TigrContigFileParser};
	 * will never be null.
	 * @throws NullPointerException if the inputstream is null.
	 */
	public static TigrContigFileParser create(InputStream contigFileStream){
		return new InputStreamBasedTigrContigParser(contigFileStream);
	}
	private TigrContigFileParser(){
		//can not instantiate outside of this file
	}
	/**
	 * Parse the contig file from the beginning and call the 
	 * appropriate visit methods on the given visitor.
	 * @param visitor the visitor instance to call
	 * the visit methods on as the file is parsed.
	 * @throws IOException if there are any problems parsing
	 * the contig file.
	 * @throws NullPointerException if visitor is null.
	 */
	public void accept(TigrContigFileVisitor visitor) throws IOException{
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
		TextLineParser lineParser =new TextLineParser(getInputStream());
		try{
			parse(visitor, lineParser);
		}finally{
			IOUtil.closeAndIgnoreErrors(lineParser);
		}
	}
	/**
	 * Parse the contig file from starting from
	 * the position provided by the given memento and call the 
	 * appropriate visit methods on the given visitor.
	 * @param visitor the visitor instance to call
	 * the visit methods on as the file is parsed.
	 * @throws IOException if there are any problems parsing
	 * the contig file.
	 * @throws NullPointerException if either visitor  or memento are null.
	 * @throws UnsupportedOperationException if the parser implementation
	 * does not support mementos.
	 * @throws IllegalArgumentException if the memento instance was produced
	 * by this class.
	 */
	public abstract void accept(TigrContigFileVisitor visitor,TigrContigVisitorMemento memento) throws IOException;

	protected final void parse(TigrContigFileVisitor visitor,
			TextLineParser parser) throws IOException {
		
		State state = new State(visitor,parser);
		Handler[] handlers = Handler.values();
		while(state.notDone()){
			String peekedLine = state.peekLine();			
			for(Handler handler : handlers){
				if(handler.handle(state, peekedLine)){
					break;
				}
			}
			state.advanceLine();
		}
		state.finishedParsing();
	}
	/**
	 * {@code State} keeps track of our
	 * state as we read the lines in the file.
	 * 
	 * @author dkatzel
	 *
	 */
	private final class State{
		private boolean inConsensus =true;
		private TigrContigVisitor contigVisitor=null;
		private TigrContigReadVisitor readVisitor=null;
		private NucleotideSequenceBuilder currentBasesBuilder =new NucleotideSequenceBuilder();
	
		private AbstractTigrContigVisitorCallback callback=null;
		private final TextLineParser parser;
		private final TigrContigFileVisitor visitor;
		
		
		public State(TigrContigFileVisitor visitor, TextLineParser parser){
			this.visitor= visitor;
			this.parser = parser;
		}

		public void visitReadBases(){
			if (readVisitor != null) {
				readVisitor.visitBasecalls(currentBasesBuilder.build());
				readVisitor.visitEnd();
			}
			currentBasesBuilder = new NucleotideSequenceBuilder();
		}
		
		public void appendBasecalls(String basecalls){
			currentBasesBuilder.append(basecalls);
		}
		
		/**
		 * We have finished parsing the file
		 * (either we got to the end of the file
		 * or parsing was halted) call the final
		 * visit methods on any visitors still
		 * referenced depending.
		 */
		public void finishedParsing(){
			if (readVisitor != null && keepParsing()){
				readVisitor.visitBasecalls(currentBasesBuilder.build());
				readVisitor.visitEnd();
			}
			if(contigVisitor !=null){
				if(keepParsing()){
					contigVisitor.visitEnd();
				}else{
					contigVisitor.halted();
				}
			}
			if(keepParsing()){
				visitor.visitEnd();
			}else{
				visitor.halted();
			}
		}
		
		public boolean keepParsing(){
			if(callback !=null){
				return callback.keepParsing();
			}
			return true;
		}
		
		public boolean notDone(){
			return keepParsing() && parser.hasNextLine();
		}
		
		public void advanceLine() throws IOException{
			parser.nextLine();
		}
		public String peekLine(){
			return parser.peekLine();
		}
		
		public void visitEndContig(){
			if(contigVisitor !=null){
				contigVisitor.visitEnd();
			}
			readVisitor=null;	
			contigVisitor =null;
		}
	

		public void visitNewContig(String contigId) {
			inConsensus = true;
			callback = createCallback(parser.getPosition());
			contigVisitor = visitor.visitContig(callback, contigId);
			currentBasesBuilder = new NucleotideSequenceBuilder();
			
		}

		

		public void beginNewRead(String seqId, int offset, Direction dir,
				Range validRange) {
			if (inConsensus && contigVisitor != null) {
				contigVisitor.visitConsensus(currentBasesBuilder.build());
			}
			if(readVisitor !=null){
				readVisitor.visitBasecalls(currentBasesBuilder.build());
				readVisitor.visitEnd();
			}
			currentBasesBuilder = new NucleotideSequenceBuilder();
			inConsensus = false;
			
			if(contigVisitor==null){
				readVisitor=null;
			}else{	       
				readVisitor= contigVisitor.visitRead(seqId, offset, dir, validRange);
			}
			
		}
		
	}
	/**
	 * {@code Handler} instances handle the types of
	 * lines that exist in a contig file.  Each Handler
	 * only can handle one type of line.
	 * The order of the Handles is defined from the
	 * most restrictive handler to the most permissive handler.
	 * 
	 * 
	 * @author dkatzel
	 *
	 */
	private enum Handler{
		NEW_CONTIG{

			@Override
			protected boolean handle(State state, String line){
				Matcher matcher = NEW_CONTIG_PATTERN.matcher(line);
				if(!matcher.find()){
					return false;
				}
				state.visitReadBases();				
				
				if(state.keepParsing()){
					state.visitEndContig();					
				}
				if(state.keepParsing()){
					
					String contigId = matcher.group(1);
					state.visitNewContig(contigId);
				}
				return true;
			}
			
		},
		NEW_READ{
			@Override
			protected boolean handle(State state, String line){
				Matcher matcher = NEW_READ_PATTERN.matcher(line);
				if(!matcher.find()){
					return false;
				}
				String seqId = matcher.group(1);
		        int offset = Integer.parseInt(matcher.group(2));
		        Direction dir= parseComplimentedFlag(matcher)?Direction.REVERSE: Direction.FORWARD;
		        Range validRange = parseValidRange(matcher, dir);
		        
				state.beginNewRead(seqId, offset, dir, validRange);
				return true;
			}
		},
		BASECALL_LINE{

			@Override
			protected boolean handle(State state, String line) {
				state.appendBasecalls(line);
				return true;
			}
			
		}
		;
		/**
		 * Try to handle the given line given the current {@link State}.
		 * @param state the current state.
		 * @param line the peeked line to handle.
		 * @return {@code true} if we successfully handled this line;
		 * {@code false} otherwise.
		 */
		protected abstract boolean handle(State state, String line);
	}
	
	protected abstract AbstractTigrContigVisitorCallback createCallback(long currentOffset);

	
	
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

		public FileBasedTigrContigParser(File contigFile) throws FileNotFoundException {
			if(contigFile==null){
				throw new NullPointerException("contig file can not be null");
			}
			if(!contigFile.exists()){
				throw new FileNotFoundException(contigFile.getAbsolutePath());
			}
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
			if(memento ==null){
				throw new NullPointerException("memento can not be null");
			}
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type, must use instance created by this parser");
			}
			long startOffset = ((OffsetMemento)memento).getOffset();
			TextLineParser in=null;
			try{
				in = new TextLineParser(new RandomAccessFileInputStream(contigFile, startOffset));
				parse(visitor, in);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
		
	}
	
	private  abstract static class AbstractTigrContigVisitorCallback implements TigrContigVisitorCallback{
		private volatile boolean keepParsing=true;
		
		@Override
		public void haltParsing() {
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
