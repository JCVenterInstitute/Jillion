package org.jcvi.jillion.assembly.util.consensus;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.util.DefaultSliceElement;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * Utility class of {@link Collector}s that can compute
 * Consensus sequence from a {@link java.util.stream.Stream} of sequence data.
 * 
 * @author dkatzel
 *
 * @since 5.2
 */
public final class ConsensusCollectors {

    

    private ConsensusCollectors(){
        //can not instantiate
    }
    
    public static Collector<SliceElement[], ConsensusCombiner, NucleotideSequence> toSliceConsensus(ConsensusCaller caller){
        
        Objects.requireNonNull(caller);
       
        
        return Collector.of(ConsensusCombiner::new,
                ConsensusCombiner::add, 
                ConsensusCombiner::merge, 
                (ConsensusCombiner combiner)->  combiner.computeConsensus(caller)
                        .turnOffDataCompression(true)
                        .build(),
                Characteristics.UNORDERED);
        
    }
    
    public static Collector<DataStoreEntry<NucleotideSequence>, ConsensusCombiner, NucleotideSequence> toDataStoreConsensus(ConsensusCaller caller){
        return toDataStoreConsensus(caller, PhredQuality.valueOf(25));
    }
    
    public static Collector<DataStoreEntry<NucleotideSequence>, ConsensusCombiner, NucleotideSequence> toDataStoreConsensus(ConsensusCaller caller, PhredQuality defaultQuality){
        return new DataStoreConsensusCaller(caller, defaultQuality);
    }
    
    public static Collector<AssembledRead, ConsensusCombiner, NucleotideSequence> toAssemblyConsensus(ConsensusCaller caller){
        return toAssemblyConsensus(caller, PhredQuality.valueOf(25));
    }
 
    public static Collector<AssembledRead, ConsensusCombiner, NucleotideSequence> toAssemblyConsensus(ConsensusCaller caller, PhredQuality defaultQuality){
        Objects.requireNonNull(caller);
        Objects.requireNonNull(defaultQuality);
        
        return new DefaultQualityAssemblyConsensusCollector(caller, defaultQuality);
        
    }
    
    public static Collector<AssembledRead, ConsensusCombiner, NucleotideSequence> toAssemblyConsensus(ConsensusCaller caller, QualitySequenceDataStore gappedQualityDataStore){
        
        Objects.requireNonNull(caller);
        Objects.requireNonNull(gappedQualityDataStore);
        
       return new AssemblyDataStoreConsensusCollector(caller, gappedQualityDataStore);
        
    }
    
 public static Collector<AssembledRead, ConsensusCombiner, NucleotideSequence> toAssemblyConsensus(ConsensusCaller caller, QualitySequenceDataStore rawQualityDataStore, GapQualityValueStrategy gapQualityValueStrategy){
        
        Objects.requireNonNull(caller);
        Objects.requireNonNull(rawQualityDataStore);
        Objects.requireNonNull(gapQualityValueStrategy);
        
       return new UngappedConverterAssemblyConsensusCollector(caller, rawQualityDataStore, gapQualityValueStrategy);
        
    }
    
    private static class UngappedConverterAssemblyConsensusCollector extends AssemblyDataStoreConsensusCollector{

        private final GapQualityValueStrategy strategy;
        
        public UngappedConverterAssemblyConsensusCollector(ConsensusCaller caller, QualitySequenceDataStore qualities, GapQualityValueStrategy strategy) {
            super(caller, qualities);
           this.strategy = strategy;
        }

        @Override
        protected QualitySequence toGappedQualitySequence(AssembledRead read, QualitySequence rawQualities) {
            return strategy.getGappedValidRangeQualitySequenceFor(read, rawQualities);
        }
        
        
        
    }

    private static abstract class AbstractAssemblyConsensusCollector extends AbstractConsensusCollector<AssembledRead> {

       
        public AbstractAssemblyConsensusCollector(ConsensusCaller caller) {
           super(caller);
        }

        @Override
        protected NucleotideSequence getSequenceFor(AssembledRead read) {
            return read.getNucleotideSequence();
        }

        @Override
        protected int getStartOffset(AssembledRead read) {
            return (int) read.getBegin();
        }

        @Override
        protected int getLength(AssembledRead read) {
            return (int) read.getLength();
        }

        @Override
        protected String getId(AssembledRead read) {
            return read.getId();
        }

        @Override
        protected Direction getDirection(AssembledRead read) {
            return read.getDirection();
        }

    }
    
    
    private static class DataStoreConsensusCaller extends AbstractConsensusCollector<DataStoreEntry<NucleotideSequence>>{

        private final byte defaultQuality;
        
        public DataStoreConsensusCaller(ConsensusCaller caller, PhredQuality defaultQuality) {
            super(caller);
            this.defaultQuality = defaultQuality.getQualityScore();
        }
        
        @Override
        protected QualitySequence getQualitySequenceFor(
                DataStoreEntry<NucleotideSequence> read) {
            int length = getLength(read);
            byte[] array = new byte[length];
            Arrays.fill(array, defaultQuality);
            
            return new QualitySequenceBuilder(array)
                            .turnOffDataCompression(true)
                            .build();
        }

        @Override
        protected NucleotideSequence getSequenceFor(
                DataStoreEntry<NucleotideSequence> read) {
            return read.getValue();
        }

        @Override
        protected int getStartOffset(DataStoreEntry<NucleotideSequence> read) {
            //everything starts at 0
            return 0;
        }

        @Override
        protected int getLength(DataStoreEntry<NucleotideSequence> read) {
            return (int) read.getValue().getLength();
        }

        @Override
        protected String getId(DataStoreEntry<NucleotideSequence> read) {
            return read.getKey();
        }

        @Override
        protected Direction getDirection(DataStoreEntry<NucleotideSequence> read) {
            //Everything defaults to forward
            return Direction.FORWARD;
        }
        
    }
    
    private static abstract class AbstractConsensusCollector<T> implements
    Collector<T, ConsensusCombiner, NucleotideSequence> {

    private final ConsensusCaller caller;
    
    public AbstractConsensusCollector(ConsensusCaller caller) {
        this.caller = caller;
    }
    
    @Override
    public Supplier<ConsensusCombiner> supplier() {
        return ConsensusCombiner::new;
    }
    
    @Override
    public BiConsumer<ConsensusCombiner, T> accumulator() {
    
        return this::addRead;
    }
    
    protected abstract QualitySequence getQualitySequenceFor(T read);
    protected abstract NucleotideSequence getSequenceFor(T read);
    
    protected abstract int getStartOffset(T read);
    protected abstract int getLength(T read);
    protected abstract String getId(T read);
    protected abstract Direction getDirection(T read);
    
    protected void addRead(ConsensusCombiner combiner, T read) {
        int startOffset = getStartOffset(read);
        int length = getLength(read);
    
        SliceElement[] array = new SliceElement[length];
        String id = getId(read);
        int i = 0;
    
        NucleotideSequence seq = getSequenceFor(read);
        QualitySequence quals = getQualitySequenceFor(read);
    
        Iterator<Nucleotide> nIter = seq.iterator();
        Iterator<PhredQuality> qIter = quals.iterator();
        Direction dir = getDirection(read);
        
        while (nIter.hasNext()) {
            Nucleotide n = nIter.next();
            PhredQuality q = qIter.next();
            array[i++] = new DefaultSliceElement(id, n, q, dir);
        }
    
        combiner.add(array, startOffset);
        
    
    }
    
    
    @Override
    public BinaryOperator<ConsensusCombiner> combiner() {
        return ConsensusCombiner::merge;
    }
    
    @Override
    public Function<ConsensusCombiner, NucleotideSequence> finisher() {
        return (combiner) -> combiner.computeConsensus(caller)
                .turnOffDataCompression(true).build();
    }
    
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
    }

}
    
    private static class DefaultQualityAssemblyConsensusCollector extends AbstractAssemblyConsensusCollector{
        private final byte defaultQuality;

        protected DefaultQualityAssemblyConsensusCollector(
                ConsensusCaller caller, PhredQuality defaultQuality) {
            super(caller);
            this.defaultQuality = defaultQuality.getQualityScore();
        }

        @Override
        protected QualitySequence getQualitySequenceFor(AssembledRead read) {
            int length = (int)read.getLength();
            byte[] array = new byte[length];
            Arrays.fill(array, defaultQuality);
            return new QualitySequenceBuilder(array)
                            .turnOffDataCompression(true)
                            .build();
        }
        
        
        
    }
    
    private static class AssemblyDataStoreConsensusCollector
            extends AbstractAssemblyConsensusCollector{
        
        private final QualitySequenceDataStore qualities;
        
        public AssemblyDataStoreConsensusCollector(ConsensusCaller caller, QualitySequenceDataStore qualities) {
           super(caller);
            this.qualities = qualities;
        }

        
        @Override
        protected QualitySequence getQualitySequenceFor(AssembledRead read) {
            String id = read.getId();
            try{
                return toGappedQualitySequence(read, qualities.get(id));
            } catch (DataStoreException e) {
                throw new IllegalStateException("error getting quality values for read " + id);
             }
        }

       
        
        protected QualitySequence toGappedQualitySequence(AssembledRead read, QualitySequence quals){
          //we will assume already gapped?
            if(read.getLength() != quals.getLength()){
                throw new IllegalStateException("quality sequence length does not match gapped nucleotide sequence length " + read.getId());
            }
            return quals;
        }

      

    }
}
