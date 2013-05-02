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
/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback.AceFileVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code AceFileParser} contains methods for parsing
 * ACE formatted files.
 * @author dkatzel
 * @see <a href = "http://www.phrap.org/consed/distributions/README.20.0.txt">Consed documentation which contains the ACE FILE FORMAT</a>
 */
public abstract class AceFileParser implements AceHandler {
	private static Pattern HEADER_PATTERN = Pattern.compile("^AS\\s+(\\d+)\\s+(\\d+)");
	private AceFileParser(){
		//private constructor.
	}
	
	public static AceHandler create(File aceFile){
		return new FileBasedParser(aceFile);
	}
	/**
	 * Create a new Parser object that will parse
	 * the given inputStream.
	 * the inputStream will be closed at the end
	 * of the first call to {@link #accept(AceFileVisitor)}.
	 * @param aceFileStream  the {@link InputStream} to parse;
	 * should never be null.
	 * @return a new {@link AceFileParser} instance;
	 * will never be null.
	 * @throws NullPointerException if aceFileStream is null.
	 */
	public static AceHandler create(InputStream aceFileStream){
		return new InputStreamParser(aceFileStream);
	}
    /**
	 * {@inheritDoc}
	 */
    @Override
	public abstract void accept(AceFileVisitor visitor) throws IOException;
    
    
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void accept(AceFileVisitor visitor, AceFileVisitorMemento memento) throws IOException;
     
    /**
     * Parse the given {@link InputStream} containing ace encoded data
     * and call the appropriate methods on the given AceFileVisitor.
     * This method may be used to parse partial ace data,
     * for example, an {@link InputStream} containing only formatted ace data
     * for a single contig.
     * If the entire inputStream is parsed, then it will automatically
     * be closed, however if there is an error while reading the {@link InputStream},
     * then the {@link InputStream} will be left open.
     * @param inputStream the inputStream to parse, can not be null.
     * @param visitor the visitor to be visited, can not be null.
     * @throws IOException if there is a problem reading the ace file.
     * @throws NullPointerException if either the aceFile or the visitor are {@code null}.
     */
    public void accept(InputStream inputStream, AceFileVisitor visitor) throws IOException{
        if(inputStream ==null){
            throw new NullPointerException("input stream can not be null");
        }
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        AceParserState parserState = AceParserState.create(inputStream, visitor, null);
        parseAceData(parserState, visitor);
        
    }
    
    
	protected final void parseAceData(AceParserState parserState,
			AceFileVisitor visitor) throws IOException {
		while(!parserState.done()){
            parserState.parseNextSection();
        }
        parserState.handleEndOfParsing();
       // visitor.visitEnd();
	}
   
    /**
     * {@code ParserState} keeps track of the where
     * we are in an ace file (in the first contig, 
     * what read number we are on etc...) as well
     * which parts of the  ace file the 
     * {@link AceFileVisitor} implementation
     * wants to visit.
     * 
     * @author dkatzel
     *
     */
    private static class AceParserState implements Closeable{
    	
        final AceFileVisitor fileVisitor;
        private AceContigVisitor currentContigVisitor;
        private AceContigReadVisitor currentReadVisitor;
        
        final TextLineParser parser;
        private final AtomicBoolean stopParsing = new AtomicBoolean(false);
        private boolean inAContig;
        private int expectedNumberOfReads;
        private int numberOfReadsSeen;
        private boolean inConsensusQualities=false;
        private boolean readReadPoritionOfContig=false;
        
        private QualitySequenceBuilder currentQualitySequenceBuilder;
        
        private final AceFileVisitorCallbackFactory callbackFactory;
        /**
         * This is the offset into the inputstream
         * where the current section starts.
         */
        private long startPositionOfCurrentSection=0L;
        
        public static AceParserState create(InputStream in, AceFileVisitor visitor, AceFileVisitorCallbackFactory callbackFactory, long startOffset) throws IOException{
        	
        	AceParserState parserState= create(new TextLineParser(new BufferedInputStream(in)), visitor, callbackFactory);        	
        	parserState.startPositionOfCurrentSection = startOffset;
        	return parserState;
        }
        public static AceParserState create(InputStream in, AceFileVisitor visitor, AceFileVisitorCallbackFactory callbackFactory) throws IOException{
        	
        	return create(new TextLineParser(new BufferedInputStream(in)), visitor, callbackFactory);        	
        }
        public static AceParserState create(TextLineParser parser, AceFileVisitor visitor, AceFileVisitorCallbackFactory callbackFactory) throws IOException{  	
             return new AceParserState(visitor,parser,false,0,0,callbackFactory);
        }
       

        
        public boolean seenAllExpectedReads(){
            return numberOfReadsSeen == expectedNumberOfReads;
        }
        public AceParserState seenRead(){
            numberOfReadsSeen++;
            if(currentReadVisitor !=null){
            	currentReadVisitor.visitEnd();
            }
            return this;
        }
        public boolean inConsensusQualities(){
        	return inConsensusQualities;
        }
       
        public boolean inAContig(){
        	return inAContig;
        }
        
        public AceParserState inConsensusQualities(boolean inConsensusQualities){
        	this.inConsensusQualities = inConsensusQualities;
        	if(!inConsensusQualities){
        		if(currentContigVisitor !=null){
        			currentContigVisitor.visitConsensusQualities(currentQualitySequenceBuilder.build());
        		}
            	currentQualitySequenceBuilder =null;
        	}
        	return this;
        }
        /**
         * Parse the next section in the ace file which may
         * be several thousands of lines long.
         * @throws IOException if there is a problem reading the ace file.
         */
        public void parseNextSection() throws IOException {
        	startPositionOfCurrentSection = parser.getPosition();
            String lineWithCR = parser.nextLine();           

            SectionHandler.handleSection(lineWithCR, this);
        }
        AceParserState(AceFileVisitor visitor, TextLineParser parser,
               boolean inAContig, int numberOfExpectedReads, int numberOfReadsSeen, AceFileVisitorCallbackFactory callbackFactory) {
            this.fileVisitor = visitor;
            this.parser = parser;
            this.inAContig = inAContig;
            this.numberOfReadsSeen = numberOfReadsSeen;
            this.expectedNumberOfReads = numberOfExpectedReads;
            this.callbackFactory = callbackFactory;
        }
        public boolean done(){
            return stopParsing.get() || !parser.hasNextLine();
        }
        
        public boolean parseCurrentContig(){
            return currentContigVisitor!=null;
        }
        
        /**
         * changed state to mark that
         * that a new contig is being visited. 
         * 
         */
        void handleNewContig(AceContigVisitor contigVisitor, int numberOfExpectedReads, int numberOfConsensusBases){
        	
        	this.inAContig = true;
        	this.numberOfReadsSeen=0;
        	this.expectedNumberOfReads=numberOfExpectedReads;
            currentQualitySequenceBuilder = new QualitySequenceBuilder(numberOfConsensusBases);
            currentContigVisitor = contigVisitor;
            currentReadVisitor = null;
            readReadPoritionOfContig=false;

        }
        
        void handleNewRead(String readId, int fulllLength){
        	readReadPoritionOfContig=true;
        	if(currentContigVisitor !=null){        		
        		this.currentReadVisitor = currentContigVisitor.visitBeginRead(readId, fulllLength);
        	}
        }
        
        void handleEndOfContig() {
        	if(expectedNumberOfReads !=numberOfReadsSeen){
                throw new IllegalStateException(
                        String.format("did not visit all expected reads : %d vs %d", numberOfReadsSeen, expectedNumberOfReads));
            }
        	if(currentContigVisitor !=null){        		
        		currentContigVisitor.visitEnd();
        	}        	
		}
        
        void handleEndOfParsing(){
        	if(stopParsing.get()){
        		//halted
        		if(currentContigVisitor !=null){
        			if(currentReadVisitor !=null){
        				currentReadVisitor.halted();
        			}
        			currentContigVisitor.halted();
        		}
        		fileVisitor.halted();
        		IOUtil.closeAndIgnoreErrors(this);
        	}else{
        		if(inAContig){
        			handleEndOfContig();
        		}
        		fileVisitor.visitEnd();
        	}
        }
        /**
         * Changes state to say that a contig
         * is no longer being parsed.
         * @return this.
         */
        AceParserState notInAContig(){
            inAContig = false;
            expectedNumberOfReads=0;
            numberOfReadsSeen=0;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();            
        }


		public void visitBasesLine(String mixedCaseBasecalls) {
			if(currentContigVisitor!=null){
				if(readReadPoritionOfContig){
					if(currentReadVisitor !=null){
						currentReadVisitor.visitBasesLine(mixedCaseBasecalls);
					}
				}else{
					currentContigVisitor.visitBasesLine(mixedCaseBasecalls);
					
				}
			}
			
		}


		public void visitAlignedReadInfo(String name, Direction dir,
				int fullRangeOffset) {
			if(currentContigVisitor !=null){
				currentContigVisitor.visitAlignedReadInfo(name, dir, fullRangeOffset);
			}
			
		}


		public void visitBeginContig(String contigId,
				int numberOfBases, int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplemented) {
			AceFileVisitorCallback callback =callbackFactory.newCallback(startPositionOfCurrentSection, stopParsing);
			AceContigVisitor contigVisitor =fileVisitor.visitContig(callback, contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplemented);
			handleNewContig(contigVisitor, numberOfReads,numberOfBases);
		}


		public boolean parseCurrentRead() {			
			return currentContigVisitor !=null && currentReadVisitor !=null;
		}


		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			currentReadVisitor.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
			
		}


		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			currentReadVisitor.visitTraceDescriptionLine(traceName, phdName, date);			
		}

    }
    /**
     * Each Section of an ACE file needs to be handled 
     * differently.  This might require firing visitor methods
     * or reading additional lines of text from the ace file.
     * @author dkatzel
     */
    private enum SectionHandler{
        
        ACE_HEADER("^AS\\s+(\\d+)\\s+(\\d+)"){
            @Override
            void handle(Matcher headerMatcher, AceParserState struct, String line) {
                int numberOfContigs = Integer.parseInt(headerMatcher.group(1));
                long totalNumberOfReads = Long.parseLong(headerMatcher.group(2));
                struct.fileVisitor.visitHeader(numberOfContigs, totalNumberOfReads);
            }
        },
        CONSENSUS_QUALITIES("^BQ\\s*"){
            @Override
            void handle(Matcher matcher, AceParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                	parserState.inConsensusQualities(true);
                }
            }
        },
        /**
         * Handles both basecalls from contig consensus as well
         * as basecalls from reads.
         */
        BASECALLS("^([\\-*a-zA-Z]+)\\s*$"){
            @Override
            void handle(Matcher basecallMatcher, AceParserState parserState, String line) {
                if(line.indexOf('-') !=-1){
                    //contains a gap as a '-' instead of a '*'
                    throw new IllegalStateException("invalid ace file: found '-' used as a gap instead of '*' : "+line);
                }
                if(parserState.parseCurrentContig()){
                    parserState.visitBasesLine(basecallMatcher.group(1));
                }
            } 
        },
        CONTIG_HEADER("^CO\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([UC])"){
            @Override
            void handle(Matcher contigMatcher, AceParserState struct, String line) {
                AceParserState ret = struct;
                ret.handleEndOfContig();
 
                if(ret.done()){
                	return;
                }
                String contigId = contigMatcher.group(1);
                int numberOfBases = Integer.parseInt(contigMatcher.group(2));
                int numberOfReads = Integer.parseInt(contigMatcher.group(3));
                int numberOfBaseSegments = Integer.parseInt(contigMatcher.group(4));
                boolean reverseComplemented = isComplimented(contigMatcher.group(5));
                
                ret.visitBeginContig(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplemented);
            } 
            
        },
        ASSEMBLED_FROM("^AF\\s+(\\S+)\\s+([U|C])\\s+(-?\\d+)"){
            @Override
            void handle(Matcher assembledFromMatcher, AceParserState parserState, String line) {
            	//parse current line
            	if(parserState.inConsensusQualities()){
            		parserState.inConsensusQualities(false);
            	}
                if(parserState.parseCurrentContig()){
                    String name = assembledFromMatcher.group(1);
                    final String group = assembledFromMatcher.group(2);
                    Direction dir = isComplimented(group)? Direction.REVERSE : Direction.FORWARD;
                    int fullRangeOffset = Integer.parseInt(assembledFromMatcher.group(3));
                    parserState.visitAlignedReadInfo(name, dir, fullRangeOffset);
                }
            } 
        },
        READ_HEADER("^RD\\s+(\\S+)\\s+(\\d+)"){
            @Override
            void handle(Matcher readMatcher, AceParserState parserState, String line) {
                if(parserState.parseCurrentContig()){
                    String readId = readMatcher.group(1);
                    int fullLength = Integer.parseInt(readMatcher.group(2));
                    parserState.handleNewRead(readId, fullLength);                   
                }
            } 
        },
        READ_QUALITY("^QA\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)"){
            @Override
            void handle(Matcher qualityMatcher, AceParserState parserState, String line) {
                if(parserState.parseCurrentRead()){
                    int qualLeft = Integer.parseInt(qualityMatcher.group(1));
                    int qualRight = Integer.parseInt(qualityMatcher.group(2));
                    
                    int alignLeft = Integer.parseInt(qualityMatcher.group(3));
                    int alignRight = Integer.parseInt(qualityMatcher.group(4));
                    parserState.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
                }
            } 
        },
        TRACE_DESCRIPTION("^DS\\s+"){
            
            private final Pattern chromatFilePattern = Pattern.compile("CHROMAT_FILE:\\s+(\\S+)\\s+");
            private final Pattern phdFilePattern = Pattern.compile("PHD_FILE:\\s+(\\S+)\\s+");
            private final Pattern timePattern = Pattern.compile("TIME:\\s+(.+:\\d\\d\\s+\\d\\d\\d\\d)");
            private final Pattern sffFakeChromatogramPattern = Pattern.compile("sff:(\\S+)?\\.sff:(\\S+)");
            
           //TODO : consed docs says chem is always required but consed seems to work anyway?
            private final Pattern chemPattern = Pattern.compile("CHEM:\\s+(\\S+)\\s+");
          //TODO : consed docs says these fields are required for sanger but consed seems to work anyway?
            
            private final Pattern dyePattern = Pattern.compile("DYE:\\s+(\\S+)\\s+");
            private final Pattern templatePattern = Pattern.compile("TEMPLATE:\\s+(\\S+)\\s+");
            private final Pattern dirPattern = Pattern.compile("DIR:\\s+(\\S+)\\s+");
            
            
            @Override
            void handle(Matcher qualityMatcher, AceParserState parserState, String line) throws IOException {
                if(parserState.parseCurrentRead()){
                    Matcher chromatogramMatcher = chromatFilePattern.matcher(line);
                    if(!chromatogramMatcher.find()){
                        throw new IOException("could not parse chromatogram name from "+line);
                    }
                    String traceName =  chromatogramMatcher.group(1);
                    String phdName = parsePhdName(line, traceName);
                    
                    Matcher timeMatcher = timePattern.matcher(line);
                    if(!timeMatcher.find()){
                        throw new IOException("could not parse phd time stamp from "+ line);
                    }
                    Date date;
					try {
						date = AceFileUtil.parsePhdDate(timeMatcher.group(1));
					} catch (Exception e) {
						throw new IllegalStateException("error parsing chromatogram time stamp '"+timeMatcher.group(1)+"'",e);
					}
                    parserState.visitTraceDescriptionLine(traceName, phdName, date);
                    
                }
                parserState.seenRead();
            }

            private String parsePhdName(String line, String traceName) {
                Matcher phdMatcher = phdFilePattern.matcher(line);
                String phdName;
                if(phdMatcher.find()){
                	phdName = phdMatcher.group(1);
                }else{
                    //sff's some times are in the format CHROMAT_FILE: sff:[-f:]<sff file>:<read id>
                    Matcher sffNameMatcher =sffFakeChromatogramPattern.matcher(traceName);
                    if(sffNameMatcher.find()){
                       
                    String sffRootName = sffNameMatcher.group(2);
                    final String group = sffNameMatcher.group(1);
                    boolean isForward = group.startsWith("-f:");
                    phdName = String.format("%s_%s", sffRootName,isForward?"left":"right");
                    }
                    else{
                        phdName = traceName;
                    }
                }
                return phdName;
            } 
        },
        READ_TAG("^RT\\{"){
            private final Pattern readTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            void handle(Matcher qualityMatcher, AceParserState parserState, String line) throws IOException {
                AceParserState currentParserState=parserState;
            	if(currentParserState.inAContig() && currentParserState.seenAllExpectedReads()){ 
            		currentParserState.handleEndOfContig();
                    currentParserState =currentParserState.notInAContig();
                    if(currentParserState.done()){
                    	return; 
                    }
                }
                
                String lineWithCR;
                lineWithCR = currentParserState.parser.nextLine();
                Matcher readTagMatcher = readTagPattern.matcher(lineWithCR);
                if(!readTagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = readTagMatcher.group(1);
                String type = readTagMatcher.group(2);
                String creator = readTagMatcher.group(3);
                long gappedStart = Long.parseLong(readTagMatcher.group(4))-1;
                long gappedEnd = Long.parseLong(readTagMatcher.group(5))-1;
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        readTagMatcher.group(6));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from read tag", e);
				}
                currentParserState.fileVisitor.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, true);
                lineWithCR = currentParserState.parser.nextLine();
                if(!lineWithCR.startsWith("}")){
                    throw new IllegalStateException("expected close read tag: " + lineWithCR); 
                }
            } 
        },
        WHOLE_ASSEMBLY_TAG("^WA\\{"){
            private final Pattern wholeAssemblyTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            void handle(Matcher qualityMatcher, AceParserState parserState, String line) throws IOException {
            	
                AceParserState currentParserState = parserState;
            	if(currentParserState.inAContig() && currentParserState.seenAllExpectedReads()){
            		currentParserState.handleEndOfContig();
                    currentParserState =currentParserState.notInAContig();
                    if(currentParserState.done()){
                    	return; 
                    }                   
                }
            	//delay calling visit current line
            	//until after we have determined we are 
            	//out of the current contig
                String lineWithCR= currentParserState.parser.nextLine();
                Matcher tagMatcher = wholeAssemblyTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected whole assembly tag information: " + lineWithCR); 
                }
                String type = tagMatcher.group(1);
                String creator = tagMatcher.group(2);
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        tagMatcher.group(3));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from while assembly tag", e);
				}
                                            
                StringBuilder data = parseWholeAssemblyTagData(currentParserState);
                currentParserState.fileVisitor.visitWholeAssemblyTag(type, creator, creationDate, data.toString());
            }

            private StringBuilder parseWholeAssemblyTagData(AceParserState parserState)
                    throws IOException {
                String lineWithCR;
                boolean doneTag =false;
                StringBuilder data = new StringBuilder();
                while(!doneTag && parserState.parser.hasNextLine()){
                    lineWithCR = parserState.parser.nextLine();
                    if(lineWithCR.startsWith("}")){
                    	doneTag =true;
                    }else{
                        data.append(lineWithCR);
                    }                    
                }
                if(!doneTag){
                    throw new IllegalStateException("unexpected EOF, Whole Assembly Tag not closed!"); 
                }
                return data;
            } 
        },
        CONSENSUS_TAG("^CT\\{"){
            private final Pattern consensusTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{6}:\\d{6})(\\s+(noTrans))?");
            
            @Override
            void handle(Matcher qualityMatcher, AceParserState parserState, String line) throws IOException {

                AceParserState currentParserState = parserState;
            	if(currentParserState.inAContig() && currentParserState.seenAllExpectedReads()){
            		currentParserState.handleEndOfContig();
                    currentParserState =currentParserState.notInAContig();
                    if(currentParserState.done()){
                    	return; 
                    }
                }
                String lineWithCR;
                lineWithCR = currentParserState.parser.nextLine();
                Matcher tagMatcher = consensusTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = tagMatcher.group(1);
                String type = tagMatcher.group(2);
                String creator = tagMatcher.group(3);
                long gappedStart = Long.parseLong(tagMatcher.group(4));
                long gappedEnd = Long.parseLong(tagMatcher.group(5));
                Date creationDate;
				try {
					creationDate = AceFileUtil.parseTagDate(                                                
					        tagMatcher.group(6));
				} catch (ParseException e) {
					throw new IllegalStateException("error parsing date from consensus tag", e);
				}
                boolean isTransient = tagMatcher.group(7)!=null;
                
                AceConsensusTagVisitor tagVisitor = currentParserState.fileVisitor.visitConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);

                
                parseConsensusTagData(currentParserState,tagVisitor);
                if(tagVisitor !=null){
                	tagVisitor.visitEnd();
                }
            }

            private void parseConsensusTagData(AceParserState parserState,AceConsensusTagVisitor tagVisitor) throws IOException {
                String lineWithCR;
                StringBuilder consensusComment=null;
                boolean doneTag = false;
                boolean inComment = false;
                
                while(!doneTag && parserState.parser.hasNextLine()){
                    lineWithCR = parserState.parser.nextLine();

                    if(lineWithCR.startsWith("COMMENT{")){
                        inComment=true;
                        consensusComment = new StringBuilder();
                    }else{
                        if(inComment){
                            if(lineWithCR.startsWith("C}")){          
                            	if(tagVisitor !=null){
                            		tagVisitor.visitComment(consensusComment.toString());
                            	}
                                inComment=false;
                            }else{
                                consensusComment.append(lineWithCR);
                            }
                        }else if(lineWithCR.startsWith("}")){
                        	 doneTag =true;
                        }
                        else if(tagVisitor !=null){                           
                            tagVisitor.visitData(lineWithCR);
                        }
                    }
                }
                if(!doneTag){
                    throw new IllegalStateException("unexpected EOF, Consensus Tag not closed!"); 
                }
            } 
        },
        IGNORE{
            @Override
            void handle(Matcher matcher, AceParserState parserState,
                    String line) throws IOException {
                if(parserState.inConsensusQualities()){
            		Scanner scanner = new Scanner(line);
            		while(scanner.hasNext()){
            			parserState.currentQualitySequenceBuilder.append(scanner.nextInt());
            		}            		
            	}
            }
        }
        ;
        private static final String COMPLIMENT_STRING = "C";
        /**
         * All handlers are considered except IGNORE.
         */
        private static final EnumSet<SectionHandler> HANDLERS_TO_CONSIDER = EnumSet.complementOf(EnumSet.of(IGNORE));
        private final Pattern pattern;
        private SectionHandler() {
            pattern = null;
        }
        private SectionHandler(String patternStr) {
            pattern = Pattern.compile(patternStr);
        }
        final Matcher matcher(String line) {            
            return pattern.matcher(line);
        }
        final boolean isComplimented(final String orientation) {
            return COMPLIMENT_STRING.equals(orientation);
        }
        abstract void handle(Matcher matcher, AceParserState struct, String line) throws IOException;
        
        
		/**
         * ResultHandler stores the SectionHandler that will 
         * be used to handle the current section and the Matcher
         * that matched the section (we store this so we 
         * don't have to match the string twice).
         * @author dkatzel
         */
        private static class ResultHandler{
            final SectionHandler handler;
            final Matcher matcher;
            
            ResultHandler(SectionHandler handler, Matcher matcher) {
                this.handler = handler;
                this.matcher = matcher;
            }
            void handle(String line, AceParserState struct) throws IOException{
                handler.handle(matcher, struct, line);
            }
        }
        private static ResultHandler findCorrectHandlerFor(String line){
            for(SectionHandler handler : HANDLERS_TO_CONSIDER){
                Matcher matcher = handler.matcher(line);
                if(matcher.find()){
                    return new ResultHandler(handler,matcher);
                }
            }
            //if we don't find a handler, then we ignore it
            return new ResultHandler(IGNORE,null);
        }
        public static void handleSection(String line, AceParserState struct) throws IOException{
            findCorrectHandlerFor(line).handle(line, struct);

        }
        
    }
    
    
    private static final class FileBasedParser extends AceFileParser {

    	private final File aceFile;
    	
		public FileBasedParser(File aceFile) {
			if(!aceFile.exists()){
				throw new IllegalArgumentException("ace file does not exist: " + aceFile.getAbsolutePath());
			}
			this.aceFile = aceFile;
		}
		@Override
		public void accept(AceFileVisitor visitor) throws IOException{
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
	        InputStream in = new FileInputStream(aceFile);
	        try{
	        	TextLineParser parser = new TextLineParser(new BufferedInputStream(in));
	        	//check first line in file to make sure it is valid formatted
	        	//ace file, can't do this for inputStream since 
	        	//we may get a partial stream
	        	String firstLine = parser.nextLine();
	        	 Matcher matcher = HEADER_PATTERN.matcher(firstLine);
	             if(!matcher.find()){            	 
	            	 throw new IOException("not valid ace file header : " + firstLine);
	             }
	             int numberOfContigs = Integer.parseInt(matcher.group(1));
	             int totalNumberOfReads = Integer.parseInt(matcher.group(2));
	             visitor.visitHeader(numberOfContigs, totalNumberOfReads);
	             AceFileVisitorCallbackFactory callbackFactory = new MementoCallbackFactory();
	             AceParserState parserState= AceParserState.create(parser,visitor, callbackFactory);
	             parseAceData(parserState, visitor);
	        }finally{
	            IOUtil.closeAndIgnoreErrors(in);
	        }
	    }
		
		@Override
		public void accept(AceFileVisitor visitor, AceFileVisitorMemento memento) throws IOException{
	        if(memento ==null){
	            throw new NullPointerException("memento can not be null");
	        }
	        if(visitor ==null){
	            throw new NullPointerException("visitor can not be null");
	        }
	        
	        if(!(memento instanceof AceFileMemento)){
	        	throw new IllegalArgumentException("unknown memento type "+ memento);
	        }
	        AceFileMemento aceFileMemento = (AceFileMemento)memento;
	        if(aceFileMemento.getParentParser() !=this){
	        	throw new IllegalArgumentException("memento must be used by the parser instance that created it");
	        }
	        long offset = aceFileMemento.getStartOffset();
	        
	        InputStream in =null;
	        try{
		        in = new RandomAccessFileInputStream(aceFile, offset);
		        
		        AceParserState parserState = AceParserState.create(in, visitor, new MementoCallbackFactory(),offset);
		        parseAceData(parserState, visitor);
	        }finally{
	        	IOUtil.closeAndIgnoreErrors(in);
	        }
	        
	    }
		private final class MementoCallbackFactory implements AceFileVisitorCallbackFactory{

			@Override
			public AceFileVisitorCallback newCallback(final long fileOffset, final AtomicBoolean stopParsing) {
				return new AceFileVisitorCallback() {
					
					@Override
					public void haltParsing() {
						stopParsing.set(true);					
					}
					
					@Override
					public AceFileVisitorMemento createMemento() {
						return new AceFileMemento(fileOffset);
					}
					
					@Override
					public boolean canCreateMemento() {
						return true;
					}
				};
			}    	
	    }
	    
	    private class AceFileMemento implements AceFileVisitorMemento{
	    	private final long startOffset;
	    	
			public AceFileMemento(long startOffset) {
				this.startOffset = startOffset;
			}

			public final long getStartOffset() {
				return startOffset;
			}

			AceHandler getParentParser(){
				return FileBasedParser.this;
			}
	    	
	    }
    }
    
    private static final class InputStreamParser extends AceFileParser {

    	private final OpenAwareInputStream in;
    	
		public InputStreamParser(InputStream in) {
			if(in ==null){
				throw new NullPointerException("input stream can not be null");
			}
			this.in = new OpenAwareInputStream(new BufferedInputStream(in));
		}
		@Override
		public void accept(AceFileVisitor visitor) throws IOException{
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			if(!in.isOpen()){
				throw new IllegalStateException("inputstream has been closed");
			}
	        
	        try{
	        	TextLineParser parser = new TextLineParser(in);
	             AceFileVisitorCallbackFactory callbackFactory = new NoMementoCallbackFactory();
	             AceParserState parserState= AceParserState.create(parser,visitor, callbackFactory);
	             parseAceData(parserState, visitor);
	        }finally{
	            IOUtil.closeAndIgnoreErrors(in);
	        }
	    }
		
		@Override
		public void accept(AceFileVisitor visitor, AceFileVisitorMemento memento) throws IOException{
	        throw new UnsupportedOperationException("mementos not supported");
	    }
		
    }
    
    private interface AceFileVisitorCallbackFactory{
    	AceFileVisitorCallback newCallback(long fileOffset, AtomicBoolean stopParsing);
    }
    
    private static final class NoMementoCallbackFactory implements AceFileVisitorCallbackFactory{

		@Override
		public AceFileVisitorCallback newCallback(long fileOffset, final AtomicBoolean stopParsing) {
			return new AceFileVisitorCallback() {
				
				@Override
				public void haltParsing() {
					stopParsing.set(true);					
				}
				
				@Override
				public AceFileVisitorMemento createMemento() {
					throw new UnsupportedOperationException("can not create mementos");
				}
				
				@Override
				public boolean canCreateMemento() {
					return false;
				}
			};
		}
    	
    }
    
    
}
