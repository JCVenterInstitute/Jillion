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
 * Created on Oct 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.contig.DefaultContigQualityClassComputer;
import org.jcvi.assembly.contig.DefaultQualityClassContigMap;
import org.jcvi.assembly.contig.QualityClassComputer;
import org.jcvi.assembly.contig.QualityClassMap;
import org.jcvi.assembly.contig.QualityClassRegion;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassContigTrimmer{

    private final int maxNumberOf5PrimeBasesToTrim;
    private final int maxNumberOf3PrimeBasesToTrim;
    private final Set<QualityClass> qualityClassesToTrim;

    private class RangeableIterable implements Iterable<Long>{

        private final Rangeable placed;
        public RangeableIterable(Rangeable placed){
           this.placed = placed;
        }
        
        @Override
        public Iterator<Long> iterator() {
            return  placed.asRange().iterator();
        }

    }
    
    /**
     * 
     * @param maxNumberOf5PrimeBasesToTrim max number of bases to trim off
     * of the 5' side (residue based)
     * @param maxNumberOf3PrimeBasesToTrim max number of bases to trim off
     * of the 3' side (residue based)
     * @param qualityClassesToTrim Set of Quality classes to consider
     * trimming
     */
    public QualityClassContigTrimmer(int maxNumberOf5PrimeBasesToTrim,
            int maxNumberOf3PrimeBasesToTrim,
            Set<QualityClass> qualityClassesToTrim) {
        this.maxNumberOf5PrimeBasesToTrim = maxNumberOf5PrimeBasesToTrim;
        this.maxNumberOf3PrimeBasesToTrim = maxNumberOf3PrimeBasesToTrim;
        this.qualityClassesToTrim = qualityClassesToTrim;
    }


    public <R extends AssembledRead,C extends Contig<R>> List<TrimmedPlacedRead<R>> trim(C contig,QualitySequenceDataStore qualityDataStore, 
            QualityClassComputer qualityClassComputer) throws DataStoreException {

        Map<R, Range> trimmedReads = new HashMap<R, Range>();
        CoverageMap<R> coverageMap =CoverageMapFactory.createGappedCoverageMapFromContig(contig);
        QualityClassMap qualityClassContigMap =DefaultQualityClassContigMap.create(contig, qualityDataStore, qualityClassComputer);
        
       
        for (QualityClassRegion qualityClassRegion : qualityClassContigMap) {
            if (isAQualityClassToTrim(qualityClassRegion.getQualityClass())) {
                for(Long consensusIndex : new RangeableIterable(qualityClassRegion)){
                    CoverageRegion<R> coverageRegion = CoverageMapUtil.getRegionWhichCovers(coverageMap, consensusIndex);

                    for (R read : coverageRegion) {
                        int gappedValidRangeIndex = (int)read.toGappedValidRangeOffset(consensusIndex);
                        if (isASnp(read, gappedValidRangeIndex)){  
                            Range oldValidRange = getPreviousValidRange(trimmedReads, read);
                            Range newValidRange = computeNewValidRange(read,gappedValidRangeIndex);
                            if (newValidRange != null ) {
                                trimmedReads.put(read, newValidRange.intersection(oldValidRange));
                            }

                        }

                    }
                }
            }
        }
        List<TrimmedPlacedRead<R>> trims = new ArrayList<TrimmedPlacedRead<R>>();
        for (Entry<R, Range> entry : trimmedReads.entrySet()) {
            trims.add(new DefaultTrimmedPlacedRead<R>(
                    entry.getKey(), 
                    entry.getValue()));
        }
        return trims;
    }

    private <R extends AssembledRead> boolean isASnp(R read, int gappedValidRangeIndex) {
        Map<Integer, Nucleotide> differenceMap = read.getNucleotideSequence().getDifferenceMap();
		Integer key = Integer.valueOf(gappedValidRangeIndex);
		Nucleotide base = differenceMap.get(key);
		//Nadia says the golden rule of editing is
		//"if the end of a sequence disagrees with the middle of another sequence,
		//the end gets trimmed".
		//that would appear to mean consider even if the 
		//consensus is a gap so consider all differences regardless of
		//what base it is or what the consensus is.
		return base !=null;
    }

    private boolean isAQualityClassToTrim(QualityClass qualityClass) {
        return qualityClassesToTrim.contains(qualityClass);
    }

    protected<R extends AssembledRead> Range computeNewValidRange(R read, int offsetToBeTrimmedOff) {
    	ReadInfo readInfo = read.getReadInfo();
		Range oldValidRange = readInfo.getValidRange();
        int gappedOffsetToKeep = computeFlankingNonGappedOffsetToKeep(read,offsetToBeTrimmedOff);
     
        int fullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(read, gappedOffsetToKeep);

        long fullLength = readInfo.getUngappedFullLength();
        
        Range validRange;
        if(read.getDirection()==Direction.FORWARD){
        	validRange = oldValidRange;
        }else{
        	//easier if we pretend everything is forward
        	//we will switch it back after calculations are done
        	validRange = AssemblyUtil.reverseComplementValidRange(oldValidRange, readInfo.getUngappedFullLength());
        }
        
        long midPointOfRead = read.getNucleotideSequence().getLength()/2;
        if(offsetToBeTrimmedOff>midPointOfRead){
        	//is 3'
        	long totalNumberOfBasesTrimmedFromEndOfFullLength = fullLength-fullRangeIndex;
        	if(totalNumberOfBasesTrimmedFromEndOfFullLength<=maxNumberOf3PrimeBasesToTrim){
        		//OK to trim
        		int ungappedOffsetToKeep = read.getNucleotideSequence().getUngappedOffsetFor(gappedOffsetToKeep);
        		long numberOfBasesToTrim = read.getNucleotideSequence().getUngappedLength()-1 -ungappedOffsetToKeep;
    			validRange= validRange.shrink(0,numberOfBasesToTrim);
    			if(read.getDirection()==Direction.REVERSE){
    				return AssemblyUtil.reverseComplementValidRange(validRange, readInfo.getUngappedFullLength());
    			}
    			return validRange;
        	}
        }else{
        	//is 5'
        	if(fullRangeIndex <= maxNumberOf5PrimeBasesToTrim){
        		//OK to trim
        		long numberOfBasesToTrim = read.getNucleotideSequence().getUngappedOffsetFor(gappedOffsetToKeep);
    			validRange=validRange.shrink(numberOfBasesToTrim, 0);
    			if(read.getDirection()==Direction.REVERSE){
    				return AssemblyUtil.reverseComplementValidRange(validRange, readInfo.getUngappedFullLength());
    			}
    			return validRange;
        		}
        	}
    
        return null;
       
    }

    private <R extends AssembledRead> Range getPreviousValidRange(
            Map<R, Range> trimmedReads,
            R read) {
        Range oldValidRange = read.getReadInfo().getValidRange();

        if (trimmedReads.containsKey(read)) {
            oldValidRange = trimmedReads.get(read);
        }
        return oldValidRange;
    }

    private <R extends AssembledRead> int computeFlankingNonGappedOffsetToKeep(R read,
            int gappedValidRangeOffsetToBeTrimmedOff) {
        final NucleotideSequence sequence = read.getNucleotideSequence();
        long midPoint = sequence.getLength()/2;
       
        if(gappedValidRangeOffsetToBeTrimmedOff> midPoint) {
        	//a forward sequence
            return AssemblyUtil.getLeftFlankingNonGapIndex(sequence,
                    gappedValidRangeOffsetToBeTrimmedOff-1);
        }
        return AssemblyUtil.getRightFlankingNonGapIndex(sequence,
                    gappedValidRangeOffsetToBeTrimmedOff+1);
    }

    public static void main(String[] args) throws IOException, DataStoreException {
            Options options = new Options();
            options.addOption(new CommandLineOptionBuilder("c", "contig file to examine")
                                .longName("contig_file")
                                .isRequired(true)
                                .build());
            options.addOption(new CommandLineOptionBuilder("q", "quality fasta file (full range)")
                                .longName("qual_file")
                                .isRequired(true)
                                .build());
            options.addOption(new CommandLineOptionBuilder("f", ".seq.features file current containing trim points")
                                .longName("feat_file")
                                .isRequired(true)
                                .build());
            options.addOption(new CommandLineOptionBuilder("max5", "max bases to trim off the 5 prime side (ungapped)")
                                    .build());
            options.addOption(new CommandLineOptionBuilder("max3", "max bases to trim off the 3 prime side (ungapped)")
                                    .build());
            options.addOption(new CommandLineOptionBuilder("quality_classes", "comma sep list of quality classes to try to trim")
                                            .build());
            options.addOption(new CommandLineOptionBuilder("high_qual_threshold", "quality value which is considered to be high quality")
                                        .build());
            try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            File contigFile = new File(commandLine.getOptionValue("c"));
            File qualFastaFile = new File(commandLine.getOptionValue("q"));
            File trimFile = new File(commandLine.getOptionValue("f"));
            
            final int fivePrimeMaxBasesToTrim = commandLine.hasOption("max5")? Integer.parseInt(commandLine.getOptionValue("max5")):50;
            int threePrimeMaxBasesToTrim = commandLine.hasOption("max3")? Integer.parseInt(commandLine.getOptionValue("max3")):15;
            String commaSepQualityClassesToTrim = commandLine.hasOption("quality_classes")?commandLine.getOptionValue("quality_classes"): "15,16,17";
            
            PhredQuality highQualityThreshold = commandLine.hasOption("high_qual_threshold")?
                                PhredQuality.valueOf(Integer.parseInt(commandLine.getOptionValue("high_qual_threshold"))) :
                                    PhredQuality.valueOf(30);
                                
            Set<QualityClass> qualityClassesToTrim = EnumSet.noneOf(QualityClass.class);
            
            
            for(String qualityClassAsString : commaSepQualityClassesToTrim.split(",")){
                qualityClassesToTrim.add(QualityClass.valueOf(Byte.parseByte(qualityClassAsString)));
            }
            ReadTrimMap trimMap = ReadTrimUtil.readReadTrimsFromFile(trimFile);
            ContigDataStore<AssembledRead, Contig<AssembledRead>> contigDataStore = new DefaultContigFileDataStore(
                    contigFile);
            QualitySequenceDataStore qualityFastaMap = 
                CachedDataStore.create(QualitySequenceDataStore.class, 
                		FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class,new LargeQualityFastaFileDataStore(qualFastaFile)),
                        100);
            StreamingIterator<Contig<AssembledRead>> iter = contigDataStore.iterator();
            try{
	            while(iter.hasNext()) {
	            	Contig<AssembledRead> contig = iter.next();
	                QualityClassContigTrimmer trimmer = new QualityClassContigTrimmer(
	                        fivePrimeMaxBasesToTrim, threePrimeMaxBasesToTrim, qualityClassesToTrim);
	
	                List<TrimmedPlacedRead<AssembledRead>> trims = trimmer
	                        .trim(contig,qualityFastaMap, new DefaultContigQualityClassComputer(
	                                GapQualityValueStrategies.LOWEST_FLANKING, highQualityThreshold));
	              
	                SortedSet<TrimmedPlacedRead<AssembledRead>> allChangedReads = new TreeSet<TrimmedPlacedRead<AssembledRead>>();
	                allChangedReads.addAll(trims);
	                for (TrimmedPlacedRead<AssembledRead> trim : allChangedReads) {
	                    Range newtrimmedRange = trim.getNewTrimRange();
	                    Range oldTrimmedRange = trim.getRead().getReadInfo().getValidRange();
	                    String readId = trim.getRead().getId();
	                    //old trim right will always be >= new right
	                    long rightDelta = oldTrimmedRange.getEnd()- newtrimmedRange.getEnd();
	                    long displayRight;
	                    if (rightDelta == 0) {
	                        displayRight = trimMap.getReadTrimFor(readId)
	                                .getTrimRange(TrimType.CLB)
	                                .getEnd(CoordinateSystem.RESIDUE_BASED);
	                    } else {
	                        displayRight = newtrimmedRange.getEnd(CoordinateSystem.RESIDUE_BASED);
	                    }
	                    System.out.printf("%s\t%d\t%d\t%d\t%d\t%s%n",
	                            readId, newtrimmedRange.getBegin(CoordinateSystem.RESIDUE_BASED), displayRight,
	                            newtrimmedRange.getBegin()
	                                    - oldTrimmedRange.getBegin(), rightDelta,
	                                    trim.getRead().asRange());
	                }
	                
	            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            }
            }catch(ParseException e){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( QualityClassContigTrimmer.class.getName(), options );
                System.exit(1);
            }
    }

}
