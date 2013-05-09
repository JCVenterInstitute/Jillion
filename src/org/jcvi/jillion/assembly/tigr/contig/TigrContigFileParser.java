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
		TextLineParser inputStream =new TextLineParser(getInputStream());
		try{
			parse(visitor, inputStream, 0L);
		}finally{
			IOUtil.closeAndIgnoreErrors(inputStream);
		}
	}
	
	public abstract void accept(TigrContigFileVisitor visitor,TigrContigVisitorMemento memento) throws IOException;

	protected final void parse(TigrContigFileVisitor visitor,
			TextLineParser parser, long initialOffset) throws IOException {
		boolean inConsensus =true;
		TigrContigVisitor contigVisitor=null;
		TigrContigReadVisitor readVisitor=null;
		NucleotideSequenceBuilder currentBasesBuilder =new NucleotideSequenceBuilder();
		
		boolean keepParsing=true;
		AbstractTigrContigVisitorCallback callback=null;
		long currentOffset = initialOffset;
		while(keepParsing && parser.hasNextLine()){
			String line = parser.nextLine();
			Matcher newContigMatcher = NEW_CONTIG_PATTERN.matcher(line);
			if (newContigMatcher.find()) {
				if (readVisitor != null) {
					readVisitor.visitBasecalls(currentBasesBuilder.build());
					readVisitor.visitEnd();
				}
				if(callback !=null){
					keepParsing = callback.keepParsing();
				}
				if(keepParsing && contigVisitor !=null){
					contigVisitor.visitEnd();
				}
				if(callback !=null){
					keepParsing = callback.keepParsing();
				}
				readVisitor=null;	
				contigVisitor =null;
				if(keepParsing){
					inConsensus = true;
					String contigId = newContigMatcher.group(1);
					callback = createCallback(currentOffset);
					contigVisitor = visitor.visitContig(callback, contigId);
					currentBasesBuilder = new NucleotideSequenceBuilder();
				}
							
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
		
		if (readVisitor != null && keepParsing){
			readVisitor.visitBasecalls(currentBasesBuilder.build());
			readVisitor.visitEnd();
		}
		if(contigVisitor !=null){
			if(keepParsing){
				contigVisitor.visitEnd();
			}else{
				contigVisitor.halted();
			}
		}
		if(keepParsing){
			visitor.visitEnd();
		}else{
			visitor.halted();
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
        Range validRange = parseValidRange(newSequenceMatcher, dir);
   	 	
        return contigVisitor.visitRead(seqId, offset, dir, validRange);
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
			TextLineParser in=null;
			try{
				in = new TextLineParser(new RandomAccessFileInputStream(contigFile, startOffset));
				parse(visitor, in, startOffset);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
		
	}
	
	private static abstract class AbstractTigrContigVisitorCallback implements TigrContigVisitorCallback{
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
