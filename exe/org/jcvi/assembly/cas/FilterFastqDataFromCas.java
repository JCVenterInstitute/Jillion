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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.clc.cas.CasInfo;
import org.jcvi.common.core.assembly.clc.cas.CasParser;
import org.jcvi.common.core.assembly.clc.cas.CasUtil;
import org.jcvi.common.core.assembly.clc.cas.consed.AbstractAcePlacedReadCasReadVisitor;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;

/**
 * @author dkatzel
 *
 *
 */
public class FilterFastqDataFromCas {

    public static void filterReads(File casFile,final CasInfo casInfo, 
            int maxSolexaCoverageDepth,PrintWriter out) throws IOException{
        final Map<Integer, List<ReadRange>> fastqReadMap = new TreeMap<Integer, List<ReadRange>>();
       
        AbstractAcePlacedReadCasReadVisitor visitor = new AbstractAcePlacedReadCasReadVisitor(casInfo) {
            
            @Override
            protected void visitMatch(AcePlacedRead acePlacedRead, Phd phd,
                    int casReferenceId) {
                String readId = acePlacedRead.getId();
                if(readId.startsWith("SOLEXA")){
                    Integer casRefId = casReferenceId;
                    if(!fastqReadMap.containsKey(casRefId)){
                        fastqReadMap.put(casRefId, new ArrayList<ReadRange>(1000000));
                    }
                    ReadRange readRange = new ReadRange(readId, acePlacedRead.asRange());
                    fastqReadMap.get(casRefId).add(readRange);
                }
            }
        };
        
        CasParser.parseCas(casFile, visitor);
        for(Entry<Integer, List<ReadRange>> entry : fastqReadMap.entrySet()){
            List<ReadRange> readRanges = entry.getValue();
            //shuffle reads around since they are 
            //probably sorted by name which for solexa means sorted by region on the machine
            //and we want
            //an even distribution
            Collections.shuffle(readRanges);
            CoverageMap<CoverageRegion<ReadRange>> coverageMap = DefaultCoverageMap.buildCoverageMap(readRanges, maxSolexaCoverageDepth);
            Set<String> reads = new TreeSet<String>();
            for(CoverageRegion<ReadRange> region : coverageMap){
                for(ReadRange readRange : region){
                    reads.add(readRange.getReadId());
                }
            }
            System.out.printf("filtered reference %d: %d -> %d%n",entry.getKey(), readRanges.size(),reads.size());
            for(String neededRead : reads){
                out.println(neededRead);
            }
        }
    }

    static Set<String> getNeededReadsFor(int maxSolexaCoverageDepth,
            CoverageMap<CoverageRegion<ReadRange>> coverageMap) {
        Set<String> neededReads = new TreeSet<String>();
        //first pass find all reads that are needed to meet min coverage levels
        for(CoverageRegion<ReadRange> region : coverageMap){
            int coverageDepth = region.getCoverage();
            if(coverageDepth <= maxSolexaCoverageDepth){
                //need all reads at this coverage level
                for(ReadRange readRange : region){
                    neededReads.add(readRange.getReadId());
                }
            }
        }
        //2nd pass find reads that aren't needed
        for(CoverageRegion<ReadRange> region : coverageMap){
            int coverageDepth = region.getCoverage();
            if(coverageDepth > maxSolexaCoverageDepth){                        
                Set<String> unseenReads = new HashSet<String>(coverageDepth);
                for(ReadRange readRange : region){
                    String id = readRange.getReadId();
                    if(!neededReads.contains(id)){
                        unseenReads.add(id);
                    }
                }
                int seenReadCount = coverageDepth - unseenReads.size();
                if(seenReadCount <maxSolexaCoverageDepth){
                    //we need to keep some
                    int numToKeep = maxSolexaCoverageDepth -seenReadCount;
                    int numSaved=0;
                    for(ReadRange readRange : region){
                        String id = readRange.getReadId();
                        if(!neededReads.contains(id)){
                            neededReads.add(id);
                            numSaved++;
                        }
                        if(numSaved==numToKeep){
                            break;
                        }
                    }                            
                }
            }
        }
        return neededReads;
    }
    
    public static void main(String[] args) throws IOException{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("cas", "path to cas file (required)")
                            .isRequired(true)
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("useIllumina", "any FASTQ files in this assembly are encoded in Illumina 1.3+ format (default is Sanger)")                                
        .isFlag(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("d", "max coverage depth.  any fastq reads that add more than this level of coverage will get filtered out (required)")
        .longName("max_depth")
        .isRequired(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("o", "output fastq include file which can be used to later create a filtered fastq file. (required)")
        .isRequired(true)
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File casFile = new File(commandLine.getOptionValue("cas"));
            FastqQualityCodec fastqQualityCodec=  commandLine.hasOption("useIllumina")?  
                    FastqQualityCodec.ILLUMINA
                 : FastqQualityCodec.SANGER;
            int maxSolexaCoverageDepth = Integer.parseInt(commandLine.getOptionValue("d"));
            File outputFile = new File(commandLine.getOptionValue("o"));
            //create dirs if needed
            IOUtil.mkdirs(outputFile.getParentFile());
            PrintWriter out = new PrintWriter(outputFile);
            //don't need to worry about trim points etc because
            //I don't actually care about the sequence...
            final CasInfo casInfo = CasUtil.createCasInfoBuilder(casFile)
                                    .fastQQualityCodec(fastqQualityCodec)            
                                    .build();
            
            
            filterReads(casFile, casInfo, maxSolexaCoverageDepth, out);           
            
            out.close();
            
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
        }
    }

    static class ReadRange implements Placed<ReadRange>{
        private final Range range;
        private final String readId;
        
        public ReadRange(String readId, Range range) {
            this.readId = readId;
            this.range = range;
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return "ReadRange [readId=" + readId + ", range=" + range + "]";
        }


        /**
         * @return the range
         */
        public Range getRange() {
            return range;
        }


        /**
         * @return the readId
         */
        public String getReadId() {
            return readId;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public int compareTo(ReadRange o) {
            return range.compareTo(o.getRange());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin() {
            return range.getBegin();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd() {
            return range.getEnd();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getLength() {
            return range.getLength();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange() {
            return range.asRange();
        }
        
        
        
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "filterFastqDataFromCLC -cas <cas file> -d <max coverage> -o <output include file> [OPTIONS]", 
                
                "Parse an CLC cas file and write out include list of solexa/ illumina read ids " +
                "that are required inorder achieve the specified max coverage depth.",
                options,
               "Created by Danny Katzel"
                  );
    }
    
}
