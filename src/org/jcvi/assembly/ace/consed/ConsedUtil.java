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
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.ConsensusAceTag;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * This class contains utility scripts for
 * converting {@link AceContig} data into
 * data that can work with Consed.
 * @author dkatzel
 *
 *
 */
public class ConsedUtil {
    /**
     * Consed rename comment header which tells us what the contig SHOULD 
     * be named instead of the given ID.
     */
    private static final Pattern CONTIG_RENAME_PATTERN = Pattern.compile("U(\\w+)");
    /**
     * Convert a string of basecalls with '*' to 
     * represent gaps (which is what consed uses) with '-' instead. 
     * @param basecallsWithAceGaps a string of basecalls with the '*' to 
     * represent gaps.
     * @return a new string with all the '*' converted into '-'.
     * @see #convertContigGapstoAceGaps(String)
     */
    public static String convertAceGapsToContigGaps(String basecallsWithAceGaps) {
        return basecallsWithAceGaps.replace('*', '-');
    }
    /**
     * Convert a string of basecalls with the conventional '-' to 
     * represent gaps with '*' which is what consed uses instead. 
     * @param basecallsWithAceGaps a string of basecalls with the conventional '-' to 
     * represent gaps.
     * @return a new string with all the '-' converted into '*'.
     * @see #convertAceGapsToContigGaps(String)
     */
    public static String convertContigGapstoAceGaps(String basecallsWithAceGaps) {
        return basecallsWithAceGaps.replace('-', '*');
    }
    /**
     * Split a contig which may contain zero coverage areas (0x)
     * into multiple contigs which all have at least some coverage at every
     * location.  If the given contig is split, the new contigs will be named
     * {@code <original_id>_<ungapped reference 1-based start>_<ungapped reference 1-based end>}
     * <p/>
     * Some Assemblers (mostly reference assemblers) create contigs with zero coverage
     * regions (0x) but that have the reference basecalls as the consensus in those 
     * areas. This method removes the parts of the contig which only have consensus. 
     * @param contig an {@link AceContig} that may have 0x regions.  Can not be null.
     * @param coverageMap the coverage map that corresponds to the given contig.
     * @return a list of (possibly new) AceContigs of the broken given contig.  
     * If there are no 0x regions in the given contig, then a list containing
     * only the reference of the given contig is returned.
     */
    public static List<AceContig> split0xContig(AceContig contig, CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap){
        List<Range> coveredRegions = new ArrayList<Range>();
        for(CoverageRegion region : coverageMap){
            if(region.getCoverage()>0){
                final Range contigRange = Range.buildRange(region.getStart(), region.getEnd())
                                            .convertRange(CoordinateSystem.RESIDUE_BASED);
                coveredRegions.add(contigRange);
            }
        }
        
        List<Range> contigRanges =Range.mergeRanges(coveredRegions);
        if(contigRanges.size()==1){
            //no 0x region
            return Arrays.asList(contig);
        }
        List<AceContig> newContigs = new ArrayList<AceContig>(contigRanges.size());
        String originalContigId= contig.getId();
        NucleotideEncodedGlyphs consensus = contig.getConsensus();
        for(Range contigRange : contigRanges){
            Set<String> contigReads = new HashSet<String>();
            
            for(CoverageRegion<AcePlacedRead> region : coverageMap.getRegionsWithin(contigRange)){
                for(AcePlacedRead read : region.getElements()){
                    contigReads.add(read.getId());
                }
            }
            String contigConsensus =NucleotideGlyph.convertToString(consensus.decode(contigRange));
            //id is now <original_id>_<ungapped 1-based start>_<ungapped 1-based end>
            String contigId = String.format("%s_%d_%d",originalContigId, 
                    consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int) contigRange.getStart())+1,
                    consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int) contigRange.getEnd())+1);
            DefaultAceContig.Builder builder = new DefaultAceContig.Builder(contigId, contigConsensus);
            
            for(String readId : contigReads){
                final VirtualPlacedRead<AcePlacedRead> placedReadById = contig.getPlacedReadById(readId);
                if(placedReadById ==null){
                    throw new NullPointerException("got a null read for id " + readId);
                }
                AcePlacedRead read = placedReadById.getRealPlacedRead();
                //Range readRange = Range.buildRange(read.getStart(), read.getEnd()).union(target);
                builder.addRead(readId, 
                        NucleotideGlyph.convertToString(read.getEncodedGlyphs().decode()), 
                        (int)(read.getStart() - contigRange.getStart()), 
                        read.getSequenceDirection(), read.getValidRange(), read.getPhdInfo());
            }
            newContigs.add(builder.build());
        }
        return newContigs;
    }
    /**
     * Checks to see if the given {@link ConsensusAceTag} is denotes
     * that the contig has been renamed.
     * @param consensusTag the tag to check.
     * @return {@code true} if this tag denotes a contig rename; {@code false}
     * otherwise.
     * @throw {@link NullPointerException} if consensusTag is null.
     */
    public static boolean isContigRename(ConsensusAceTag consensusTag){
        return consensusTag.getType().equals("contigName");
    }
    /**
     * Get the new name this contig should be named according to the given
     * rename tag.
     * @param contigRenameTag a {@link ConsensusAceTag} that denotes
     * the contig has been renamed.
     * @return the new name that the contig should be renamed to.
     * @throws NullPointerException if contigRenameTag is null.
     * @throws IllegalArgumentException if the given tag is not a contig rename
     * tag or if the tag text does not match the known pattern for 
     * contig renames.
     */
    public static String getRenamedContigId(ConsensusAceTag contigRenameTag){
        if(!isContigRename(contigRenameTag)){
            throw new IllegalArgumentException("not a contig rename");
        }
        String data= contigRenameTag.getData();
        Matcher matcher = CONTIG_RENAME_PATTERN.matcher(data);
        if(matcher.find()){
            return matcher.group(1);
        }
        throw new IllegalArgumentException("consensus tag does not contain rename info : "+contigRenameTag);
    }
    /**
     * Gets the latest ace file with the given prefix in the given edit_dir.
     * 
     *<p/>Consed labels each version of the ace file with a incrementing
     *value so {@code prefix.ace.2} is newer than {@code prefix.ace.1}.
     * @param editDir the consed edit_dir folder to inspect.
     * @param filenamePrefix the beginning part of the file name to filter,
     * incase there are more than 1 groups of versioned assemblies.
     * @return the File object representing the latest version of the ace file
     * with the given prefix in the given edit_dir; {@code null}
     * if no such file exists.
     */
    public static File getLatestAceFile(File editDir, final String filenamePrefix){
        int highestAceFileVersion=Integer.MIN_VALUE;
        File highestAceFile=null;
        for(File file : editDir.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith(filenamePrefix +".ace") && !name.endsWith("wrk");
            }
        
     })){
            String name = file.getName();
            int version = Integer.parseInt(""+name.charAt(name.length()-1));
            if(version > highestAceFileVersion){
                highestAceFileVersion=version;
                highestAceFile = file;
            }
        }
        return highestAceFile;
    }
}
