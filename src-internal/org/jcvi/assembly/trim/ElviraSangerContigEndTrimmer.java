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

package org.jcvi.assembly.trim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.trimmer.MinimumBidirectionalEndCoverageTrimmer;

/**
 * @author dkatzel
 *
 *
 */
public class ElviraSangerContigEndTrimmer<P extends AssembledRead, C extends Contig<P>> extends MinimumBidirectionalEndCoverageTrimmer<P, C>{
    private final int minimumEndCloneCoverage;
    /**
     * @param minimumEndCoverage
     */
    public ElviraSangerContigEndTrimmer(int minimumEndCloneCoverage,int minimumBiDirectionalCoverage, int maxCoverageToConsider) {
        super(minimumBiDirectionalCoverage,maxCoverageToConsider);
        this.minimumEndCloneCoverage = minimumEndCloneCoverage;
    }
    private static Pattern templatePattern =Pattern.compile("^.{4}(.)(\\d{2})([A-Z]\\d{2})([A-HXZ])(\\d{2}).*");
    
    private static Pattern CLOSURE_READ_NAME_PATTERN = Pattern.compile("^.{4}(.)(\\d{2})T00(\\S+?)([F|R])M?([A-Z]?)$");
    
    public static Map<String, Set<String>> binByClone(Collection<String> seqnames){
        Map<String, Set<String>> bins = new HashMap<String, Set<String>>();
        for(String seqname : seqnames){
            String cloneName= seqname.substring(0, 13);
            if(!bins.containsKey(cloneName)){
                bins.put(cloneName, new HashSet<String>());
            }
            bins.get(cloneName).add(seqname);
            
        }
        return bins;
    }
    
    /**
     * Does the given sequenceName match the Elvira
     * Sanger Sequence name pattern.
     * @param sequenceName the sequence name to check.
     * @return {@code true} if the read does match
     * the Elvira SequenceRead name pattern; {@code false} 
     * otherwise.
     */
    public static boolean isElviraSangerRead(String sequenceName){
        final Matcher matcher = templatePattern.matcher(sequenceName);
        return matcher.matches() || isElviraClosureRead(sequenceName);
    }
    private static boolean isElviraClosureRead(String sequenceName){
        return CLOSURE_READ_NAME_PATTERN.matcher(sequenceName).matches();
    }

    @Override
    protected boolean meetsTrimmingRequirements(CoverageRegion<P> region) {
        if(super.meetsTrimmingRequirements(region)){
            return true;
        }
        //only here if we don't meet requirements of bidirectional trimming...
        List<String> seqnames = new ArrayList<String>(region.getCoverage());
        for(AssembledRead read : region){
            if(isElviraSangerRead(read.getId())){
                seqnames.add(read.getId());
            }
        }
        final Map<String, Set<String>> clones = binByClone(seqnames);
        return clones.size()>= minimumEndCloneCoverage;
    }
    
    
    
}
