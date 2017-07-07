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
package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.Builder;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.Trace;

public class TrimmerPipeline {

    private static final long NOT_SET =1;
    
    private static Range EMPTY = Range.ofLength(0);
    private static Range.Builder EMPTY_BUILDER = new Range.Builder(0);
    
    private final List<NucleotideTrimmer> nucleotideTrimmers;
    private final List<QualityTrimmer> qualityTrimmers;
    
    private final Predicate<Rangeable> rangePredicate;
    private final Predicate<NucleotideSequence> seqPredicate;
    private final Predicate<QualitySequence> qualityPredicate;
    private final long minLength;
        
    TrimmerPipeline(TrimmerPipelineBuilder builder) {
        this.nucleotideTrimmers = new ArrayList<>(builder.nucleotideTrimmers);
        this.qualityTrimmers = new ArrayList<>(builder.qualityTrimmers);
        
        this.rangePredicate = builder.rangePredicate;
        this.seqPredicate = builder.seqPredicate;
        this.qualityPredicate = builder.qualityPredicate;      
        
        this.minLength = builder.minLength;
        
    }



    public Range trim(Trace trace){
        Range.Builder range;
        if(nucleotideTrimmers.isEmpty()){
           return trim(trace.getQualitySequence());
        }else{           
            range = nucTrim(trace.getNucleotideSequence());  
        }
        
        if(!qualityTrimmers.isEmpty()){                     
            range.intersect(qualTrim(trace.getQualitySequence()));  
        }
        
       
        if(minLength !=NOT_SET && range.getLength() < minLength){
            return EMPTY;
        }
        
        if(rangePredicate !=null && rangePredicate.test(range)){
            //short circuit
            return EMPTY;
        }
        Range builtRange = range.build();
        
        if(seqPredicate !=null && seqPredicate.test(
                new NucleotideSequenceBuilder(trace.getNucleotideSequence(), builtRange)
                .turnOffDataCompression(true)
                .build())){
            return EMPTY;
        }
        if(qualityPredicate !=null && qualityPredicate.test(
                new QualitySequenceBuilder(trace.getQualitySequence(), builtRange)
                .turnOffDataCompression(true)
                .build())){
            return EMPTY;
        }
        return builtRange;
    }
    
    public Range trim(NucleotideSequence seq){
        Builder builder = nucTrim(seq);
        if(builder == EMPTY_BUILDER){
            return EMPTY;
        }
        
        if(minLength !=NOT_SET && builder.getLength() < minLength){
            return EMPTY;
        }
        
        if(rangePredicate !=null && rangePredicate.test(builder)){
            //short circuit
            return EMPTY;
        }
        Range builtRange = builder.build();
        
        if(seqPredicate !=null && seqPredicate.test(
                new NucleotideSequenceBuilder(seq, builtRange)
                .turnOffDataCompression(true)
                .build())){
            return EMPTY;
        }
       
        
        return builder.build();
    }
    
    private Range.Builder nucTrim(NucleotideSequence seq){
        long length = seq.getLength();
        if(minLength !=NOT_SET && length < minLength){
            return EMPTY_BUILDER;
        }
        
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(seq).turnOffDataCompression(true);
       
        Range.Builder fullTrimRange = new Range.Builder(length);

         for(NucleotideTrimmer trimmer : nucleotideTrimmers){
             Range currentRange = trimmer.trim(builder);
             if(minLength !=NOT_SET && currentRange.getLength() < minLength){
                 return EMPTY_BUILDER;
             }
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin() + currentRange.getLength()-1);

             builder.trim(currentRange);
             
         }
         
         return fullTrimRange;
    }
    public Range trim(QualitySequence seq){
        Range.Builder builder = qualTrim(seq);
        if(builder == EMPTY_BUILDER){
            return EMPTY;
        }
        if(minLength !=NOT_SET && builder.getLength() < minLength){
            return EMPTY;
        }
        
        if(rangePredicate !=null && rangePredicate.test(builder)){
            //short circuit
            return EMPTY;
        }
        Range builtRange = builder.build();
       
        if(qualityPredicate !=null && qualityPredicate.test(
                new QualitySequenceBuilder(seq, builtRange)
                .turnOffDataCompression(true)
                .build())){
            return EMPTY;
        }
        
        return builder.build();
    }
    private Range.Builder qualTrim(QualitySequence seq){
        long length = seq.getLength();
        
        if(minLength !=NOT_SET && length < minLength){
            return EMPTY_BUILDER;
        }
        if(qualityPredicate !=null && qualityPredicate.test(seq)){
            return EMPTY_BUILDER;
        }
        
        QualitySequenceBuilder builder = new QualitySequenceBuilder(seq).turnOffDataCompression(true);
        
        Range.Builder fullTrimRange = new Range.Builder(length);
        
        if(rangePredicate !=null && rangePredicate.test(fullTrimRange)){
            //short circuit
            return EMPTY_BUILDER;
        }
         for(QualityTrimmer trimmer : qualityTrimmers){
             Range currentRange = trimmer.trim(builder);
             if(minLength !=NOT_SET && currentRange.getLength() < minLength){
                 return EMPTY_BUILDER;
             }
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin() + currentRange.getLength()-1);
            
             if(rangePredicate !=null && rangePredicate.test(fullTrimRange)){
                 //short circuit
                 return EMPTY_BUILDER;
             }
             builder.trim(currentRange);
             
             if(qualityPredicate !=null && qualityPredicate.test(builder.build())){
                 return EMPTY_BUILDER;
             }
         }
         
         return fullTrimRange;
    }
}
