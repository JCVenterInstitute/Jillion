/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import static org.jcvi.jillion.core.Direction.FORWARD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public final class TestSliceUtil {
    private TestSliceUtil(){}
    
    public static Slice createIsolatedSliceFrom(String bases, int... qualities){
    	return createIsolatedSliceFrom(null, bases, qualities);
    }
    public static Slice createIsolatedSliceFrom(String consensus, String bases, int... qualities){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        if(consensus !=null){
        	builder.setConsensus(Nucleotide.parse(consensus));
        }
        for(int i =0; i< qualities.length; i++){
            builder.add("read_"+i,Nucleotide.parse(bases.charAt(i)), PhredQuality.valueOf(qualities[i]), FORWARD);
        }
        return builder.build();
    }
    public static Slice createSliceFrom(List<Nucleotide> nucleotides, byte[] qualities, List<Direction> directions){
        return createSliceFrom(null, nucleotides, qualities, directions);
    }
   
    public static Slice createSliceFrom(String consensus, List<Nucleotide> nucleotides, byte[] qualities, List<Direction> directions){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        if(consensus !=null){
        	builder.setConsensus(Nucleotide.parse(consensus));
        }
        for(int i=0; i<nucleotides.size(); i++){
            builder.add("read_"+i,nucleotides.get(i), PhredQuality.valueOf(qualities[i]), directions.get(i));
        }
        return builder.build();
    }
    public static Slice createSliceFrom(String consensus, String nucleotides, byte[] qualities, Direction... directions){
        return createSliceFrom(consensus, nucleotides, qualities, Arrays.asList(directions));
    }
    public static Slice createSliceFrom(String consensus, String nucleotides, byte[] qualities, List<Direction> directions){
        
        return createSliceFrom(consensus, asList(new NucleotideSequenceBuilder(nucleotides)),
                qualities, directions);
    }
    public static Slice createSliceFrom(String nucleotides, byte[] qualities, List<Direction> directions){
        
        return createSliceFrom(null, nucleotides, qualities, directions);
    }
    private static List<Nucleotide> asList(NucleotideSequenceBuilder builder){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)builder.getLength());
    	for(Nucleotide n : builder){
    		list.add(n);
    	}
    	return list;
    }
    public static List<Slice> createSlicesFrom(List<String> nucleotides, byte[][] qualities, List<Direction> directions){
        List<Slice> slices = new ArrayList<Slice>();
        for(int j=0; j< nucleotides.get(0).length(); j++){
            StringBuilder sliceBases = new StringBuilder();
            byte[] sliceQualities = new byte[nucleotides.size()];
            for(int i=0; i< nucleotides.size(); i++){
                sliceBases.append(nucleotides.get(i).charAt(j));
                sliceQualities[i] = qualities[i][j];                
            }
            slices.add(createSliceFrom(sliceBases.toString(), sliceQualities, directions));
        }
           
        return slices;
    }
}
