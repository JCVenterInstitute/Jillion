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
package org.jcvi.common.core.assembly.ace.consed;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Ranges;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceAssembledReadBuilder;
import org.jcvi.common.core.assembly.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.ace.DefaultPhdInfo;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

/**
 * This class contains utility scripts for
 * converting {@link AceContig} data into
 * data that can work with Consed.
 * @author dkatzel
 *
 *
 */
public final class ConsedUtil {
	
	
    /**
     * 
     */
    private static final String CONTIG_RENAME_TAG_TYPE = "contigName";

    /**
     * Consed rename comment header which tells us what the contig SHOULD 
     * be named instead of the given ID.
     */
    private static final Pattern CONTIG_RENAME_PATTERN = Pattern.compile("U(\\w+)");
    
    private static final Pattern CONSED_ACE_PATTERN = Pattern.compile("((.+?)\\.)?ace(\\.(\\d+))?$");
    
    private static final Pattern CONSED_ACE_VERSION_PATTERN = Pattern.compile("((.+?)\\.)?ace\\.(\\d+)$");
    
    private static final Pattern ACE_CONTIG_ID_PATTERN = Pattern.compile("(\\S+)_(\\d+)_\\d+");
    
    private ConsedUtil(){
		//private constructor
	}
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
    public static PhdInfo generatePhdInfoFor(File traceFile, String readId,
			Date phdDate) {
		final String id;
        if(traceFile ==null){
        	id= readId;
        }else{
            final String extension = FileUtil.getExtension(traceFile.getName());
            if("sff".equals(extension)){        
                id="sff:"+traceFile.getName()+":"+readId;
            }
            else if("scf".equals(extension)){        
                id=traceFile.getName();
            }
            else{
                id= readId;
            }
        }
        return new DefaultPhdInfo(id, readId+".phd.1", phdDate);
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
     * @param contigBuilder an {@link AceContig} that may have 0x regions.  Can not be null.
     * @param adjustIdCoordinates this contig id already has coordinates appended to the end
     * of the id, adjust these coordinates instead of appending new ones...
     * @return a {@link SortedMap} of (possibly new) AceContigs of the broken given contig.
     * The keys of the map are the Ranges into the original contig where these contigs
     * are placed and the values of the  map are the (possibly new) AceContigs.
     * If there are no 0x regions in the given contig, then a Map containing
     * one entry containing the Range covered and the reference of the given contig is returned.
     */
    public static SortedMap<Range,AceContig> split0xContig(AceContigBuilder contigBuilder, boolean adjustIdCoordinates){
        List<Range> coveredRegions = new ArrayList<Range>();
        NucleotideSequence consensus = contigBuilder.getConsensusBuilder().build();
        CoverageMap<AceAssembledReadBuilder> coverageMap = CoverageMapFactory.create(contigBuilder.getAllAssembledReadBuilders());
        for(CoverageRegion<AceAssembledReadBuilder> region : coverageMap){
            if(region.getCoverageDepth()>0){
                
                final Range contigRange =region.asRange();
                coveredRegions.add(contigRange);
            }
        }
        
        List<Range> contigRanges =Ranges.merge(coveredRegions);
        SortedMap<Range, AceContig> map = new TreeMap<Range, AceContig>(Range.Comparators.ARRIVAL);
        if(contigRanges.size()==1){
            //no 0x region        	
        	map.put(AssemblyUtil.toUngappedRange(consensus, contigRanges.get(0)), contigBuilder.build());
            return map;
        }
        String originalContigId= contigBuilder.getContigId();
        int oldStart=1;
        if(adjustIdCoordinates){
            Matcher matcher = ACE_CONTIG_ID_PATTERN.matcher(originalContigId);
            if(matcher.matches()){
                originalContigId = matcher.group(1);
                oldStart=Integer.parseInt(matcher.group(2));
            }
        }
        for(Range contigRange : contigRanges){
            AceContig splitContig = createSplitContig(contigBuilder,
                    coverageMap, consensus, originalContigId, oldStart,
                    contigRange);
            Range ungappedRange = AssemblyUtil.toUngappedRange(consensus, contigRange);
            map.put(ungappedRange, splitContig);
        }
        return map;
    }
    private static AceContig createSplitContig(AceContigBuilder builderToSplit,
            CoverageMap<AceAssembledReadBuilder> coverageMap,
            NucleotideSequence consensus, String originalContigId,
            int oldStart, Range contigRange) {
        Set<String> contigReads = new HashSet<String>();
        
        for(CoverageRegion<AceAssembledReadBuilder> region : CoverageMapUtil.getRegionsWhichIntersect(coverageMap, contigRange)){
            for(AceAssembledReadBuilder read : region){
                contigReads.add(read.getId());
            }
        }
        NucleotideSequence contigConsensus =new NucleotideSequenceBuilder(consensus)
        							.trim(contigRange)
        							.build();
        //id is now <original_id>_<ungapped 1-based start>_<ungapped 1-based end>
        String contigId = String.format("%s_%d_%d",originalContigId, 
                oldStart + consensus.getUngappedOffsetFor((int) contigRange.getBegin()),
                oldStart + consensus.getUngappedOffsetFor((int) contigRange.getEnd()));
        AceContigBuilder builder = DefaultAceContig.createBuilder(contigId, contigConsensus);
        
        for(String readId : contigReads){
            final AceAssembledReadBuilder read = builderToSplit.getAssembledReadBuilder(readId);
            if(read ==null){
                throw new NullPointerException("got a null read for id " + readId);
            }
            builder.addRead(readId, 
                    read.getNucleotideSequenceBuilder().build(), 
                    (int)(read.getBegin() - contigRange.getBegin()), 
                    read.getDirection(), read.getClearRange(), read.getPhdInfo(),
                    read.getUngappedFullLength());
        }
        return builder.build();
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
        return CONTIG_RENAME_TAG_TYPE.equals(consensusTag.getType());
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
        try{
        for(File file : editDir.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith(filenamePrefix) && CONSED_ACE_PATTERN.matcher(name).find();
            }
        
     })){
           
            int version = getAceVersionFor(file);
            if(version > highestAceFileVersion){
                highestAceFileVersion=version;
                highestAceFile = file;
            }
        }
        return highestAceFile;
        }catch(NullPointerException e){
        	throw e;
        }
    }
    public static File getPhdDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"phd_dir");
    }
	private static void verifyNotNull(File consedDir) {
		if(consedDir==null){
            throw new NullPointerException("consedDir can not be null");
        }
	}
    public static File getEditDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"edit_dir");
    }
    public static File getChromatDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"chromat_dir");
    }
    public static File getPhdBallDirFor(File consedDir){
        verifyNotNull(consedDir);
        return new File(consedDir,"phdball_dir");
    }
    public static int getAceVersionFor(File consedAceFile){
        String name = consedAceFile.getName();
        Matcher matcher = CONSED_ACE_VERSION_PATTERN.matcher(name);
        if(!matcher.matches()){
            throw new IllegalArgumentException("could not parse version from "+ name);
        }
        return Integer.parseInt(matcher.group(3));
    }
    
    public static String generateNextAceVersionNameFor(File consedAceFile){
        String name = consedAceFile.getName();
        Matcher matcher = CONSED_ACE_VERSION_PATTERN.matcher(name);
        if(!matcher.matches()){
            throw new IllegalArgumentException("could not parse version from "+ name);
        }
        String prefix = matcher.group(2);
        int version= Integer.parseInt(matcher.group(3));
        
        return String.format("%sace.%d",
                prefix==null?"": prefix+".", 
                        version+1);
    }
    
    public static enum ClipPointsType{
    	VALID,
    	NEGATIVE_VALID_RANGE,
    	ALL_LOW_QUALITY,
    	NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION
    	;
    	
    	public static ClipPointsType getType(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			if(qualLeft == -1 && qualRight ==-1){
				return ClipPointsType.ALL_LOW_QUALITY;
	        }
	        if((qualRight-qualLeft) <0){
	            //invalid converted ace file? 
	            return ClipPointsType.NEGATIVE_VALID_RANGE;
	        }    
	        
	        //dkatzel 4/2011 - There have been cases when qual coords and align coords
	        //do not match; usually qual is a sub set of align
	        //but occasionally, qual goes beyond the align coords.
	        //I guess this happens in a referenced based alignment for
	        //reads at the edges when the reads have good quality 
	        //beyond the reference.
	        //It might also be possible that the read has been 
	        //edited and that could have changed the coordinates.
	        //Therefore intersect the qual and align coords
	        //to find the region we are interested in
	        Range qualityRange = Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
	        Range alignmentRange = Range.of(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
	        if(qualityRange.intersects(alignmentRange)){
	        	return ClipPointsType.VALID;
	        }else{	        
	        	//no intersection! 
	        	//I've only seen this on really bad quality
	        	//////////////////////////////////////////////////////////
	        	//dkatzel - 2012-01-26
	        	//email response from David Gordon (author of consed)
	        	//regarding what to do if these ranges don't overlap
	        	//From David Gordon:
	        	//the first 2 numbers indicate the high quality segment
	        	//(roughly corresponding to that above quality 13).
	        	//The last 2 numbers indicates the portion of the read aligned
	        	//to the consensus sequence.
	        	//
	        	//Hence there is a very short high quality segment 
	        	//634-649 (only 16 bases).  And the portion of the 
	        	//read aligned to the consensus is 851-1758 is very low quality.
	        	//
	        	//Consed treats these reads like any others.  
	        	//The "dim" menu on the Aligned Reads Window 
	        	//indicates what portion of the read to dim.  
	        	//If you set it on dim both low quality and unaligned, 
	        	//this entire read would be dimmed.
	        	/////////////////////////////////////////////////////////
	        	//dkatzel -therefore if consed dims the entire read
	        	//that's enough justification for me to throw the read out
	        	return ClipPointsType.NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION;
	        }
		}
    }
    
    
}
