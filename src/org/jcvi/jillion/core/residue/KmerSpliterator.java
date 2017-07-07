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
package org.jcvi.jillion.core.residue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterators;
import java.util.function.Consumer;

import org.jcvi.jillion.core.Range;
/**
 * Internal class that uses a {@link java.util.Spliterator} to make
 * {@link java.util.stream.Stream}s of {@link Kmer}s.
 * @author dkatzel
 *
 * @param <R>
 * @param <S>
 * @param <B>
 * 
 * @since 5.3
 */
class KmerSpliterator<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S>> extends Spliterators.AbstractSpliterator<Kmer<S>> {
    private LinkedList<ResidueSequenceBuilder<R, S>> builders = new LinkedList<>();
    private long counter;
    private final Iterator<R> iter;
    
    private final int k;
    private final S sequence;
    public KmerSpliterator(int k, S residueSequence, Range range){
        super(residueSequence.getLength() - k +1, 0);
        this.k = k;
        counter=range.getBegin();
        this.sequence = residueSequence;
        iter = residueSequence.iterator(range);
        
        if(residueSequence.getLength() >=k){
            for(int i=0; i< k; i++){
                addNextBase();
            }
        }
        
    }
    
   
        
        
        private void addNextBase(){
            if(iter.hasNext()){
                R n = iter.next();
               
                builders.addLast(sequence.newEmptyBuilder(k).turnOffDataCompression(true));
                
                builders.forEach( b-> b.append(n));
            }else if(!builders.isEmpty()){
                //no more left remove all except the first if it's long enough
                ResidueSequenceBuilder<R, S> head = builders.pop();
                builders.clear();
                if(head.getLength() == k){
                    builders.add(head);
                }
                
            }
        }
        @Override
        public boolean tryAdvance(Consumer<? super Kmer<S>> action) {
            if(builders.isEmpty()){
                return false;
            }
            action.accept( new Kmer<>(counter++, builders.pop().build()));
            addNextBase();
            return !builders.isEmpty();
        }
    
}
