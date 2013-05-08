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
package org.jcvi.jillion.assembly.ca.asm;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.ca.asm.AsmContigVisitor.VariantRecord;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.LinkOrientation;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.MatePairEvidence;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.MateStatus;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.OverlapStatus;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.OverlapType;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.UnitigLayoutType;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.UnitigStatus;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback.AsmVisitorMemento;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.internal.core.util.JillionUtil;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AsmFileParser {
	
	
	/**
	 * Refactored out split Pattern since String.split() 
	 * causes a new Pattern to be created and compiled
	 * for each call.  This is a minor cpu optimization.
	 */
	private static final Pattern SPLIT_ON_SLASH = Pattern.compile("/");
	
	 private static final String END_MESSAGE = "}";
	 
	 private static final Pattern LENGTH_PATTERN = Pattern.compile("len:(\\d+)");
	 private  static final Pattern NUM_READS_PATTERN = Pattern.compile("n\\S\\S:(\\d+)");
     
	 
	private AsmFileParser(){
		//can not instantiate outside this file.
	}
	
	public static AsmFileParser create(File asmFile){
		return new FileBasedAsmFileParser(asmFile);
	}
	
	public abstract void accept(AsmVisitor visitor) throws IOException;
	
	public abstract void accept(AsmVisitor visitor, AsmVisitorMemento memento) throws IOException;
	
	
    protected void  parseAsm(ParserState parserState, AsmVisitor visitor) throws IOException{       
        AsmMessageHandler.parse(parserState, visitor);
    }
    
    protected abstract class ParserState implements Closeable{
        private final TextLineParser parser;
        
        private long currentOffset;
        protected long markedOffset;
        protected final AtomicBoolean keepParsing = new AtomicBoolean(true);
        
        ParserState(InputStream inputStream, long initialOffset) throws IOException{
            this.parser = new TextLineParser(inputStream);
            currentOffset = initialOffset;
            markedOffset= currentOffset;
        }
        
        boolean hasNextLine(){
            return parser.hasNextLine();
        }
        
        String getNextLine() throws IOException{
            String line= parser.nextLine(); 
            currentOffset += line.length();
            return line;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();            
        }
        public abstract CallBack createCallback();

		public void markCurrentOffset() {
			markedOffset =currentOffset;
			
		}

		public boolean keepParsing() {
			return keepParsing.get();
		}

        
    }
    
    enum AsmMessageHandler{
        /**
         * Describes a group of mate pairs that belong to the same library.
         */
        MODIFIED_DISTANCE_MESSAGE("MDI") {
            private final Pattern refIdPattern = Pattern.compile("ref:\\((\\S+),(\\d+)\\)");
            /**
             * min can be set to a negative value
             */
            private final Pattern minPattern = Pattern.compile("min:(-?\\d+)");
            private final Pattern maxPattern = Pattern.compile("max:(\\d+)");
            private final Pattern histogramBucketPattern = Pattern.compile("buc:(\\d+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor) throws IOException {
                IdTuple idTuple = parseIds(parserState, visitor, refIdPattern);
                float mean = parseMean(parserState, visitor);                
                float stdDev = parseStdDev(parserState, visitor);                
                long min=parseMin(parserState, visitor);
                long max=parseMax(parserState, visitor);
                
                List<Long> histogram=parseHistogram(parserState,visitor);
                parseEndOfMessage(parserState, this.getMessageCode());
                visitor.visitLibraryStatistics(idTuple.externalId, idTuple.internalId, 
                        mean, stdDev, min, max, histogram);
            }
           
            private float parseStdDev(ParserState parserState, AsmVisitor visitor) throws IOException {
                String stdDevLine = parserState.getNextLine();
                Matcher stdDevMatcher = STD_DEV_PATTERN.matcher(stdDevLine);
                if(!stdDevMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI std dev of distances: "+stdDevLine);
                }
                return  Float.parseFloat(stdDevMatcher.group(1));
            }
            private float parseMean(ParserState parserState, AsmVisitor visitor) throws IOException {
                String meanLine = parserState.getNextLine();
                Matcher meanMatcher = MEAN_PATTERN.matcher(meanLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI mean distance: "+meanLine);
                }
                return Float.parseFloat(meanMatcher.group(1));
            }
            
            private long parseMin(ParserState parserState, AsmVisitor visitor) throws IOException {
                String minLine = parserState.getNextLine();
                Matcher meanMatcher = minPattern.matcher(minLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI min distance: "+minLine);
                }
                return Long.parseLong(meanMatcher.group(1));
            }
            private long parseMax(ParserState parserState, AsmVisitor visitor) throws IOException {
                String maxLine = parserState.getNextLine();
                Matcher meanMatcher = maxPattern.matcher(maxLine);
                if(!meanMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI max distance: "+maxLine);
                }
                return Long.parseLong(meanMatcher.group(1));
            }
            
            private List<Long> parseHistogram(ParserState parserState, AsmVisitor visitor) throws IOException {
                String histLine =parserState.getNextLine();
                Matcher bucketMatcher = histogramBucketPattern.matcher(histLine);
                if(!bucketMatcher.find()){
                    throw new IOException("invalid asm file: could not parse MDI number of histogram buckets: "+histLine);
                }
                //even though the ASM spec says
                //unsigned Int32, there shouldn't
                //ever be more then Integer.MAX_VALUE buckets!
                //Even if an ASM file had  a larger number of buckets
                //we woudn't be able to create the histogram
                //because List and arrays have a max value of 
                //Integer.MAX_VALUE.
                //Integer.parseInt javadoc says it will throw
                //NumberFormatException if value is too big
                //so we are covered.
                int numBuckets = Integer.parseInt(bucketMatcher.group(1));
                String histogramStart =parserState.getNextLine();
                if(!histogramStart.startsWith("his:")){
                    throw new IOException("invalid asm file: could not parse MDI start of histogram values: "+histogramStart);
                 }
                List<Long> histogram = new ArrayList<Long>(numBuckets);
                for(int i=0; i< numBuckets; i++){
                    String line = parserState.getNextLine();
                    histogram.add(Long.parseLong(line.trim()));
                }
                return histogram;
            }
            
            
        },
        FRAGMENT("AFG"){
             
            private final Pattern isSingletonPattern = Pattern.compile("cha:(\\d+)");
            private final Pattern clearRangePattern = Pattern.compile("clr:(\\d+,\\d+)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                IdTuple idTuple =parseIds(parserState, visitor, ACCESSION_PATTERN);
                //old asm files used to have a scn block 
                //which should be ignored
                String scnLine = parserState.getNextLine();
                if(scnLine.startsWith("scn")){
                    while(!scnLine.startsWith(".")){
                        scnLine = parserState.getNextLine();
                    }
                    //get next line which should be status
                    scnLine = parserState.getNextLine();
                }
                MateStatus mateStatus = parseMateStatus(scnLine);
                //is chimeric line is now ignored
                //but we need to visit the line anyway
                //to correctly update offset counts
                parserState.getNextLine();                
                boolean isSingleton = parseIsSingleton(parserState, visitor);
                Range clearRange = parseClearRange(parserState, visitor);
                parseEndOfMessage(parserState, this.getMessageCode());
                visitor.visitRead(idTuple.externalId, idTuple.internalId,
                        mateStatus, isSingleton, clearRange);
            }

            private Range parseClearRange(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = clearRangePattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("invalid asm file: could not parse AFG clear range: "+line);
                }
                return Range.parseRange(matcher.group(1),CoordinateSystem.SPACE_BASED);
            }

            
            /**
             * Is singleton if chaff value is set to 1; 0 otherwise.
             */
            private boolean parseIsSingleton(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = isSingletonPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("invalid asm file: could not parse AFG singlton status: "+line);
                }
                return Integer.parseInt(matcher.group(1).trim())==1;
            }
            
        },
        MATE_PAIR("AMP"){
            private final Pattern frgIdPattern = Pattern.compile("frg:(\\S+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                String id1 = parseReadId(parserState);
                String id2 = parseReadId(parserState);
                MateStatus mateStatus = parseMateStatus(parserState);
                parseEndOfMessage(parserState, this.getMessageCode());
                visitor.visitMatePair(id1, id2, mateStatus);
                
            }
            private String parseReadId(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = frgIdPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading frg id :"+ line);
                }
                return matcher.group(1);
            }
            
        },
        UNITIG("UTG"){
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                IdTuple idTuple =parseIds(parserState, visitor, ACCESSION_PATTERN);
                String nextLine = parserState.getNextLine();
                //CA <= 5 had a src block which should be ignored
                //CA 6+ doesn't have it anymore so need to handle
                //both cases.
                if(nextLine.startsWith("src")){
                    skipReservedSource(parserState);
                    nextLine = parserState.getNextLine();
                }
                float aStat = parseAStat(nextLine);
                nextLine = parserState.getNextLine();
                //measure of polymorphism
                //was introduced in CA 6
                //looks like older versions
                //had this value inside the src field
                final float polymorphism;
                if(nextLine.startsWith("mhp")){
                    polymorphism = parsePolymorphismMeasure(nextLine);
                    nextLine = parserState.getNextLine();
                }else{
                    polymorphism = Float.NaN;
                }
                UnitigStatus status = parseUnitigStatus(nextLine);
                nextLine = parserState.getNextLine();
                //skip legacy branch point lines
                //which don't exist in newer versions of CA
                if(nextLine.startsWith("abp")){
                    //skip bbp as well
                   parserState.getNextLine();
                   parserState.getNextLine();
                }
               
                int length = parseLength(nextLine);
                NucleotideSequence consensus = parseConsensus(parserState, length);
                QualitySequence consensusQualities = parseConsensusQualities(parserState,length);
                //skip forced line
                parserState.getNextLine();
                long numberOfReads = parseNumberOfReads(parserState);
                CallBack callback = parserState.createCallback();
                AsmUnitigVisitor asmUnitigVisitor =visitor.visitUnitig(callback, idTuple.externalId, idTuple.internalId, aStat,
                        polymorphism, status, consensus, consensusQualities, numberOfReads);
                //read info is nested in each unitig
                for(int i=0; parserState.keepParsing() && i<numberOfReads; i++){
                    String readHeader = parserState.getNextLine();
                    Matcher matcher = MESSAGE_HEADER_PATTERN.matcher(readHeader);
                    if(!matcher.find()){
                        throw new IOException(
                                String.format("error reading read # %d for unitig %s; invalid header :%s",
                                        i,idTuple.externalId,readHeader));
                    }
                    String code = matcher.group(1);
                    if(!ReadMapping.INSTANCE.canHandle(code)){
                        throw new IOException(
                                String.format("error reading read # %d for unitig %s; invalid header code :%s",
                                        i,idTuple.externalId,code));
                    
                    }
                    ReadMapping.INSTANCE.handleReadLayout(parserState, asmUnitigVisitor);
                }
                if(asmUnitigVisitor !=null){
                	if(parserState.keepParsing()){
		                parseEndOfMessage(parserState, this.getMessageCode());
		                asmUnitigVisitor.visitEnd();
	                }else{
	                	asmUnitigVisitor.halted();
	                }
                }
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
            
            

            
            
        },
        
        UNITIG_LINK("ULK"){
            private final Pattern unitigIdPattern = Pattern.compile("ut\\d:(\\S+)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor) throws IOException{
                handle(parserState, visitor,true);
            }
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor, boolean shouldParse)
                    throws IOException {
                parseLinkMessage(parserState, visitor, shouldParse, unitigIdPattern);
            }
            @Override
            protected void visitLink(AsmVisitor visitor, String unitig1,
                    String unitig2, LinkOrientation orientation,
                    OverlapType overlapType, boolean isPossibleChimera,
                    float mean, float stdDev, int numberOfContributingEdges,
                    OverlapStatus status, Set<MatePairEvidence> evidenceList) {
                visitor.visitUnitigLink(unitig1, unitig2, orientation, overlapType, status, 
                        isPossibleChimera, numberOfContributingEdges, mean, stdDev, evidenceList);
            }
            
            
            
        },
        CONTIG("CCO"){
            private final Pattern degeneratePattern = Pattern.compile("pla:(\\S)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
                IdTuple idTuple =parseIds(parserState, visitor, ACCESSION_PATTERN);
                boolean isDegenerate = parseIsDegenerateFlag(parserState, visitor);
                String lengthLine = parserState.getNextLine();
                int length = parseLength(lengthLine);
                NucleotideSequence consensus = parseConsensus(parserState, length);
                QualitySequence consensusQualities = parseConsensusQualities(parserState,length);
                //skip forced line
                parserState.getNextLine();
                long numberOfReads = parseNumberOfReads(parserState);
                long numberOfUnitigs = parseNumberOfReads(parserState);
                long numberOfVariants = parseNumberOfReads(parserState);
                CallBack callback = parserState.createCallback();
                AsmContigVisitor contigVisitor =visitor.visitContig(callback, idTuple.externalId, idTuple.internalId, isDegenerate, 
									                        consensus, consensusQualities, 
									                        numberOfReads, numberOfUnitigs, numberOfVariants);
                if(!parserState.keepParsing()){
                	return;
                }
                if(contigVisitor ==null){
                	//skip everything
                	long allSubBlocks = numberOfVariants + numberOfReads + numberOfUnitigs;
                	for(long i=0; i<allSubBlocks; i++){
                        skipCurrentBlock(parserState);
                    }                	
                }else{
                	for(long i=0; parserState.keepParsing() && i<numberOfVariants; i++){
                        String variantHeader = parserState.getNextLine();
                        String messageCode = parseMessageCode(variantHeader);
                        if(!ContigVariant.INSTANCE.canHandle(messageCode)){
                            throw new IOException("invalid variant block start : "+ variantHeader);
                        }
                        ContigVariant.INSTANCE.handle(parserState, contigVisitor);
                    }
                	
                	for(long i=0; parserState.keepParsing() && i<numberOfReads; i++){
                        String readHeader = parserState.getNextLine();
                        String messageCode = parseMessageCode(readHeader);
                        if(!ReadMapping.INSTANCE.canHandle(messageCode)){
                            throw new IOException("invalid read mapping block start : "+ readHeader);
                        }
                        ReadMapping.INSTANCE.handleReadLayout(parserState, contigVisitor);
                    }
                	
                	for(long i=0; parserState.keepParsing() && i<numberOfUnitigs; i++){
                        String unitigHeader = parserState.getNextLine();
                        String messageCode = parseMessageCode(unitigHeader);
                        if(!ContigUnitigMapping.INSTANCE.canHandle(messageCode)){
                            throw new IOException("invalid unitig mapping block start : "+ unitigHeader);
                        }
                        ContigUnitigMapping.INSTANCE.handle(parserState, contigVisitor);
                    }
                	if(parserState.keepParsing()){
	                	 parseEndOfMessage(parserState, this.getMessageCode());
	                	 contigVisitor.visitEnd();
                	}else{
                		contigVisitor.halted();
                	}
                }
                
                
               
                
            }

            private boolean parseIsDegenerateFlag(ParserState parserState,
                    AsmVisitor visitor) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = degeneratePattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading contig placement status (degenerate flag):"+ line);
                }
                //P is placed in a scaffold (maybe even a scaffold of just this contig
                //U is unplaced i.e. degenerate
                return matcher.group(1).charAt(0)=='U';
            }
            
        },
        
        
        CONTIG_LINK("CLK"){
            private final Pattern contigIdPattern = Pattern.compile("co\\d:(\\S+)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor) throws IOException{
                handle(parserState, visitor,true);
            }
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor, boolean shouldParse)
                    throws IOException {
                parseLinkMessage(parserState, visitor, shouldParse, contigIdPattern);
            }
            @Override
            protected void visitLink(AsmVisitor visitor, String id1,
                    String id2, LinkOrientation orientation,
                    OverlapType overlapType, boolean isPossibleChimera,
                    float mean, float stdDev, int numberOfContributingEdges,
                    OverlapStatus status, Set<MatePairEvidence> evidenceList) {
                visitor.visitContigLink(id1, id2, orientation, overlapType, status, 
                        numberOfContributingEdges, mean, stdDev, evidenceList);
            }
            
        },
        SCAFFOLD_LINK("SLK"){
            private final Pattern scaffoldIdPattern = Pattern.compile("sc\\d:(\\S+)");
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor) throws IOException{
                handle(parserState, visitor,true);
            }
            
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor, boolean shouldParse)
                    throws IOException {
                parseLinkMessage(parserState, visitor, shouldParse, scaffoldIdPattern);
            }
            @Override
            protected void visitLink(AsmVisitor visitor, String id1,
                    String id2, LinkOrientation orientation,
                    OverlapType overlapType, boolean isPossibleChimera,
                    float mean, float stdDev, int numberOfContributingEdges,
                    OverlapStatus status, Set<MatePairEvidence> evidenceList) {
                visitor.visitScaffoldLink(id1, id2, orientation, overlapType, status, 
                        numberOfContributingEdges, mean, stdDev, evidenceList);
            }
            
        },
        /**
         * Handles scaffold messages <strong>and</strong>
         * nested contig link messages since
         * there are different kinds of links depending 
         * on the number of contigs in the scaffold.
         */
        SCAFFOLD("SCF"){
            final Pattern numPairsPattern = Pattern.compile("noc:(\\d+)");
            final Pattern contigIdPattern = Pattern.compile("ct\\d:(\\S+)");
            @Override
            protected void handle(ParserState parserState, AsmVisitor visitor)
                    throws IOException {
            	CallBack callback = parserState.createCallback();
                IdTuple idTuple = parseIds(parserState, visitor, ACCESSION_PATTERN);
                int numberOfContigPairs = parseNumberOfContigPairs(parserState);
                if(numberOfContigPairs==0){
                    //only 1 contig
                    handleSingleContig(callback, parserState, idTuple,visitor);
                }else{
                	
                	AsmScaffoldVisitor scaffoldVisitor = visitor.visitScaffold(callback, idTuple.externalId, idTuple.internalId, numberOfContigPairs);
                    if(scaffoldVisitor !=null){
	                    for(int i=0; parserState.keepParsing() && i< numberOfContigPairs; i++){
	                        handleContigPairs(parserState, scaffoldVisitor);
	                    }
	                    if(parserState.keepParsing()){
	                    	parseEndOfMessage(parserState, this.getMessageCode());
	                    	scaffoldVisitor.visitEnd();
	                    }else{
	                    	scaffoldVisitor.halted();
	                    }
                    }
                }
            }
            
            private void handleSingleContig(CallBack callback, ParserState parserState,IdTuple idTuple,
            		AsmVisitor visitor) throws IOException {
                parsePairStart(parserState);
                String contigId = parseContigId(parserState);
                //ids should be the same
                String duplicateId = parseContigId(parserState);
                if(!contigId.equals(duplicateId)){
                    throw new IOException(
                            String.format(
                        		"invalid single contig scaffold, contig ids ct1 and ct2 should be identical %s vs %s",
                            		contigId, duplicateId));
                }
                //only 1 contig mean, stddev and ori should be ignored
                parserState.getNextLine();
                parserState.getNextLine();
                parserState.getNextLine();
                parseEndOfMessage(parserState, this.getMessageCode());
                visitor.visitScaffold(callback, idTuple.externalId,idTuple.internalId,contigId);
                
            }

            private void handleContigPairs(ParserState parserState,
            		AsmScaffoldVisitor scaffoldVisitor) throws IOException {
                parsePairStart(parserState);
                String contigId1 = parseContigId(parserState); 
                String contigId2 = parseContigId(parserState);                
                float mean = parseMeanEdgeDistance(parserState);
                float stdDev = parseStdDevDistance(parserState);
                LinkOrientation orientation = getLinkOrientation(parserState);
                
                parseEndOfMessage(parserState, this.getMessageCode());                
                scaffoldVisitor.visitContigPair(contigId1, contigId2, mean, stdDev, orientation);
                
            }
            private int parseNumberOfContigPairs(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = numPairsPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error parsing number of contig pairs : "+ line);
                }
                return Integer.parseInt(matcher.group(1));
            }
            
            private String parseContigId(ParserState parserState) throws IOException{
                String line = parserState.getNextLine();
                Matcher matcher = contigIdPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error contig id :"+ line);
                }
                return matcher.group(1);
            }
            
            private void parsePairStart(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                if(!line.startsWith("{CTP")){
                    throw new IOException("error parsing contig pair header : "+ line);
                }
            }
            
        },
        
        ;
        
        
        private final String messageCode;
        static Pattern MESSAGE_HEADER_PATTERN = Pattern.compile("\\{(\\S+)");
        static final Pattern MATE_STATUS_PATTERN = Pattern.compile("mst:(\\S)");
        static final Pattern ACCESSION_PATTERN = Pattern.compile("acc:\\((\\S+),(\\d+)\\)");
        static final Pattern MEAN_PATTERN = Pattern.compile("mea:(\\S+)");
        static final Pattern STD_DEV_PATTERN = Pattern.compile("std:(\\S+)");
        
        static final Pattern A_STAT_PATTERN = Pattern.compile("cov:(\\S+)");
        static final Pattern POLYMORPHISM_PATTERN = Pattern.compile("mhp:(\\S+)");
        static final Pattern STATUS_PATTERN = Pattern.compile("sta:(\\S)");
        
        
        final Pattern linkOrientationPattern = Pattern.compile("ori:(\\S)");
        final Pattern overlapTypePattern = Pattern.compile("ovt:(\\S)");
        
        final Pattern chimeraFlagPattern = Pattern.compile("ipc:(\\d)");
        final Pattern numEdgesPattern = Pattern.compile("num:(\\d+)");
        
        final Pattern linkStatusPattern = Pattern.compile("sta:(\\S)");
        final Pattern jumpListPattern = Pattern.compile("(\\S+),(\\S+),(\\S)");
        
        
       
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

        public String parseMessageCode(String line){
            Matcher matcher = MESSAGE_HEADER_PATTERN.matcher(line);
            if(matcher.find()){
                return matcher.group(1);
            }
            return null;
        }
        protected abstract void handle(ParserState parserState, AsmVisitor visitor) throws IOException;
        
        protected void handle(ParserState parserState, AsmVisitor visitor, boolean shouldVisitRecord) throws IOException{
            handle(parserState, visitor);
        }
        public static void parse(ParserState parserState, AsmVisitor visitor) throws IOException{
            while(parserState.hasNextLine()){
            	parserState.markCurrentOffset();
                String line =parserState.getNextLine();
                if(line !=null){                    
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
            if(parserState.keepParsing()){
            	visitor.visitEnd();
            }else{
            	visitor.halted();
            }
        }
        
        
        
        IdTuple parseIds(ParserState parserState, AsmVisitor visitor, Pattern pattern) throws IOException {
            String idLine =parserState.getNextLine();
            Matcher idMatcher = pattern.matcher(idLine);
            if(!idMatcher.find()){
                throw new IOException("invalid asm file: could not parse IDs: "+idLine);
            }
            return new IdTuple(idMatcher.group(1), Long.parseLong(idMatcher.group(2)));
        }
       
        
        MateStatus parseMateStatus(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = MATE_STATUS_PATTERN.matcher(line);
            if(!matcher.find()){
                throw new IOException("invalid asm file: could not parse "+messageCode+" mate status: "+line);
            }
            return MateStatus.parseMateStatus(matcher.group(1));
        }
        
        MateStatus parseMateStatus(String line) throws IOException {
            Matcher matcher = MATE_STATUS_PATTERN.matcher(line);
            if(!matcher.find()){
                throw new IOException("invalid asm file: could not parse "+messageCode+" mate status: "+line);
            }
            return MateStatus.parseMateStatus(matcher.group(1));
        }
        
        
        
       
        QualitySequence parseConsensusQualities(
                ParserState parserState, int length) throws IOException {
            byte[] qualities = new byte[length];
            //first line should be qlt
            String line = parserState.getNextLine();
            if(!line.startsWith("qlt:")){
               throw new IOException("expected start quality consensus block :"+line); 
            }
            line = parserState.getNextLine();
            int offset=0;
            while(!line.startsWith(".")){
                String trimmedLine = line.trim();
                for(int i=0; i<trimmedLine.length(); i++){
                    //qualities are encoded as value + ascii zero
                    qualities[offset+i]=(byte)(trimmedLine.charAt(i)- '0');
                }
                offset +=trimmedLine.length();
                line = parserState.getNextLine();
            }
            if(offset !=length){
                throw new IOException( String.format("incorrect consensus quality length for %s: expected %d but was %d",
                        getMessageCode(),
                        length,offset));
            }
            return new QualitySequenceBuilder(qualities).build();
        }
        NucleotideSequence parseConsensus(ParserState parserState,
                int expectedLength) throws IOException {
            NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(expectedLength);
            String line = parserState.getNextLine();
            if(!line.startsWith("cns:")){
                throw new IOException("expected begin cns field but was "+line );
            }
            line = parserState.getNextLine();
            while(!line.startsWith(".")){
                builder.append(line.trim());
                line = parserState.getNextLine();
            }
            if(builder.getLength()!=expectedLength){
                throw new IOException(
                        String.format("incorrect consensus length for %s: expected %d but was %d",
                                getMessageCode(),
                                expectedLength,builder.getLength()));
            }
            return builder.build();
        }
        

        Set<MatePairEvidence> parseMatePairEvidence(
                int expectedNumberOfMatePairEvidenceRecords,
                ParserState parserState) throws IOException {
            Set<MatePairEvidence> set = new LinkedHashSet<MatePairEvidence>();
            for(int i=0; i<expectedNumberOfMatePairEvidenceRecords; i++){
                String line = parserState.getNextLine();
                Matcher matcher = jumpListPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("invalid jump list record: "+ line);
                }
                set.add(new MatePairEvidenceImpl(matcher.group(1), matcher.group(2)));
            }
            return set;
        }

        OverlapStatus parseOverlapStatus(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = linkStatusPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error overlap status"+ line);
            }
            return OverlapStatus.parseOverlapStatus(matcher.group(1));
        }

        int parseNumberOfEdges(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = numEdgesPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading # of edges"+ line);
            }
            return Integer.parseInt(matcher.group(1));
        }

        float parseMeanEdgeDistance(String nextLine) throws IOException {
            Matcher matcher = MEAN_PATTERN.matcher(nextLine);
            if(!matcher.find()){
                throw new IOException("error reading is mean edge distance message"+ nextLine);
            }
            return Float.parseFloat(matcher.group(1));
        }
        float parseMeanEdgeDistance(ParserState parserState) throws IOException {
            String nextLine = parserState.getNextLine();
            Matcher matcher = MEAN_PATTERN.matcher(nextLine);
            if(!matcher.find()){
                throw new IOException("error reading is mean edge distance message"+ nextLine);
            }
            return Float.parseFloat(matcher.group(1));
        }
        
        float parseStdDevDistance(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = STD_DEV_PATTERN.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading is std dev edge distance message"+ line);
            }
            return Float.parseFloat(matcher.group(1));
        }

        boolean getChimeraFlag(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = chimeraFlagPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading is possible chimera message"+ line);
            }
            int value = Integer.parseInt(matcher.group(1));
            return value ==1;
        }

        LinkOrientation getLinkOrientation(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = linkOrientationPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading link orientation message"+ line);
            }
            return LinkOrientation.parseLinkOrientation(matcher.group(1));
        }
        
        OverlapType getOverlapType(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = overlapTypePattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading overlap type message"+ line);
            }
            return OverlapType.parseOverlapType(matcher.group(1));
        }
        
        String getUnitigId(Pattern idPattern, ParserState parserState) throws IOException{
            String line = parserState.getNextLine();
            Matcher matcher = idPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading unitig link message unitig id:"+ line);
            }
            return matcher.group(1);
        }

        void parseLinkMessage(ParserState parserState, AsmVisitor visitor,
                boolean shouldParse, Pattern idPattern) throws IOException {
            String unitig1 = getUnitigId(idPattern, parserState);
            String unitig2 = getUnitigId(idPattern, parserState);
            LinkOrientation orientation = getLinkOrientation(parserState);
            OverlapType overlapType = getOverlapType(parserState);
            boolean isPossibleChimera = getChimeraFlag(parserState);
            //includes guide was removed in CA 6
            String nextLine = parserState.getNextLine();
            if(nextLine.startsWith("gui:")){
                nextLine = parserState.getNextLine();
            }
            float mean = parseMeanEdgeDistance(nextLine);
            float stdDev = parseStdDevDistance(parserState);
            int numberOfContributingEdges = parseNumberOfEdges(parserState);
            OverlapStatus status = parseOverlapStatus(parserState);
            String jumpList = parserState.getNextLine();
            if(!jumpList.startsWith("jls:")){
                throw new IOException("invalid jump list block : "+ jumpList);
            }
            Set<MatePairEvidence> evidenceList = parseMatePairEvidence(overlapType.getExpectedNumberOfMatePairEvidenceRecords(numberOfContributingEdges), parserState);
            parseEndOfMessage(parserState, messageCode);
            if(shouldParse){
                visitLink(visitor, unitig1, unitig2, orientation,
                        overlapType, isPossibleChimera, mean, stdDev,
                        numberOfContributingEdges, status, evidenceList);
            }
        }
        
        protected void visitLink(AsmVisitor visitor, String unitig1,
                String unitig2, LinkOrientation orientation,
                OverlapType overlapType, boolean isPossibleChimera,
                float mean, float stdDev, int numberOfContributingEdges,
                OverlapStatus status, Set<MatePairEvidence> evidenceList){
            throw new IllegalStateException("invalid state should not contain any links");
        }

    }
    
    private enum ContigUnitigMapping{
    	INSTANCE("UPS")
    	;
    	private final String messageCode;
    	
        private final Pattern typePattern = Pattern.compile("typ:(\\S)");
        private final Pattern idPattern = Pattern.compile("lid:(\\S+)");
        private final Pattern rangePattern = Pattern.compile("pos:(\\d+,\\d+)");
        private final Pattern numOffsetsPattern = Pattern.compile("dln:(\\d+)");
        
        ContigUnitigMapping(String code){
        	this.messageCode = code;
        }
        final boolean canHandle(String messageCode){
            return this.messageCode.equals(messageCode);
        }

        protected void handle(ParserState parserState, AsmContigVisitor visitor) throws IOException {
                UnitigLayoutType type = parseUnitigLayoutType(parserState);
                String readId = parseReadId(parserState);
                String nextLine = parserState.getNextLine();
                //CA <= 5 had a src block which should be ignored
                //CA 6+ doesn't have it anymore so need to handle
                //both cases.
                if(nextLine.startsWith("src")){
                    skipReservedSource(parserState);
                    nextLine = parserState.getNextLine();
                }
                
                DirectedRange directedRange = parseDirectedRange(nextLine);
                List<Long> gapOffsets = parseGapOffsets(parserState);
                parseEndOfMessage(parserState, messageCode);
           
                visitor.visitUnitigLayout(type, readId, directedRange, gapOffsets);
            
        }
        private DirectedRange parseDirectedRange(String line) throws IOException {
            Matcher matcher = rangePattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig placed range:"+ line);
            }
            return DirectedRange.parse(matcher.group(1));
        }
        private List<Long> parseGapOffsets(ParserState parserState) throws IOException {
            String lengthLine = parserState.getNextLine();
            Matcher matcher = numOffsetsPattern.matcher(lengthLine);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig delta encoding length:"+ lengthLine);
            }
            int expectedNumberOfOffsets = Integer.parseInt(matcher.group(1));
            String beginDeltaEncodingLine = parserState.getNextLine();
            if(!beginDeltaEncodingLine.startsWith("del:")){
                throw new IOException("error reading read-to-unitig delta encoding:"+ beginDeltaEncodingLine);
            }
            List<Long> offsets = new ArrayList<Long>(expectedNumberOfOffsets);
            while(offsets.size()<expectedNumberOfOffsets){
                String offsetLine = parserState.getNextLine();
                //TODO Scanner is so slow use different implementation?
                Scanner scanner = new Scanner(offsetLine);
                if(!scanner.hasNextLong()){
                    throw new IOException("error reading read-to-unitig delta encoding not enough values :"+ offsetLine);
                    
                }
                while(scanner.hasNextLong()){
                    offsets.add(scanner.nextLong());
                }
            }
            return offsets;
        }
        private String parseReadId(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = idPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig read id:"+ line);
            }
            return matcher.group(1);
        }
        private UnitigLayoutType parseUnitigLayoutType(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = typePattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading unitig-to-contig mapping type:"+ line);
            }
            return UnitigLayoutType.parseUnitigLayoutType(matcher.group(1));
        }
    }
    
    
    private enum ContigVariant{
    	INSTANCE("VAR");
    	
    	private final String messageCode;
    	 final Pattern positionPattern = Pattern.compile("pos:(\\d+,\\d+)");
         final Pattern anchorPattern = Pattern.compile("anc:(\\d+)");
         final Pattern variantIdPattern = Pattern.compile("vid:(\\d+)");
         final Pattern phasePattern = Pattern.compile("pid:(\\S+)");
         
    	ContigVariant(String code){
    		this.messageCode = code;
    	}
           
    	 final boolean canHandle(String messageCode){
             return this.messageCode.equals(messageCode);
         }
             protected void handle(ParserState parserState, AsmContigVisitor contigVisitor) throws IOException {
                Range position = parseVariantPosition(parserState);
                long numberOfReads = parseNumberOfReads(parserState);
                //Java is limited to signed int
                //throw exception in unlikely even that we have 2^15 coverage.                
                if(numberOfReads > Integer.MAX_VALUE){
                	throw new NumberFormatException("too many reads in variant must be < Integer.MAX_VALUE");
                }
                long numberOfVariants = parseNumberOfVariants(parserState);
                //Java is limited to signed int
                //throw exception in unlikely even that we have 2^15 different variants.                
                if(numberOfVariants > Integer.MAX_VALUE){
                	throw new NumberFormatException("too many variants must be < Integer.MAX_VALUE");
                }
                long anchorSize = parseAnchorSize(parserState);
                //skip length?
                parseLength(parserState);
                long variantId = parseVariantId(parserState);
                long phasedVariantId = parsePhasedVariantId(parserState);
                String[] contributingReadCountString = SPLIT_ON_SLASH.split(parseContributingReadcountString(parserState));
                String[] weightString = SPLIT_ON_SLASH.split(parseWeightString(parserState));
                String[] sequencesString = SPLIT_ON_SLASH.split(parseSequencesString(parserState));
                String supportingReadIds = parseSupportingReadsString(parserState);
                List<Long> readIds = new ArrayList<Long>((int)numberOfReads);                
                for(String id : SPLIT_ON_SLASH.split(supportingReadIds)){
                    readIds.add(Long.parseLong(id.trim()));
                }
                
                parseEndOfMessage(parserState, messageCode);
                
                SortedSet<VariantRecord> variantRecords = new TreeSet<VariantRecord>();
                int readCounter=0;
                int numberOfVariantsAsInt = (int)numberOfVariants;
                for(int i=0; i < numberOfVariantsAsInt; i++){
                    int numContributingReads = Integer.parseInt(contributingReadCountString[i].trim());
                    int weight = Integer.parseInt(weightString[i].trim());
                    NucleotideSequence seq = new NucleotideSequenceBuilder(sequencesString[i]).build();
                    List<Long> reads = readIds.subList(readCounter, readCounter+numContributingReads);
                    variantRecords.add(new VariantRecordImpl(reads, seq, weight));
                    readCounter+=numContributingReads;
                }
                contigVisitor.visitVariance(position, numberOfReads, anchorSize, 
                        variantId, phasedVariantId, 
                        variantRecords);
                
             }
            
            private String parseContributingReadcountString(
                    ParserState parserState) throws IOException {
                return parseVariantBlock(parserState, "nra:");
            }
            private String parseWeightString(ParserState parserState) throws IOException {
                return parseVariantBlock(parserState, "wgt:");
            }
            private String parseSequencesString(ParserState parserState) throws IOException {
                return parseVariantBlock(parserState, "seq:");
            }
            private String parseSupportingReadsString(ParserState parserState) throws IOException {
                return parseVariantBlock(parserState, "rid:");
            }
            private String parseVariantBlock(ParserState parserState, String expectedBlockStart) throws IOException{
                String line = parserState.getNextLine();
                if(!line.startsWith(expectedBlockStart)){
                    throw new IOException("invalid start of variants block section : "+ line);
                }
                String value = parserState.getNextLine();
                String endBlock = parserState.getNextLine();
                if(!endBlock.startsWith(".")){
                    throw new IOException("invalid end of variant block section : "+ endBlock);
                }
                return value;
            }
            private long parseNumberOfVariants(ParserState parserState) throws IOException {
                // delegate to parse number of reads since the pattern
                //is close enough, this method name is just
                //to avoid confusion/ make it intent revealing
                return parseNumberOfReads(parserState);
            }
            private Range parseVariantPosition(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = positionPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading variant record position:"+ line);
                }
                return Range.parseRange(matcher.group(1), CoordinateSystem.SPACE_BASED);
            }
            private long parseAnchorSize(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = anchorPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading variant anchor size:"+ line);
                }
                return Long.parseLong(matcher.group(1));
            }
            private long parseVariantId(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = variantIdPattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading variant id"+ line);
                }
                return Long.parseLong(matcher.group(1));
            }
            private long parsePhasedVariantId(ParserState parserState) throws IOException {
                String line = parserState.getNextLine();
                Matcher matcher = phasePattern.matcher(line);
                if(!matcher.find()){
                    throw new IOException("error reading variant id"+ line);
                }
                return Long.parseLong(matcher.group(1));
            }
        }
    
    
    private enum ReadMapping{
    	INSTANCE("MPS");
    	
    	private final String messageCode;
    	ReadMapping(String code){
    		this.messageCode = code;
    	}
    
        private final Pattern typePattern = Pattern.compile("typ:(\\S)");
        private final Pattern readIdPattern = Pattern.compile("mid:(\\S+)");
        private final Pattern rangePattern = Pattern.compile("pos:(\\d+,\\d+)");
        private final Pattern numOffsetsPattern = Pattern.compile("dln:(\\d+)");
        
        final boolean canHandle(String messageCode){
            return this.messageCode.equals(messageCode);
        }
   
        public void handleReadLayout(ParserState parserState, AsmUnitigVisitor visitor) throws IOException {
            if(visitor !=null){
               char type = parseReadType(parserState);
               String readId = parseReadId(parserState);
               String nextLine = parserState.getNextLine();
               //CA <= 5 had a src block which should be ignored
               //CA 6+ doesn't have it anymore so need to handle
               //both cases.
               if(nextLine.startsWith("src")){
                   skipReservedSource(parserState);
                   nextLine = parserState.getNextLine();
               }
               
               DirectedRange directedRange = parseDirectedRange(nextLine);
               List<Integer> gapOffsets = parseGapOffsets(parserState);
               parseEndOfMessage(parserState, messageCode);                   
               visitor.visitReadLayout(type, readId, directedRange, gapOffsets);
            }else{
                skipCurrentBlock(parserState);
            }
        }
        
        public void handleReadLayout(ParserState parserState, AsmContigVisitor visitor) throws IOException {
            if(visitor !=null){
               char type = parseReadType(parserState);
               String readId = parseReadId(parserState);
               String nextLine = parserState.getNextLine();
               //CA <= 5 had a src block which should be ignored
               //CA 6+ doesn't have it anymore so need to handle
               //both cases.
               if(nextLine.startsWith("src")){
                   skipReservedSource(parserState);
                   nextLine = parserState.getNextLine();
               }
               
               DirectedRange directedRange = parseDirectedRange(nextLine);
               List<Integer> gapOffsets = parseGapOffsets(parserState);
               parseEndOfMessage(parserState, messageCode);                   
               visitor.visitReadLayout(type, readId, directedRange, gapOffsets);
            }else{
                skipCurrentBlock(parserState);
            }
        }
        private DirectedRange parseDirectedRange(String line) throws IOException {
            Matcher matcher = rangePattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig placed range:"+ line);
            }
            return DirectedRange.parse(matcher.group(1));
        }
        private List<Integer> parseGapOffsets(ParserState parserState) throws IOException {
            String lengthLine = parserState.getNextLine();
            Matcher matcher = numOffsetsPattern.matcher(lengthLine);
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
        private String parseReadId(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = readIdPattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig read id:"+ line);
            }
            return matcher.group(1);
        }
        private char parseReadType(ParserState parserState) throws IOException {
            String line = parserState.getNextLine();
            Matcher matcher = typePattern.matcher(line);
            if(!matcher.find()){
                throw new IOException("error reading read-to-unitig mapping type:"+ line);
            }
            return matcher.group(1).charAt(0);
        }
        
    }
    static int parseLength(ParserState parserState) throws IOException {
        String line = parserState.getNextLine();
        return parseLength(line);
    }
    private static int parseLength(String line) throws IOException {               
        Matcher matcher = LENGTH_PATTERN.matcher(line);
        if(!matcher.find()){
            throw new IOException("error reading length:"+ line);
        }
        return Integer.parseInt(matcher.group(1));
    }
    private static long parseNumberOfReads(ParserState parserState) throws IOException {
        String line = parserState.getNextLine();
        Matcher matcher = NUM_READS_PATTERN.matcher(line);
        if(!matcher.find()){
            throw new IOException("error parsing unitig number of reads : "+ line);
        }
        return Long.parseLong(matcher.group(1));
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

        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return "MatePairEvidenceImpl [read1=" + read1 + ", read2=" + read2
                    + "]";
        }
        
        
    }
    
    private static class VariantRecordImpl implements VariantRecord{

        private final List<Long> readIds;
        private final NucleotideSequence sequence;
        private final int weight;
        
        
        public VariantRecordImpl(List<Long> readIds,
                NucleotideSequence sequence, int weight) {
            this.readIds = Collections.unmodifiableList(readIds);
            this.sequence = sequence;
            this.weight = weight;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public List<Long> getContributingReadIIDs() {
            return readIds;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int getWeight() {
            return weight;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequence getVariantSequence() {
            return sequence;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int compareTo(VariantRecord o) {
        	return JillionUtil.compare(weight, o.getWeight());			
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((readIds == null) ? 0 : readIds.hashCode());
            result = prime * result
                    + ((sequence == null) ? 0 : sequence.hashCode());
            result = prime * result + weight;
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
            if (!(obj instanceof VariantRecordImpl)) {
                return false;
            }
            VariantRecordImpl other = (VariantRecordImpl) obj;
            if (readIds == null) {
                if (other.readIds != null) {
                    return false;
                }
            } else if (!readIds.equals(other.readIds)) {
                return false;
            }
            if (sequence == null) {
                if (other.sequence != null) {
                    return false;
                }
            } else if (!sequence.equals(other.sequence)) {
                return false;
            }
            if (weight != other.weight) {
                return false;
            }
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return "VariantRecordImpl [readIds=" + readIds + ", sequence="
                    + sequence + ", weight=" + weight + "]";
        }
        
    }
    
    private static void skipReservedSource(ParserState parserState) throws IOException {
        
        String line = "";
        while(!".\n".equals(line)){
            line = parserState.getNextLine();
        }
        
    }
    private  static void parseEndOfMessage(ParserState parserState, String messageCode) throws IOException {
        String endLine = parserState.getNextLine();
        if(!endLine.startsWith(END_MESSAGE)){
            throw new IOException("invalid asm file: invalid "+ messageCode +" end tag : " + endLine);
        }
    }
    
    private static void skipCurrentBlock(ParserState parserState) throws IOException {
        String line=null;
        do{
            line = parserState.getNextLine();
        }while(line !=null && !line.startsWith("}"));
        
    }
    
    private static abstract class CallBack implements AsmVisitorCallback{
    	private final AtomicBoolean keepParsing;

		public CallBack(AtomicBoolean keepParsing) {
			this.keepParsing = keepParsing;
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);			
		}
    	
    }
    
    
    private static class MementoedCallback extends CallBack{

    	private final long offset;
    	public MementoedCallback(AtomicBoolean keepParsing, long offset){
    		super(keepParsing);
    		this.offset = offset;
    	}
		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public AsmVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
    	
    }
    
    
    private static class OffsetMemento implements AsmVisitorMemento{
    	private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		protected final long getOffset() {
			return offset;
		}
    	
    }
    private static class FileBasedAsmFileParser extends AsmFileParser{
    	private final File asmFile;

		public FileBasedAsmFileParser(File asmFile) {
			this.asmFile = asmFile;
		}

		@Override
		public void accept(AsmVisitor visitor) throws IOException {
			InputStream in =null;
	        try{
	            in= new BufferedInputStream(new FileInputStream(asmFile));
	            ParserState parserState = new ParserState(in, 0){

					@Override
					public CallBack createCallback() {
						return new MementoedCallback(keepParsing, markedOffset);
					}
	            	
	            };
	            parseAsm(parserState,visitor);
	        }finally{
	            IOUtil.closeAndIgnoreErrors(in);
	        }
			
		}

		@Override
		public void accept(AsmVisitor visitor, AsmVisitorMemento memento)
				throws IOException {
			if( !(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type "+ memento + " must use instance created by this parser");
			}
			
			long offset = ((OffsetMemento)memento).getOffset();			
			InputStream in =null;
			try{
				in = new BufferedInputStream(new RandomAccessFileInputStream(asmFile, offset));
				ParserState parserState = new ParserState(in, offset){

					@Override
					public CallBack createCallback() {
						return new MementoedCallback(keepParsing, markedOffset);
					}
	            	
	            };
	            parseAsm(parserState,visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
    	
    }
   
}
