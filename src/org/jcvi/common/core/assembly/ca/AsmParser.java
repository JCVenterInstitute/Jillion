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

package org.jcvi.common.core.assembly.ca;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ca.AsmVisitor.LinkOrientation;
import org.jcvi.common.core.assembly.ca.AsmVisitor.MatePairEvidence;
import org.jcvi.common.core.assembly.ca.AsmVisitor.MateStatus;
import org.jcvi.common.core.assembly.ca.AsmVisitor.OverlapStatus;
import org.jcvi.common.core.assembly.ca.AsmVisitor.OverlapType;
import org.jcvi.common.core.assembly.ca.AsmVisitor.UnitigStatus;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public final class AsmParser {

    public static void parseAsm(File asmFile, AsmVisitor visitor) throws IOException{
        InputStream in =null;
        try{
            in= new FileInputStream(asmFile);
            parseAsm(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }
    
    public static void  parseAsm(InputStream asmFileStream, AsmVisitor visitor) throws IOException{
        ParserState parserState = new ParserState(new BufferedInputStream(asmFileStream));
        AsmMessageHandler.parse(parserState, visitor);
    }
    
    private static final class ParserState implements Closeable{
        private final TextLineParser parser;
        private boolean parseCurrentUnitig=true;
        private boolean parseCurrentContig=true;
        private boolean parseCurrentScaffold=true;
        private boolean reachedEOF =false;
        
        ParserState(InputStream inputStream) throws IOException{
            this.parser = new TextLineParser(inputStream);
        }
        
        boolean hasNextLine(){
            return parser.hasNextLine();
        }
        
        String getNextLine() throws IOException{
            String next= parser.nextLine();
            if(!parser.hasNextLine()){
                reachedEOF=true;
            }
            return next;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();
            
        }

        /**
         * @return the parseCurrentUnitig
         */
        public boolean shouldParseCurrentUnitig() {
            return parseCurrentUnitig;
        }

        /**
         * @param parseCurrentUnitig the parseCurrentUnitig to set
         */
        public void parseCurrentUnitig(boolean parseCurrentUnitig) {
            this.parseCurrentUnitig = parseCurrentUnitig;
        }

        /**
         * @return the parseCurrentContig
         */
        public boolean shouldParseCurrentContig() {
            return parseCurrentContig;
        }

        /**
         * @param parseCurrentContig the parseCurrentContig to set
         */
        public void parseCurrentContig(boolean parseCurrentContig) {
            this.parseCurrentContig = parseCurrentContig;
        }

        /**
         * @return the parseCurrentScaffold
         */
        public boolean shouldParseCurrentScaffold() {
            return parseCurrentScaffold;
        }

        /**
         * @param parseCurrentScaffold the parseCurrentScaffold to set
         */
        public void parseCurrentScaffold(boolean parseCurrentScaffold) {
            this.parseCurrentScaffold = parseCurrentScaffold;
        }

        /**
         * @return the reachedEOF
         */
        public boolean reachedEOF() {
            return reachedEOF;
        }
        
        
    }
    
    enum AsmMessageHandler{
        /**
         * Describes a group of mate pairs that belong to the same library.
         */
        MODIFIED_DISTANCE_MESSAGE("MDI") {
            private final Pattern REF_ID_PATTERN = Pattern.compile("ref:\\((\\S+),(\\d+)\\)");
           
            private final Pattern MIN_PATTERN = Pattern.compile("min:(\\d+)");
            private final Pattern MAX_PATTERN = Pattern.compile("max:(\\d+)");
            private final Pattern HISTOGRAM_BUCKET_PATTERN = Pattern.compile("buc:(\\d+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor) throws IOException {
                IdTuple idTuple = parseIds(parserState, visitor, REF_ID_PATTERN);
                float mean = parseMean(parserState, visitor);                
                float stdDev = parseStdDev(parserState, visitor);                
                int min=parseMin(parserState, visitor);
                int max=parseMax(parserState, visitor);
                
                List<Integer> histogram=parseHistogram(parserState,visitor);
                parseEndOfMessage(parserState, visitor);
                visitor.visitLibraryStatistics(idTuple.externalId, idTuple.internalId, 
                        mean, stdDev, min, max, histogram);
            }
           
            private float parseStdDev(ParserState parserState, AsmVisitor visitor) throws IOException {
                String stdDevLine = parserState.getNextLine();
                visitor.visitLine(stdDevLine);
                Matcher stdDevMatcher = STD_DEV_PATTERN.matcher(stdDevLine);
                if(!stdDevMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI std dev of distances: "+stdDevLine);
                }
                return  Float.parseFloat(stdDevMatcher.group(1));
            }
            private float parseMean(ParserState parserState, AsmVisitor visitor) throws IOException {
                String meanLine = parserState.getNextLine();
                visitor.visitLine(meanLine);
                Matcher meanMatcher = MEAN_PATTERN.matcher(meanLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI mean distance: "+meanLine);
                }
                return Float.parseFloat(meanMatcher.group(1));
            }
            
            private int parseMin(ParserState parserState, AsmVisitor visitor) throws IOException {
                String minLine = parserState.getNextLine();
                visitor.visitLine(minLine);
                Matcher meanMatcher = MIN_PATTERN.matcher(minLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI min distance: "+minLine);
                }
                return Integer.parseInt(meanMatcher.group(1));
            }
            private int parseMax(ParserState parserState, AsmVisitor visitor) throws IOException {
                String maxLine = parserState.getNextLine();
                visitor.visitLine(maxLine);
                Matcher meanMatcher = MAX_PATTERN.matcher(maxLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI max distance: "+maxLine);
                }
                return Integer.parseInt(meanMatcher.group(1));
            }
            
            private List<Integer> parseHistogram(ParserState parserState, AsmVisitor visitor) throws IOException {
                String histLine =parserState.getNextLine();
                visitor.visitLine(histLine);
                Matcher bucketMatcher = HISTOGRAM_BUCKET_PATTERN.matcher(histLine);
                if(!bucketMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI number of histogram buckets: "+histLine);
                }
                int numBuckets = Integer.parseInt(bucketMatcher.group(1));
                String histogramStart =parserState.getNextLine();
                if(!histogramStart.startsWith("his:")){
                    throw new IOException("invalid asm file: could not parse MDI start of histogram values: "+histogramStart);
                 }
                List<Integer> histogram = new ArrayList<Integer>(numBuckets);
                for(int i=0; i< numBuckets; i++){
                    String line = parserState.getNextLine();
                    visitor.visitLine(line);
                    histogram.add(Integer.parseInt(line.trim()));
                }
                return histogram;
            }
            
            
        },
        FRAGMENT_MESSAGE_HANDLER("AFG"){
             
            private final Pattern IS_SINGLETON_PATTERN = Pattern.compile("cha:(\\d+)");
            private final Pattern CLEAR_RANGE_PATTERN = Pattern.compile("clr:(\\d+,\\d+)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                IdTuple idTuple =parseIds(parserState, visitor, ACCESSION_PATTERN);
                MateStatus mateStatus = parseMateStatus(parserState, visitor);
                //is chimeric line is now ignored
                //but we need to visit the line anyway
                //in case the visitor is counting bytes or lines
                String isChimericLine =parserState.getNextLine();                
                visitor.visitLine(isChimericLine);
                boolean isSingleton = parseIsSingleton(parserState, visitor);
                Range clearRange = parseClearRange(parserState, visitor);
                parseEndOfMessage(parserState, visitor);
                visitor.visitRead(idTuple.externalId, idTuple.internalId,
                        mateStatus, isSingleton, clearRange);
            }

            private Range parseClearRange(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = CLEAR_RANGE_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("invalid asm file: could not parse AFG clear range: "+line);
                }
                return Range.parseRange(matcher.group(1));
            }

            
            /**
             * Is singleton if chaff value is set to 1; 0 otherwise.
             */
            private boolean parseIsSingleton(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = IS_SINGLETON_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("invalid asm file: could not parse AFG singlton status: "+line);
                }
                return Integer.parseInt(matcher.group(1).trim())==1;
            }
            
        },
        MATE_PAIR_HANDLER("AMP"){
            private final Pattern FRG_ID_PATTERN = Pattern.compile("frg:(\\S+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                String id1 = parseReadId(parserState,visitor);
                String id2 = parseReadId(parserState,visitor);
                MateStatus mateStatus = parseMateStatus(parserState, visitor);
                parseEndOfMessage(parserState, visitor);
                visitor.visitMatePair(id1, id2, mateStatus);
                
            }
            private String parseReadId(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = FRG_ID_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading frg id :"+ line);
                }
                return matcher.group(1);
            }
            
        },
        UNITIG_MESSAGE_FORMAT("UTG"){
            private final Pattern A_STAT_PATTERN = Pattern.compile("cov:(\\S+)");
            private final Pattern POLYMORPHISM_PATTERN = Pattern.compile("mhp:(\\S+)");
            private final Pattern STATUS_PATTERN = Pattern.compile("sta:(\\S)");
            private final Pattern LENGTH_PATTERN = Pattern.compile("len:(\\d+)");
            private final Pattern NUM_READS_PATTERN = Pattern.compile("nfr:(\\d+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                IdTuple idTuple =parseIds(parserState, visitor, ACCESSION_PATTERN);
                String nextLine = parserState.getNextLine();
                visitor.visitLine(nextLine);
                //CA <= 5 had a src block which should be ignored
                //CA 6+ doesn't have it anymore so need to handle
                //both cases.
                if(nextLine.startsWith("src")){
                    skipReservedSource(parserState, visitor);
                    nextLine = parserState.getNextLine();
                    visitor.visitLine(nextLine);
                }
                float aStat = parseAStat(nextLine);
                nextLine = parserState.getNextLine();
                visitor.visitLine(nextLine);
                //measure of polymorphism
                //was introduced in CA 6
                final float polymorphism;
                if(nextLine.startsWith("mhp")){
                    polymorphism = parsePolymorphismMeasure(nextLine);
                    nextLine = parserState.getNextLine();
                    visitor.visitLine(nextLine);
                }else{
                    polymorphism = Float.NaN;
                }
                UnitigStatus status = parseUnitigStatus(nextLine);
                nextLine = parserState.getNextLine();
                visitor.visitLine(nextLine);
                //skip legacy branch point lines
                //which don't exist in newer versions of CA
                if(nextLine.startsWith("abp")){
                    nextLine = parserState.getNextLine();
                    visitor.visitLine(nextLine);
                }
               
                int length = parseConsensusLength(nextLine);
                NucleotideSequence consensus = parseConsensus(parserState, visitor,length);
                QualitySequence consensusQualities = parseConsensusQualities(parserState,visitor,length);
                //skip forced line
                String forcedLine = parserState.getNextLine();
                visitor.visitLine(forcedLine);
                int numberOfReads = parseNumberOfReads(parserState, visitor);
                boolean shouldVisitUnitigReads =visitor.visitUnitig(idTuple.externalId, idTuple.internalId, aStat,
                        polymorphism, status, consensus, consensusQualities, numberOfReads);
                parserState.parseCurrentUnitig(shouldVisitUnitigReads);
                //read info is nested in each unitig
                for(int i=0; i<numberOfReads; i++){
                    String readHeader = parserState.getNextLine();
                    Matcher matcher = MESSAGE_HEADER_PATTERN.matcher(readHeader);
                    if(!matcher.find()){
                        throw new IOException(
                                String.format("error reading read # %d for unitig %d; invalid header :%s",
                                        i,idTuple.externalId,readHeader));
                    }
                    String code = matcher.group(1);
                    if(!READ_TO_UNITIG_MAPPING.canHandle(code)){
                        throw new IOException(
                                String.format("error reading read # %d for unitig %d; invalid header code :%s",
                                        i,idTuple.externalId,code));
                    
                    }
                    visitor.visitLine(readHeader);
                    READ_TO_UNITIG_MAPPING.handle(parserState, visitor);
                }
                parseEndOfMessage(parserState, visitor);
                visitor.visitEndOfUnitig();
            }
            private int parseNumberOfReads(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = NUM_READS_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error parsing unitig number of reads : "+ line);
                }
                return Integer.parseInt(matcher.group(1));
            }
            private QualitySequence parseConsensusQualities(
                    ParserState parserState, AsmVisitor visitor, int length) throws IOException {
                byte[] qualities = new byte[length];
                //first line should be qlt
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                if(!line.startsWith("qlt:")){
                   throw new IOException("expected start quality consensus block :"+line); 
                }
                line = parserState.getNextLine();
                visitor.visitLine(line);
                int offset=0;
                while(!line.startsWith(".")){
                    String trimmedLine = line.trim();
                    for(int i=0; i<trimmedLine.length(); i++){
                        //qualities are encoded as value + ascii zero
                        qualities[offset+i]=(byte)(trimmedLine.charAt(i)- '0');
                    }
                    offset +=trimmedLine.length();
                    line = parserState.getNextLine();
                    visitor.visitLine(line);
                }
                if(offset !=length){
                    throw new IOException( String.format("incorrect consensus quality length for %s: expected %d but was %d",
                            getMessageCode(),
                            length,offset));
                }
                return new EncodedQualitySequence(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                        PhredQuality.valueOf(qualities));
            }
            private NucleotideSequence parseConsensus(ParserState parserState,
                    AsmVisitor visitor, int expectedLength) throws IOException {
                NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(expectedLength);
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                if(!line.startsWith("cns:")){
                    throw new IOException("expected begin cns field but was "+line );
                }
                line = parserState.getNextLine();
                visitor.visitLine(line);
                while(!line.startsWith(".")){
                    builder.append(line.trim());
                    line = parserState.getNextLine();
                    visitor.visitLine(line);
                }
                if(builder.getLength()!=expectedLength){
                    throw new IOException(
                            String.format("incorrect consensus length for %s: expected %d but was %d",
                                    getMessageCode(),
                                    expectedLength,builder.getLength()));
                }
                return builder.build();
            }
           

            private UnitigStatus parseUnitigStatus(String line) throws IOException {
                Matcher matcher = STATUS_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error parsing unitig status : "+ line);
                }
                return UnitigStatus.parseUnitigStatus(matcher.group(1));
            }

            private float parseAStat(String line) throws IOException {
                Matcher matcher = A_STAT_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading unitig coverage a stat:"+ line);
                }
                return Float.parseFloat(matcher.group(1));
            }
            
            private float parsePolymorphismMeasure(String line) throws IOException {
                Matcher matcher = POLYMORPHISM_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading unitig polymorphism measure:"+ line);
                }
                return Float.parseFloat(matcher.group(1));
            }
            
            private int parseConsensusLength(String line) throws IOException {               
                Matcher matcher = LENGTH_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading unitig consensus length:"+ line);
                }
                return Integer.parseInt(matcher.group(1));
            }

            
            
        },
        READ_TO_UNITIG_MAPPING("MPS"){
            private final Pattern TYPE_PATTERN = Pattern.compile("typ:(\\S)");
            private final Pattern READ_ID_PATTERN = Pattern.compile("mid:(\\S+)");
            private final Pattern RANGE_PATTERN = Pattern.compile("pos:(\\d+,\\d+)");
            private final Pattern NUM_OFFSETS_PATTERN = Pattern.compile("dln:(\\d+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
               char type = parseReadType(parserState, visitor);
               String readId = parseReadId(parserState,visitor);
               String nextLine = parserState.getNextLine();
               visitor.visitLine(nextLine);
               //CA <= 5 had a src block which should be ignored
               //CA 6+ doesn't have it anymore so need to handle
               //both cases.
               if(nextLine.startsWith("src")){
                   skipReservedSource(parserState, visitor);
                   nextLine = parserState.getNextLine();
                   visitor.visitLine(nextLine);
               }
               
               DirectedRange directedRange = parseDirectedRange(nextLine);
               List<Integer> gapOffsets = parseGapOffsets(parserState,visitor);
               parseEndOfMessage(parserState, visitor);
               if(parserState.shouldParseCurrentUnitig()){
                   visitor.visitReadLayout(type, readId, directedRange, gapOffsets);
               }
            }
            private DirectedRange parseDirectedRange(String line) throws IOException {
                Matcher matcher = RANGE_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading read-to-unitig placed range:"+ line);
                }
                return DirectedRange.parse(matcher.group(1));
            }
            private List<Integer> parseGapOffsets(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String lengthLine = parserState.getNextLine();
                visitor.visitLine(lengthLine);
                Matcher matcher = NUM_OFFSETS_PATTERN.matcher(lengthLine);
                if(!matcher.find()){
                    throw new IOException("error reading read-to-unitig delta encoding length:"+ lengthLine);
                }
                int expectedNumberOfOffsets = Integer.parseInt(matcher.group(1));
                String beginDeltaEncodingLine = parserState.getNextLine();
                if(!beginDeltaEncodingLine.startsWith("del:")){
                    throw new IOException("error reading read-to-unitig delta encoding:"+ beginDeltaEncodingLine);
                }
                List<Integer> offsets = new ArrayList<Integer>(expectedNumberOfOffsets);
                while(offsets.size()<expectedNumberOfOffsets){
                    String offsetLine = parserState.getNextLine();
                    visitor.visitLine(offsetLine);
                    Scanner scanner = new Scanner(offsetLine);
                    if(!scanner.hasNextInt()){
                        throw new IOException("error reading read-to-unitig delta encoding not enough values :"+ offsetLine);
                        
                    }
                    while(scanner.hasNextInt()){
                        offsets.add(scanner.nextInt());
                    }
                }
                return offsets;
            }
            private String parseReadId(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = READ_ID_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading read-to-unitig read id:"+ line);
                }
                return matcher.group(1);
            }
            private char parseReadType(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = TYPE_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading read-to-unitig mapping type:"+ line);
                }
                return matcher.group(1).charAt(0);
            }
            
        },
        UNITIG_LINK("ULK"){
            private final Pattern UNITIG_ID_PATTERN = Pattern.compile("ut\\d:(\\S+)");
            private final Pattern LINK_ORIENTATION_PATTERN = Pattern.compile("ori:(\\S)");
            private final Pattern OVERLAP_TYPE_PATTERN = Pattern.compile("ovt:(\\S)");
            
            private final Pattern CHIMERA_FLAG_PATTERN = Pattern.compile("ipc:(\\d)");
            private final Pattern NUM_EDGES_PATTERN = Pattern.compile("num:(\\d+)");
            
            private final Pattern LINK_STATUS_PATTERN = Pattern.compile("sta:(\\S)");
            private final Pattern JUMP_LIST_PATTERN = Pattern.compile("(\\S+),(\\S+),(\\S)");
            
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                String unitig1 = getUnitigId(parserState, visitor);
                String unitig2 = getUnitigId(parserState, visitor);
                LinkOrientation orientation = getLinkOrientation(parserState, visitor);
                OverlapType overlapType = getOverlapType(parserState, visitor);
                boolean isPossibleChimera = getChimeraFlag(parserState, visitor);
                //includes guide was removed in CA 6
                String nextLine = parserState.getNextLine();
                visitor.visitLine(nextLine);
                if(nextLine.startsWith("gui:")){
                    nextLine = parserState.getNextLine();
                    visitor.visitLine(nextLine);
                }
                float mean = parseMeanEdgeDistance(nextLine);
                float stdDev = parseStdDevDistance(parserState, visitor);
                int numberOfContributingEdges = parseNumberOfEdges(parserState, visitor);
                OverlapStatus status = parseOverlapStatus(parserState, visitor);
                String jumpList = parserState.getNextLine();
                visitor.visitLine(jumpList);
                if(!jumpList.startsWith("jls:")){
                    throw new IOException("invalid jump list block : "+ jumpList);
                }
                Set<MatePairEvidence> evidenceList = parseMatePairEvidence(overlapType.getExpectedNumberOfMatePairEvidenceRecords(numberOfContributingEdges), parserState,visitor);
                parseEndOfMessage(parserState, visitor);
                if(parserState.shouldParseCurrentUnitig()){
                    visitor.visitUnitigLink(unitig1, unitig2, orientation, overlapType, status, 
                            numberOfContributingEdges, mean, stdDev, evidenceList);
                }
            }
            
            private Set<MatePairEvidence> parseMatePairEvidence(
                    int expectedNumberOfMatePairEvidenceRecords,
                    ParserState parserState, AsmVisitor visitor) throws IOException {
                Set<MatePairEvidence> set = new LinkedHashSet<AsmVisitor.MatePairEvidence>();
                for(int i=0; i<expectedNumberOfMatePairEvidenceRecords; i++){
                    String line = parserState.getNextLine();
                    visitor.visitLine(line);
                    Matcher matcher = JUMP_LIST_PATTERN.matcher(line);
                    if(!matcher.find()){
                        throw new IOException("invalid jump list record: "+ line);
                    }
                    set.add(new MatePairEvidenceImpl(matcher.group(1), matcher.group(2)));
                }
                return set;
            }

            private OverlapStatus parseOverlapStatus(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line); 
                Matcher matcher = LINK_STATUS_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error overlap status"+ line);
                }
                return OverlapStatus.parseOverlapStatus(matcher.group(1));
            }

            private int parseNumberOfEdges(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line); 
                Matcher matcher = NUM_EDGES_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading # of edges"+ line);
                }
                return Integer.parseInt(matcher.group(1));
            }

            private float parseMeanEdgeDistance(String nextLine) throws IOException {
                Matcher matcher = MEAN_PATTERN.matcher(nextLine);
                if(!matcher.find()){
                    throw new IOException("error reading is mean edge distance message"+ nextLine);
                }
                return Float.parseFloat(matcher.group(1));
            }
            
            private float parseStdDevDistance(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line); 
                Matcher matcher = STD_DEV_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading is std dev edge distance message"+ line);
                }
                return Float.parseFloat(matcher.group(1));
            }

            private boolean getChimeraFlag(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = CHIMERA_FLAG_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading is possible chimera message"+ line);
                }
                int value = Integer.parseInt(matcher.group(1));
                return value ==1;
            }

            private LinkOrientation getLinkOrientation(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = LINK_ORIENTATION_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading link orientation message"+ line);
                }
                return LinkOrientation.parseLinkOrientation(matcher.group(1));
            }
            
            private OverlapType getOverlapType(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = OVERLAP_TYPE_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading overlap type message"+ line);
                }
                return OverlapType.parseOverlapType(matcher.group(1));
            }

            private String getUnitigId(ParserState parserState, AsmVisitor visitor) throws IOException{
                String line = parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = UNITIG_ID_PATTERN.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading unitig link message unitig id:"+ line);
                }
                return matcher.group(1);
            }
            
        }
        ;
        
        
        private final String messageCode;
        static Pattern MESSAGE_HEADER_PATTERN = Pattern.compile("\\{(\\S+)");
        static final Pattern MATE_STATUS_PATTERN = Pattern.compile("mst:(\\S)");
        static final Pattern ACCESSION_PATTERN = Pattern.compile("acc:\\((\\S+),(\\d+)\\)");
        static final Pattern MEAN_PATTERN = Pattern.compile("mea:(\\S+)");
        static final Pattern STD_DEV_PATTERN = Pattern.compile("std:(\\S+)");
        
        
        private static final String END_MESSAGE = "}";
        private AsmMessageHandler(String messageCode){
            this.messageCode = messageCode;
        }
        
        final boolean canHandle(String messageCode){
            return this.messageCode.equals(messageCode);
        }
        
        /**
         * @return the messageCode
         */
        public String getMessageCode() {
            return messageCode;
        }

        protected abstract void handle(ParserState parserState, AsmVisitor visitor) throws IOException;
        public static void parse(ParserState parserState, AsmVisitor visitor) throws IOException{
            while(parserState.hasNextLine()){
                String line =parserState.getNextLine();
                visitor.visitLine(line);
                Matcher matcher = MESSAGE_HEADER_PATTERN.matcher(line);
                if(matcher.find()){
                    String header = matcher.group(1);
                    for(AsmMessageHandler handler : values()){
                        if(handler.canHandle(header)){
                            handler.handle(parserState, visitor);
                            break;
                        }
                    }
                }
            }
        }
        
        IdTuple parseIds(ParserState parserState, AsmVisitor visitor, Pattern pattern) throws IOException {
            String idLine =parserState.getNextLine();
            visitor.visitLine(idLine);
            Matcher idMatcher = pattern.matcher(idLine);
            if(!idMatcher.find()){
                throw new IOException("invalid asm file: could not parse IDs: "+idLine);
            }
            return new IdTuple(idMatcher.group(1), Long.parseLong(idMatcher.group(2)));
        }
        void parseEndOfMessage(ParserState parserState,
                AsmVisitor visitor) throws IOException {
            String endLine = parserState.getNextLine();
            visitor.visitLine(endLine);
            if(!endLine.startsWith(END_MESSAGE)){
                throw new IOException("invalid asm file: invalid "+ messageCode +" end tag : " + endLine);
            }
        }
        
        MateStatus parseMateStatus(ParserState parserState,
                AsmVisitor visitor) throws IOException {
            String line = parserState.getNextLine();
            visitor.visitLine(line);
            Matcher matcher = MATE_STATUS_PATTERN.matcher(line);
            if(!matcher.find()){
                throw new IOException("invalid asm file: could not parse "+messageCode+" mate status: "+line);
            }
            return MateStatus.parseMateStatus(matcher.group(1));
        }
        
        void skipReservedSource(ParserState parserState,
                AsmVisitor visitor) throws IOException {
            
            String line = "";
            while(!line.equals(".\n")){
                line = parserState.getNextLine();
                visitor.visitLine(line);
            }
            
        }
    }
    
    private static final class IdTuple{
        private final String externalId;
        private final long internalId;
        private IdTuple(String externalId, long internalId) {
            this.externalId = externalId;
            this.internalId = internalId;
        }
        
    }
    
    private static final class MatePairEvidenceImpl implements MatePairEvidence{
        private final String read1,read2;
        
        private MatePairEvidenceImpl(String read1, String read2) {
            this.read1 = read1;
            this.read2 = read2;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getRead1() {
            return read1;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getRead2() {
            return read2;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((read1 == null) ? 0 : read1.hashCode());
            result = prime * result + ((read2 == null) ? 0 : read2.hashCode());
            return result;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MatePairEvidenceImpl)) {
                return false;
            }
            MatePairEvidenceImpl other = (MatePairEvidenceImpl) obj;
            if (read1 == null) {
                if (other.read1 != null) {
                    return false;
                }
            } else if (!read1.equals(other.read1)) {
                return false;
            }
            if (read2 == null) {
                if (other.read2 != null) {
                    return false;
                }
            } else if (!read2.equals(other.read2)) {
                return false;
            }
            return true;
        }
        
        
    }
}
