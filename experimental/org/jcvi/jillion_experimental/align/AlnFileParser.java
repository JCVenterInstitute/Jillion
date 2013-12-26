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
package org.jcvi.jillion_experimental.align;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion_experimental.align.AlnGroupVisitor.ConservationInfo;
import org.jcvi.jillion_experimental.align.AlnVisitor.AlnVisitorCallback;
import org.jcvi.jillion_experimental.align.AlnVisitor.AlnVisitorCallback.AlnVisitorMemento;

/**
 * {@code AlnFileParser} is a utility class that can 
 * parse .aln alignment files like those
 * created by clustal.
 * 
 * @author dkatzel
 *
 *
 */
public abstract class AlnFileParser implements AlnParser{

	
    /**
     * Regular expression of string that contains
     * valid nucleotide or amino acids.
     */
    private static final String REGEX = "^([^*\\s]+)\\s+([\\-ACGTNVHDBWMRSYKILFQPEX*]+)";
    private static final Pattern ALIGNMENT_PATTERN = Pattern.compile(REGEX);
    private static final Pattern CONSERVATION_PATTERN = Pattern.compile("\\s+([-:\\. \\*]+)$");
    
    
    public static AlnParser create(File alnFile) throws IOException{
    	return new FileBasedAlnParser(alnFile);
    }
    public static AlnParser create(InputStream alnStream) throws IOException{
    	return new InputStreamBasedAlnParser(alnStream);
    }
    
    private AlnFileParser(){
		//private constructor.
	}
    
    
    /**
     * @param conservationString
     * @param numberOfBasesPerGroup
     * @return
     */
    private static List<ConservationInfo> parseConservationInfo(
            String conservationString, int numberOfBasesPerGroup) {
        final String paddedString = createPaddedConservationString(conservationString, numberOfBasesPerGroup);
        List<ConservationInfo> result = new ArrayList<ConservationInfo>(numberOfBasesPerGroup);
        for(int i=0; i< paddedString.length(); i++){
        	result.add(ConservationInfo.parse(paddedString.charAt(i)));            
        }
        return result;
    }
    
    
    protected void parse(AlnVisitor visitor, InputStream in) throws IOException{
		TextLineParser parser = new TextLineParser(in);
		AtomicBoolean keepParsing=new AtomicBoolean(true);
		boolean eofReached=false;
		try{
			 while(keepParsing.get() && parser.hasNextLine()){
				 AlnVisitorCallback callback = createCallBack(parser, keepParsing);
				 	Group group = Group.getNextGroup(parser);
				 	if(group !=null){				 		
				 		group.accept(visitor,callback, keepParsing);				 		
				 	}
			 }
			 eofReached=!parser.hasNextLine();
		}finally{
			if(eofReached && keepParsing.get()){
				 visitor.visitEnd();
			 }else{
				 visitor.halted();
			 }
			IOUtil.closeAndIgnoreErrors(parser);
		}
	}
    protected abstract AlnVisitorCallback createCallBack(TextLineParser parser, AtomicBoolean keepParsing);
    
    
	/**
     * Aln format uses spaces to denote not conserved regions,
     * this is hard to parse out using regular expressions if 
     * the conservation string is supposed to START with spaces.
     * By using the expected number of basecalls in this group,
     * we can create a padded string with the correct number of leading
     * spaces.
     * @param conservationString
     * @param numberOfBasesPerGroup
     * @return
     */
    private static String createPaddedConservationString(
            String conservationString, int numberOfBasesPerGroup) {
        int length = conservationString.length();
        int padding = numberOfBasesPerGroup-length;
        
        final String paddedString;
        if(padding>0){
            String format = "%"+padding+"s%s";
            paddedString= String.format(format, "",conservationString);
        }else{
            paddedString = conservationString;
        }
        return paddedString;
    }
    
    private static final class Group{
    	private final Map<String,String> lines;
    	private final List<ConservationInfo> info;
    	
    	public Group(Map<String, String> lines, List<ConservationInfo> info) {
			this.lines = lines;
			this.info = info;
		}



		public void accept(AlnVisitor visitor, AlnVisitorCallback callback, AtomicBoolean keepParsing) {
			AlnGroupVisitor groupVisitor = visitor.visitGroup(lines.keySet(), callback);
			if(groupVisitor !=null){
				for(Entry<String,String> entry : lines.entrySet()){
					if(keepParsing.get()){
						groupVisitor.visitAlignedSegment(entry.getKey(), entry.getValue());
					}
				}
				if(keepParsing.get()){
					groupVisitor.visitConservationInfo(info);
				}
				if(keepParsing.get()){
					groupVisitor.visitEndGroup();
				}
			}
		}



		static Group getNextGroup(TextLineParser parser) throws IOException{
    		Map<String,String> lines = new LinkedHashMap<String, String>();
    		boolean done=false;
    		boolean foundGroup=false;
    		List<ConservationInfo> info = null;
    		int numberOfBasesPerGroup=0;
    		
    		while(parser.hasNextLine() && !done){
    			String line = parser.nextLine();
    			if(foundGroup && line.trim().isEmpty()){
    				//we found the end of the group
    				done=true;
    			}
	            Matcher alignmentMatcher =  ALIGNMENT_PATTERN.matcher(line);
	            if(alignmentMatcher.find()){
	            	foundGroup=true;
	            	numberOfBasesPerGroup = alignmentMatcher.group(2).length();
	            	lines.put(alignmentMatcher.group(1), alignmentMatcher.group(2));
	            }else{
	            	Matcher conservationMatcher = CONSERVATION_PATTERN.matcher(line);
	                if(conservationMatcher.find()){
	                    String conservationString = conservationMatcher.group(1);
	                    info = parseConservationInfo(conservationString,numberOfBasesPerGroup);
	                    done=true;
	                }
	            }
    		}
    		if(lines.isEmpty()){
    			//didn't find anything
    			return null;
    		}
    		if(info ==null){
    			//no conservation line means not conserved?
    			
    			info = new ArrayList<AlnGroupVisitor.ConservationInfo>(numberOfBasesPerGroup);
    			for(int i=0; i<info.size(); i++){
    				info.add(ConservationInfo.NOT_CONSERVED);
    			}
    		}
    		return new Group(lines,info);
    	}
    }
    
    
    private static final class InputStreamBasedAlnParser extends AlnFileParser{
    	private final OpenAwareInputStream in;
    	
    	public InputStreamBasedAlnParser(InputStream in){
    		this.in = new OpenAwareInputStream(in);
    	}
		@Override
		public boolean canParse() {
			return in.isOpen();
		}

		@Override
		public void parse(AlnVisitor visitor) throws IOException {
			try{
				parse(visitor,in);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}

		@Override
		public void parse(AlnVisitor visitor, AlnVisitorMemento memento)
				throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}

		@Override
		protected AlnVisitorCallback createCallBack(TextLineParser parser,
				AtomicBoolean keepParsing) {
			return new NoMementoCallback(keepParsing);
		}
    	
    }
    
    private static final class NoMementoCallback implements AlnVisitorCallback{
    	private final AtomicBoolean keepParsing;
    	
    	
		public NoMementoCallback(AtomicBoolean keepParsing) {
			this.keepParsing = keepParsing;
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public AlnVisitorMemento createMemento() {
			throw new UnsupportedOperationException("mementos not supported");
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
			
		}
    	
    }
    
    private static final class FileBasedAlnParser extends AlnFileParser{

    	private final File alnFile;
    	
		public FileBasedAlnParser(File alnFile) throws IOException {
			if(!alnFile.exists()){
				throw new FileNotFoundException(alnFile.getAbsolutePath());
			}
			this.alnFile = alnFile;
		}

		@Override
		public boolean canParse() {
			return true;
		}

		@Override
		protected AlnVisitorCallback createCallBack(TextLineParser parser,
				AtomicBoolean keepParsing) {
			return new AlnFileVisitorCallback(parser.getPosition(), keepParsing);
		}

		@Override
		public void parse(AlnVisitor visitor) throws IOException {
			InputStream in =null;
			try{
				in = new BufferedInputStream(new FileInputStream(alnFile));
				parse(visitor,in);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
		
		

		@Override
		public void parse(AlnVisitor visitor, AlnVisitorMemento memento)
				throws IOException {
			if(!(memento instanceof AlnFileMemento)){
				throw new IllegalStateException("unknown memento type" + memento);
			}
			AlnFileMemento alnFileMemento = (AlnFileMemento)memento;
			if(alnFileMemento.getOuterType()!=this){
				throw new IllegalStateException("invalid memento: was not created by this parser");
			}
			
			
			InputStream in =null;
			try{
				in = new BufferedInputStream(new RandomAccessFileInputStream(alnFile, alnFileMemento.offset));
				parse(visitor,in);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
    	
		private class AlnFileVisitorCallback implements AlnVisitorCallback{
			private final AtomicBoolean keepParsing;
			private final long offset;
			
			public AlnFileVisitorCallback(long offset, AtomicBoolean keepParsing) {
				this.keepParsing = keepParsing;
				this.offset = offset;
			}

			@Override
			public boolean canCreateMemento() {
				return true;
			}

			@Override
			public AlnVisitorMemento createMemento() {
				return new AlnFileMemento(offset);
			}

			@Override
			public void haltParsing() {
				keepParsing.set(false);
				
			}
			
		}
		private class AlnFileMemento implements AlnVisitorMemento{
			private long offset;

			public AlnFileMemento(long offset) {
				this.offset = offset;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + (int) (offset ^ (offset >>> 32));
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof AlnFileMemento)) {
					return false;
				}
				AlnFileMemento other = (AlnFileMemento) obj;
				if (!getOuterType().equals(other.getOuterType())) {
					return false;
				}
				if (offset != other.offset) {
					return false;
				}
				return true;
			}

			private FileBasedAlnParser getOuterType() {
				return FileBasedAlnParser.this;
			}
			
		}
    }
}
