package org.jcvi.jillion.assembly.util.consensus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;


class ConsensusCombiner {
    List<SliceBuilder> builders = new ArrayList<>();
    
    public void add(SliceElement[] seq){
       add(seq, 0);
    }
    public void add(SliceElement[] seq, int startOffset){
        ensureSize(startOffset+ seq.length);
        for(int i=startOffset; i< seq.length; i++){
            SliceElement element = seq[i];
            if(element !=null){
                builders.get(i).add(element);
            }
        }
    }
    
    public ConsensusCombiner merge(ConsensusCombiner other){
        ensureSize(other.builders.size());
        int currentSize = builders.size();
        for(int i=0; i< currentSize; i++){
            builders.get(i).addAll(other.builders.get(i));
            
        }
        return this;
    }

    private void ensureSize(int length) {
        int delta = length - builders.size();
       for(int i=0; i< delta; i++){
           builders.add(new SliceBuilder());
       }
        
    }
    
    public NucleotideSequenceBuilder computeConsensus(ConsensusCaller c){
        NucleotideSequenceBuilder consensusBuilder = new NucleotideSequenceBuilder(builders.size());
        for(SliceBuilder b : builders){
            consensusBuilder.append(c.callConsensus(b.build()).getConsensus());
        }
        return consensusBuilder;
    }

}
