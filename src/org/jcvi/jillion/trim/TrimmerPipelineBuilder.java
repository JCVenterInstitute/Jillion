package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Rangeable;

public class TrimmerPipelineBuilder {

    private final List<NucleotideTrimmer> nucleotideTrimmers = new ArrayList<>();
    private final List<QualityTrimmer> qualityTrimmers = new ArrayList<>();
    
    
    private Predicate<Rangeable> predicate = null;
    private long minLength = -1;
    
    public TrimmerPipelineBuilder minLength(long length){
        if(length < 0){
            throw new IllegalArgumentException("min length must be >= 0");
        }
        this.minLength = length;
        return this;
    }
    public TrimmerPipelineBuilder shortCircuit(Predicate<Rangeable> predicate){
        this.predicate = predicate;
        return this;
    }
    public TrimmerPipelineBuilder add(NucleotideTrimmer trimmer){
        Objects.requireNonNull(trimmer);
        nucleotideTrimmers.add(trimmer);
        return this;
    }
    
    public TrimmerPipelineBuilder add(QualityTrimmer trimmer){
        Objects.requireNonNull(trimmer);
        qualityTrimmers.add(trimmer);
        return this;
    }
    
    public TrimmerPipeline build(){
        return new TrimmerPipeline(nucleotideTrimmers, qualityTrimmers, predicate, minLength);
    }
    
}
