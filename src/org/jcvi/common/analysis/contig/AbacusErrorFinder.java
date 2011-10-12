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

package org.jcvi.common.analysis.contig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedNavigationWriter;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultExcludeDataStoreFilter;
import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.common.core.datastore.EmptyDataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

/**
 * @author dkatzel
 *
 *
 */
public class AbacusErrorFinder {
    private final int clusterDistance;
    private final int minAbacusLength;
    public AbacusErrorFinder(int clusterDistance, int minAbacusLength){
        this.clusterDistance = clusterDistance;
        this.minAbacusLength = minAbacusLength;
    }
    private <P extends PlacedRead, C extends Contig<P>> List<Range> filterCandidates(C contig,
            List<Range> ungappedCandidateRanges) {
        CoverageMap<CoverageRegion<P>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
        NucleotideSequence consensus = contig.getConsensus();
        List<Range> errorRanges = new ArrayList<Range>(ungappedCandidateRanges.size());
        for(Range ungappedCandidateRange : ungappedCandidateRanges){           
            int gappedStart = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getStart())+1;
            int gappedEnd = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getEnd()+1) -1;
            Range gappedCandidateRange = Range.buildRange(gappedStart, gappedEnd);
            Set<String> readIds = new HashSet<String>();
            for(CoverageRegion<P> region : coverageMap.getRegionsWhichIntersect(gappedCandidateRange)){
                for(P read : region){
                    readIds.add(read.getId());
                }
            }
            boolean isAbacusError=true;
            for(String readId : readIds){
                P read =contig.getPlacedReadById(readId);              
                long adjustedStart = Math.max(gappedCandidateRange.getStart(), read.getStart());
                long adjustedEnd = Math.min(gappedCandidateRange.getEnd(), read.getEnd());
                boolean spansEntireRegion = (adjustedStart == gappedCandidateRange.getStart()) && (adjustedEnd == gappedCandidateRange.getEnd());
                if(spansEntireRegion){
                    Range rangeOfInterest = Range.buildRange(
                            read.toGappedValidRangeOffset(adjustedStart),
                            read.toGappedValidRangeOffset(adjustedEnd));
                   double numGaps=0;
                    for(Nucleotide n :read.getNucleotideSequence().asList(rangeOfInterest)){
                        if(n.isGap()){
                            numGaps++;
                        }
                    }
                    double percentGaps = numGaps/rangeOfInterest.getLength();
                    if(percentGaps <.5D){
                        isAbacusError=false;
                        break;
                    }
                    
                }
            }
            if(isAbacusError){
                errorRanges.add(ungappedCandidateRange);
            }
            
        }
        return errorRanges;
    }

    private List<Range> convertToUngappedRanges(List<Range> abacusErrors,
            NucleotideSequence consensus) {
        List<Range> ungappedRanges = new ArrayList<Range>(abacusErrors.size());
        for(Range error : abacusErrors){
            if(error.getLength() >=5){
                int ungappedStart =consensus.getUngappedOffsetFor((int)error.getStart());
                int ungappedEnd =consensus.getUngappedOffsetFor((int)error.getEnd());
                
                ungappedRanges.add(Range.buildRange(ungappedStart, ungappedEnd)); 
            }
        }

        List<Range> candidateRanges = Range.mergeRanges(ungappedRanges);
        return candidateRanges;
    }
    public <P extends PlacedRead, C extends Contig<P>> List<Range>  findAbacusErrors(C contig){
        List<Range> ungappedCandidateRanges = getUngappedCandidateRanges(contig);
        return filterCandidates(contig, ungappedCandidateRanges) ;
        
    }
    private <P extends PlacedRead, C extends Contig<P>> List<Range> getUngappedCandidateRanges(C contig) {
        
        List<Range> gapRangesPerRead = new ArrayList<Range>(contig.getNumberOfReads());
        for(P placedRead : contig.getPlacedReads()){           
            List<Range> gaps = new ArrayList<Range>(placedRead.getNucleotideSequence().getNumberOfGaps());
            for(Integer gapOffset : placedRead.getNucleotideSequence().getGapOffsets()){
                Range buildRange = Range.buildRange(gapOffset.intValue() + placedRead.getStart());
                gaps.add(buildRange);
            }
            List<Range> mergeRanges = Range.mergeRanges(gaps);
            for(Range mergedRange: mergeRanges ){               
                if(mergedRange.getLength() >=minAbacusLength){
                    gapRangesPerRead.add(mergedRange);
                }
            }
        }
        
        CoverageMap<CoverageRegion<Range>> clusteredGapCoverage = DefaultCoverageMap.buildCoverageMap(gapRangesPerRead);
    
        List<Range> abacusErrors = new ArrayList<Range>();
       
        for(CoverageRegion<Range> gapRegion : clusteredGapCoverage){          
            if(gapRegion.getCoverage() >0){
                abacusErrors.add(gapRegion.asRange());
            }            
        }
        
        List<Range> ungappedCandidateRanges = convertToUngappedRanges(Range.mergeRanges(abacusErrors,clusterDistance), contig.getConsensus());
        return ungappedCandidateRanges;
    }
    
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output file (if not used output printed to STDOUT)")
        .longName("out")
        .build());
        
        options.addOption(new CommandLineOptionBuilder("nav", "path to optional consed navigation file to see abacus errors easier in consed")
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            final DataStoreFilter filter = getDataStoreFilter(commandLine);
            final PrintWriter out = getOutputWriter(commandLine);
            final ConsedNavigationWriter consedNavWriter;
           
            File aceFile = new File(commandLine.getOptionValue("a"));
            if(commandLine.hasOption("nav")){
                consedNavWriter = new ConsedNavigationWriter("Abacus errors for " + aceFile.getName(), new FileOutputStream(commandLine.getOptionValue("nav")));
            }else{
                consedNavWriter =null;
            }
            final AbacusErrorFinder abacusErrorFinder = new AbacusErrorFinder(5,3);
            try{
                AceContigDataStore datastore = IndexedAceFileDataStore.create(aceFile);
                Iterator<String> contigIds = datastore.getIds();
                while(contigIds.hasNext()){
                    String contigId = contigIds.next();
                    if(filter.accept(contigId)){
                        try {
                            findErrorsIn(abacusErrorFinder, datastore.get(contigId), out,consedNavWriter);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }             
                }
            }finally{
                IOUtil.closeAndIgnoreErrors(out,consedNavWriter);
            }
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
        
       
    }

    /**
     * @param commandLine
     * @return
     * @throws FileNotFoundException 
     */
    private static PrintWriter getOutputWriter(CommandLine commandLine) throws FileNotFoundException {
        if(commandLine.hasOption("o")){
            return new PrintWriter(new File(commandLine.getOptionValue("o")));
        }
        return new PrintWriter(System.out);
    }

    private static DataStoreFilter getDataStoreFilter(CommandLine commandLine)
            throws IdReaderException {
        final DataStoreFilter filter;
        File idFile;
        if(commandLine.hasOption("i")){
            idFile =new File(commandLine.getOptionValue("i"));
            Set<String> includeList=parseIdsFrom(idFile);
            if(commandLine.hasOption("e")){
                Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                includeList.removeAll(excludeList);
            }
            filter = new DefaultIncludeDataStoreFilter(includeList);
            
        }else if(commandLine.hasOption("e")){
            idFile =new File(commandLine.getOptionValue("e"));
            filter = new DefaultExcludeDataStoreFilter(parseIdsFrom(idFile));
        }else{
            filter = EmptyDataStoreFilter.INSTANCE;
        }
        return filter;
    }

    private static void findErrorsIn(AbacusErrorFinder abacusErrorFinder,
            AceContig contig, PrintWriter out,ConsedNavigationWriter consedNavigationWriter) throws IOException {
        String contigId=contig.getId();
        out.println(contig.getId());
        List<Range> errorRanges = abacusErrorFinder.findAbacusErrors(contig);
       
        out.println("found "+ errorRanges.size() + " abacus errors");
        for(Range errorRange : errorRanges){
            Range residueBasedRange = errorRange.convertRange(CoordinateSystem.RESIDUE_BASED);
            if(consedNavigationWriter !=null){
                consedNavigationWriter.writeNavigationElement(new ConsensusNavigationElement(contigId, errorRange, "CA abacus error"));
            }
            out.printf("abacus error range : %s%n", residueBasedRange);
            
        }
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "findAbacusErrors -a <ace file>", 
                
                "Parse an ace file and write out ungapped consensus coordinates of abacus assembly errors",
                options,
               "Created by Danny Katzel"
                  );
    }
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }

}
