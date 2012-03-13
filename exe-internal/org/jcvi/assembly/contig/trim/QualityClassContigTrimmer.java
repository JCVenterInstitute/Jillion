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
import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaRecordDataStoreAdapter;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassContigTrimmer<R extends PlacedRead,C extends Contig<R>>{

    private final int maxNumberOf5PrimeBasesToTrim;
    private final int maxNumberOf3PrimeBasesToTrim;
    private final Set<QualityClass> qualityClassesToTrim;

    private class PlacedIterable implements Iterable<Long>{

        private final Placed placed;
        public PlacedIterable(Placed placed){
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


    public List<TrimmedPlacedRead<R>> trim(C struct,QualityDataStore qualityDataStore, 
            QualityClassComputer<R> qualityClassComputer) throws DataStoreException {

        Map<R, Range> trimmedReads = new HashMap<R, Range>();
        CoverageMap<CoverageRegion<R>> coverageMap =DefaultCoverageMap.buildCoverageMap(struct);
        QualityClassMap qualityClassContigMap =DefaultQualityClassContigMap.create(struct, qualityDataStore, qualityClassComputer);
        
       
        for (QualityClassRegion qualityClassRegion : qualityClassContigMap) {
            if (isAQualityClassToTrim(qualityClassRegion.getQualityClass())) {
                for(Long consensusIndex : new PlacedIterable(qualityClassRegion)){
                    CoverageRegion<R> coverageRegion = coverageMap.getRegionWhichCovers(consensusIndex);

                    for (R read : coverageRegion) {
                        int gappedValidRangeIndex = (int)read.toGappedValidRangeOffset(consensusIndex);
                        if (isASnp(read, gappedValidRangeIndex)){                           

                            Range oldValidRange = getPreviousValidRange(trimmedReads, read);
                            Range newValidRange = computeNewValidRange(qualityDataStore, read,oldValidRange,gappedValidRangeIndex);
                            if (newValidRange != null) {
                                trimmedReads.put(read, newValidRange);
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

    private boolean isASnp(R read, int gappedValidRangeIndex) {
        return read.getSnps().containsKey(
                Integer.valueOf(gappedValidRangeIndex));
    }

    private boolean isAQualityClassToTrim(QualityClass qualityClass) {
        return qualityClassesToTrim.contains(qualityClass);
    }

    private Range computeNewValidRange(QualityDataStore qualityMap, R read, Range oldValidRange,int gappedValidRangeIndex) throws DataStoreException {
        final Sequence<PhredQuality> qualityValues = qualityMap.get(read.getId());

        int gappedTrimIndex = computeGappedTrimIndex(read,gappedValidRangeIndex);
     
        int fullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(read,(int)qualityValues.getLength(), gappedTrimIndex);
        // need to +1 passed fullRangeIndex to trim off snp for non-gaps
        if(!read.getNucleotideSequence().isGap(gappedValidRangeIndex)){
            fullRangeIndex ++;
        }
        long fullLength = qualityValues.getLength();
        
        Range newValidRange = null;
        if (fullRangeIndex < maxNumberOf5PrimeBasesToTrim) {
            // 5 prime
            newValidRange = Range.buildRange(fullRangeIndex, 
                    oldValidRange.getEnd());
        } else if (fullLength - fullRangeIndex < maxNumberOf3PrimeBasesToTrim) {
            // 3 prime
            newValidRange = Range.buildRange(oldValidRange.getEnd(),
                    fullRangeIndex);
        }
        return newValidRange;
    }

    private Range getPreviousValidRange(
            Map<R, Range> trimmedReads,
            R read) {
        Range oldValidRange = read.getValidRange();

        if (trimmedReads.containsKey(read)) {
            oldValidRange = trimmedReads.get(read);
        }
        return oldValidRange;
    }

    private int computeGappedTrimIndex(R read,
            int gappedValidRangeIndex) {
        int gappedTrimIndex;
        final NucleotideSequence encodedGlyphs = read.getNucleotideSequence();
        if (read.getDirection() == Direction.FORWARD) {
            gappedTrimIndex = AssemblyUtil.getRightFlankingNonGapIndex(encodedGlyphs,
                    gappedValidRangeIndex);
        } else {
            gappedTrimIndex = AssemblyUtil.getLeftFlankingNonGapIndex(encodedGlyphs,
                    gappedValidRangeIndex);
        }
        return gappedTrimIndex;
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
            ContigDataStore<PlacedRead, Contig<PlacedRead>> contigDataStore = new DefaultContigFileDataStore(
                    contigFile);
            QualityDataStore qualityFastaMap = 
                CachedDataStore.create(QualityDataStore.class, 
                        QualityFastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(qualFastaFile)),
                        100);
            CloseableIterator<Contig<PlacedRead>> iter = contigDataStore.iterator();
            try{
	            while(iter.hasNext()) {
	            	Contig<PlacedRead> contig = iter.next();
	                QualityClassContigTrimmer trimmer = new QualityClassContigTrimmer(
	                        fivePrimeMaxBasesToTrim, threePrimeMaxBasesToTrim, qualityClassesToTrim);
	
	                List<TrimmedPlacedRead<PlacedRead>> trims = trimmer
	                        .trim(contig,qualityFastaMap, new DefaultContigQualityClassComputer<PlacedRead>(
	                                GapQualityValueStrategies.LOWEST_FLANKING, highQualityThreshold));
	              
	                List<TrimmedPlacedRead<PlacedRead>> allChangedReads = new ArrayList<TrimmedPlacedRead<PlacedRead>>();
	                allChangedReads.addAll(trims);
	                for (TrimmedPlacedRead<PlacedRead> trim : allChangedReads) {
	                    // force it to be residue based
	                    Range newtrimmedRange = trim.getNewTrimRange();
	                    Range oldTrimmedRange = trim.getRead().getValidRange();
	                    String readId = trim.getRead().getId();
	                    long rightDelta = newtrimmedRange.getEnd(CoordinateSystem.RESIDUE_BASED) - oldTrimmedRange.getEnd(CoordinateSystem.RESIDUE_BASED);
	                    long displayRight;
	                    if (rightDelta == 0) {
	                        displayRight = trimMap.getReadTrimFor(readId)
	                                .getTrimRange(TrimType.CLB)
	                                .getEnd(CoordinateSystem.RESIDUE_BASED);
	                    } else {
	                        displayRight = newtrimmedRange.getEnd(CoordinateSystem.RESIDUE_BASED);
	                    }
	                    System.out.println(String.format("%s\t%d\t%d\t%d\t%d",
	                            readId, newtrimmedRange.getStart(CoordinateSystem.RESIDUE_BASED), displayRight,
	                            newtrimmedRange.getStart(CoordinateSystem.RESIDUE_BASED)
	                                    - oldTrimmedRange.getStart(CoordinateSystem.RESIDUE_BASED), rightDelta));
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
