/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.assembly.util.consensus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
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
    
    public void setReferenceSequence(NucleotideSequence consensus){
        Iterator<Nucleotide> refIter = consensus.iterator();
        Iterator<SliceBuilder> sliceIter = builders.iterator();
        while(refIter.hasNext() && sliceIter.hasNext()){
            sliceIter.next().setConsensus(refIter.next());
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
