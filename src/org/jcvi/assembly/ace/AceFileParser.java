/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.io.TextLineParser;
import org.jcvi.sequence.SequenceDirection;
/**
 * {@code AceFileParser} contains methods for parsing
 * ACE formatted files.
 * @author dkatzel
 *
 *
 */
public final class AceFileParser {
    public static void parseAceFile(File file, AceFileVisitor visitor) throws IOException{
        parseAceFile(new FileInputStream(file), visitor);
    }
    
    public static void parseAceFile(InputStream inputStream, AceFileVisitor visitor) throws IOException{
        if(inputStream ==null){
            throw new NullPointerException("input stream can not be null");
        }
        TextLineParser parser= new TextLineParser(new BufferedInputStream(inputStream));
        ParserStruct parserStruct = new ParserStruct(visitor, parser);
        while(parser.hasNextLine()){
            String lineWithCR = parser.nextLine();           
            visitor.visitLine(lineWithCR);
            String line = lineWithCR.endsWith("\n")
                        ? lineWithCR.substring(0, lineWithCR.length()-1)
                        : lineWithCR; 
            parserStruct = SectionHandler.handleSection(line, parserStruct);
                       
        }
        visitor.visitEndOfContig();
        visitor.visitEndOfFile();
    }

    private static class ParserStruct{
        final boolean isFirstContigInFile;
        final AceFileVisitor visitor;
        final TextLineParser parser;
        ParserStruct(AceFileVisitor visitor,
                 TextLineParser parser){
            this(visitor, true,  parser);
        }
        ParserStruct(AceFileVisitor visitor, boolean isFirstContigInFile,
               TextLineParser parser) {
            this.visitor = visitor;
            this.isFirstContigInFile = isFirstContigInFile;
            this.parser = parser;
        }
        ParserStruct updateContigBeingVisited(){
            return new ParserStruct(visitor, false, parser);
        }
      
        
    }
    
    private enum SectionHandler{
        ACE_HEADER("^AS\\s+(\\d+)\\s+(\\d+)"){
            @Override
            ParserStruct handle(Matcher headerMatcher, ParserStruct struct, String line) {
                ParserStruct ret = struct;
                if(!struct.isFirstContigInFile){
                    ret = ret.updateContigBeingVisited();
                    ret.visitor.visitEndOfContig();
                }
                int numberOfContigs = Integer.parseInt(headerMatcher.group(1));
                int totalNumberOfReads = Integer.parseInt(headerMatcher.group(2));
                ret.visitor.visitHeader(numberOfContigs, totalNumberOfReads);
                return ret;
            }
        },
        CONSENSUS_QUALITIES("^BQ\\s*"){
            @Override
            ParserStruct handle(Matcher matcher, ParserStruct struct, String line) {
                struct.visitor.visitConsensusQualities();
                return struct;
            }
        },
        BASECALLS("^([*a-zA-Z]+)\\s*$"){
            @Override
            ParserStruct handle(Matcher basecallMatcher, ParserStruct struct, String line) {
                struct.visitor.visitBasesLine(basecallMatcher.group(1));
                return struct;
            } 
        },
        CONTIG_HEADER("^CO\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([UC])"){
            @Override
            ParserStruct handle(Matcher contigMatcher, ParserStruct struct, String line) {
                String contigId = contigMatcher.group(1);
                int numberOfBases = Integer.parseInt(contigMatcher.group(2));
                int numberOfReads = Integer.parseInt(contigMatcher.group(3));
                int numberOfBaseSegments = Integer.parseInt(contigMatcher.group(4));
                boolean reverseComplimented = parseIsComplimented(contigMatcher.group(5));
                struct.visitor.visitContigHeader(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplimented);
                return struct;
            } 
            
        },
        ASSEMBLED_FROM("^AF\\s+(\\S+)\\s+([U|C])\\s+(-?\\d+)"){
            @Override
            ParserStruct handle(Matcher assembledFromMatcher, ParserStruct struct, String line) {
                String name = assembledFromMatcher.group(1);
                final String group = assembledFromMatcher.group(2);
                SequenceDirection dir = parseIsComplimented(group)? SequenceDirection.REVERSE : SequenceDirection.FORWARD;
                int fullRangeOffset = Integer.parseInt(assembledFromMatcher.group(3));
                struct.visitor.visitAssembledFromLine(name, dir, fullRangeOffset);
                return struct;
            } 
        },
        READ_HEADER("^RD\\s+(\\S+)\\s+(\\d+)"){
            @Override
            ParserStruct handle(Matcher readMatcher, ParserStruct struct, String line) {
                String readId = readMatcher.group(1);
                int fullLength = Integer.parseInt(readMatcher.group(2));
                struct.visitor.visitReadHeader(readId, fullLength);
                return struct;
            } 
        },
        READ_QUALITY("^QA\\s+(-?\\d+)\\s+(-?\\d+)\\s+(\\d+)\\s+(\\d+)"){
            @Override
            ParserStruct handle(Matcher qualityMatcher, ParserStruct struct, String line) {
                int clearLeft = Integer.parseInt(qualityMatcher.group(1));
                int clearRight = Integer.parseInt(qualityMatcher.group(2));
                
                int alignLeft = Integer.parseInt(qualityMatcher.group(3));
                int alignRight = Integer.parseInt(qualityMatcher.group(4));
                struct.visitor.visitQualityLine(clearLeft, clearRight, alignLeft, alignRight);
                return struct;
            } 
        },
        TRACE_DESCRIPTION("^DS\\s+"){
            
            private final Pattern chromatFilePattern = Pattern.compile("CHROMAT_FILE:\\s+(\\S+)\\s+");
            private final Pattern phdFilePattern = Pattern.compile("PHD_FILE:\\s+(\\S+)\\s+");
            private final Pattern timePattern = Pattern.compile("TIME:\\s+(.+:\\d\\d\\s+\\d\\d\\d\\d)");
            private final Pattern sffFakeChromatogramPattern = Pattern.compile("sff:(\\S+)?\\.sff:(\\S+)");
             
            @Override
            ParserStruct handle(Matcher qualityMatcher, ParserStruct struct, String line) throws IOException {
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
                Date date= AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.parseDateTime(                                                
                        timeMatcher.group(1)).toDate();
                struct.visitor.visitTraceDescriptionLine(traceName, phdName, date);
                return struct;
            }

            private String parsePhdName(String line, String traceName) {
                Matcher phdMatcher = phdFilePattern.matcher(line);
                String phdName;
                if(!phdMatcher.find()){
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
                }else{
                    phdName = phdMatcher.group(1);
                }
                return phdName;
            } 
        },
        READ_TAG("^RT\\{"){
            private final Pattern readTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            ParserStruct handle(Matcher qualityMatcher, ParserStruct struct, String line) throws IOException {
                String lineWithCR;
                lineWithCR = struct.parser.nextLine();
                struct.visitor.visitLine(lineWithCR);
                Matcher readTagMatcher = readTagPattern.matcher(lineWithCR);
                if(!readTagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = readTagMatcher.group(1);
                String type = readTagMatcher.group(2);
                String creator = readTagMatcher.group(3);
                long gappedStart = Long.parseLong(readTagMatcher.group(4));
                long gappedEnd = Long.parseLong(readTagMatcher.group(5));
                Date creationDate= AceFileUtil.TAG_DATE_TIME_FORMATTER.parseDateTime(                                                
                        readTagMatcher.group(6)).toDate();
                struct.visitor.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, true);
                lineWithCR = struct.parser.nextLine();
                struct.visitor.visitLine(lineWithCR);
                if(!lineWithCR.startsWith("}")){
                    throw new IllegalStateException("expected close read tag: " + lineWithCR); 
                }
                return struct;
            } 
        },
        WHOLE_ASSEMBLY_TAG("^WA\\{"){
            private final Pattern wholeAssemblyTagPattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\d{6}:\\d{6})");
            
            @Override
            ParserStruct handle(Matcher qualityMatcher, ParserStruct struct, String line) throws IOException {
                String lineWithCR;
                lineWithCR = struct.parser.nextLine();
                struct.visitor.visitLine(lineWithCR);
                Matcher tagMatcher = wholeAssemblyTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected whole assembly tag information: " + lineWithCR); 
                }
                String type = tagMatcher.group(1);
                String creator = tagMatcher.group(2);
                Date creationDate= AceFileUtil.TAG_DATE_TIME_FORMATTER.parseDateTime(                                                
                        tagMatcher.group(3)).toDate();
                                            
                StringBuilder data = parseWholeAssemblyTagData(struct);
                struct.visitor.visitWholeAssemblyTag(type, creator, creationDate, data.toString());
                return struct;
            }

            private StringBuilder parseWholeAssemblyTagData(ParserStruct struct)
                    throws IOException {
                String lineWithCR;
                boolean doneTag =false;
                StringBuilder data = new StringBuilder();
                while(!doneTag && struct.parser.hasNextLine()){
                    lineWithCR = struct.parser.nextLine();
                    struct.visitor.visitLine(lineWithCR);
                    if(!lineWithCR.startsWith("}")){
                        data.append(lineWithCR);
                    }
                    else{
                        doneTag =true;
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
            ParserStruct handle(Matcher qualityMatcher, ParserStruct struct, String line) throws IOException {
                String lineWithCR;
                lineWithCR = struct.parser.nextLine();
                struct.visitor.visitLine(lineWithCR);
                Matcher tagMatcher = consensusTagPattern.matcher(lineWithCR);
                if(!tagMatcher.find()){
                    throw new IllegalStateException("expected read tag infomration: " + lineWithCR); 
                }
                String id = tagMatcher.group(1);
                String type = tagMatcher.group(2);
                String creator = tagMatcher.group(3);
                long gappedStart = Long.parseLong(tagMatcher.group(4));
                long gappedEnd = Long.parseLong(tagMatcher.group(5));
                Date creationDate= AceFileUtil.TAG_DATE_TIME_FORMATTER.parseDateTime(                                                
                        tagMatcher.group(6)).toDate();
                boolean isTransient = tagMatcher.group(7)!=null;
                
                struct.visitor.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
                
                
                boolean doneTag =false;
                boolean inComment=false;
                
                parseConsensusTagData(struct, doneTag, inComment);
                struct.visitor.visitEndConsensusTag();
                return struct;
            }

            private void parseConsensusTagData(ParserStruct struct,
                    boolean doneTag, boolean inComment) throws IOException {
                String lineWithCR;
                StringBuilder consensusComment=null;
                while(!doneTag && struct.parser.hasNextLine()){
                    lineWithCR = struct.parser.nextLine();
                    struct.visitor.visitLine(lineWithCR);
                    if(lineWithCR.startsWith("COMMENT{")){
                        inComment=true;
                        consensusComment = new StringBuilder();
                    }else{
                        if(inComment){
                            if(lineWithCR.startsWith("C}")){                                                            
                                struct.visitor.visitConsensusTagComment(consensusComment.toString());
                                inComment=false;
                            }else{
                                consensusComment.append(lineWithCR);
                            }
                        }else if(!lineWithCR.startsWith("}")){
                            struct.visitor.visitConsensusTagData(lineWithCR);
                        }
                        else{
                            doneTag =true;
                        }
                    }
                }
                if(!doneTag){
                    throw new IllegalStateException("unexpected EOF, Consensus Tag not closed!"); 
                }
            } 
        }
        ;
        private static final String COMPLIMENT_STRING = "C";
        private final Pattern pattern;
        private SectionHandler(String patternStr) {
            pattern = Pattern.compile(patternStr);
        }
        final Matcher matcher(String line) {
            return pattern.matcher(line);
        }
        final boolean parseIsComplimented(final String group) {
            return COMPLIMENT_STRING.equals(group);
        }
        abstract ParserStruct handle(Matcher matcher, ParserStruct struct, String line) throws IOException;
    
        private static class Results{
            final SectionHandler handler;
            final Matcher matcher;
            
            Results(SectionHandler handler, Matcher matcher) {
                this.handler = handler;
                this.matcher = matcher;
            }
        }
        private static Results findCorrectHandlerFor(String line){
            for(SectionHandler handler : values()){
                Matcher matcher = handler.matcher(line);
                if(matcher.find()){
                    return new Results(handler,matcher);
                }
            }
            return null;
        }
        public static ParserStruct handleSection(String line,ParserStruct struct) throws IOException{
            Results result = findCorrectHandlerFor(line);
            if(result !=null){
                return result.handler.handle(result.matcher, struct, line);
            }
            return struct;
        }
        
    }
    
}
