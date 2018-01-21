package org.jcvi.jillion.assembly.util;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * Helper class used by {@link SliceMapCollector}.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
final class SliceCombiner {

    private final SliceBuilder[] builders;
    
    
    public SliceCombiner(int size){
        builders = new SliceBuilder[size];
        Arrays.parallelSetAll(builders , i-> new SliceBuilder());
    }
    public void add(String id, int start, NucleotideSequence seq, Iterator<PhredQuality> qualities, Direction dir){
        int i =0;
        for(Nucleotide base : seq){
                PhredQuality quality = qualities.next();
                SliceBuilder builder = builders[start+i];
                synchronized (builder) {
                    builder.addNew(id, base, quality, dir);
                }
                i++;
        }
    }
    
    public SliceCombiner combine(SliceCombiner other){
        for(int i=0; i< builders.length; i++){
            SliceBuilder otherBuilder = other.builders[i];
            if(!otherBuilder.isEmpty()){
                
                SliceBuilder myBuilder = builders[i];
                synchronized (myBuilder) {
                    myBuilder.mergeNew(otherBuilder);
                }
               
            }
        }
        return this;
    }
    
    public synchronized Slice[] toSlices(NucleotideSequence consensus){
        Slice[] ret = new Slice[builders.length];
        int i=0;
        for(Nucleotide n : consensus){
            ret[i] = builders[i].setConsensus(n).build();
            i++;
        }
        return ret;
        
    }
    
    
}
