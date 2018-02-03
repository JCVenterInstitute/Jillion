package org.jcvi.jillion.assembly.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
/**
 * Collectors to turn a Stream of reads into a SliceMap.
 * 
 * <p>
 * The following examples show some example usages:
 * 
 * <pre>
 * {@code
 //CONVERT CONTIG TO SLICE MAP USING DEFAULT QUALITY VALUE:
 SliceMap sliceMap =  contig.reads()
                            .parallel()
                            .collect(SliceMapCollector.toSliceMap(contig.getConsensusSequence()));
                            
 //CONVERT CONTIG TO SLICE MAP FROM PARALLEL STREAM:
 SliceMap sliceMap =  contig.reads()
                            .parallel()
                            .collect(SliceMapCollector.toSliceMap(contig.getConsensusSequence(), qualityDataStore));
 * 
 * }
 * </pre>
 * </p>
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
public final class SliceMapCollector implements Collector<AssembledRead, SliceCombiner, SliceMap>{
    private static final Set<java.util.stream.Collector.Characteristics> UNORDERED_AND_CONCURRENT = EnumSet.of(Collector.Characteristics.UNORDERED,
            Collector.Characteristics.CONCURRENT
);
    private static final PhredQuality DEFAULT_QUALITY = PhredQuality.valueOf(30);
    /**
     * Returns a Collector that creates a SliceMap from a stream of {@link AssembledRead}s
     * with all qualities in the SliceMap faked to be the default quality value (30).
     * 
     * @param consensus the consensus sequence of the contig that the assembled reads are from.
     * @return a new Collector.
     */
    public static SliceMapCollector toSliceMap(NucleotideSequence consensus){
        return toSliceMap(consensus, GapQualityValueStrategy.LOWEST_FLANKING, DEFAULT_QUALITY);
    }
    /**
     * Returns a Collector that creates a SliceMap from a stream of {@link AssembledRead}s
     * with all qualities in the SliceMap faked to be the default quality value (30).
     * 
     * @param consensus the consensus sequence of the contig that the assembled reads are from.
     * @param qualityValueStrategy the {@link GapQualityValueStrategy} used to compute the qualities of gaps in the reads.
     * @return a new Collector.
     */
    public static SliceMapCollector toSliceMap(NucleotideSequence consensus, GapQualityValueStrategy qualityValueStrategy){
        return toSliceMap(consensus, qualityValueStrategy, DEFAULT_QUALITY);
    }
    /**
     * Returns a Collector that creates a SliceMap from a stream of {@link AssembledRead}s
     * with all qualities in the SliceMap faked to be given default quality value.
     * 
     * @param consensus the consensus sequence of the contig that the assembled reads are from.
     * @param qualityValueStrategy the {@link GapQualityValueStrategy} used to compute the qualities of gaps in the reads.
     * @param defaultQuality the {@link PhredQuality} to use for all non-gap bases in the slices.
     * 
     * @return a new Collector.
     */
public static SliceMapCollector toSliceMap(NucleotideSequence consensus, GapQualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality){
    return toSliceMap(consensus, qualityValueStrategy, null, DEFAULT_QUALITY);
    }
/**
 * Returns a Collector that creates a SliceMap from a stream of {@link AssembledRead}s
 * with all qualities in the SliceMap faked to be given default quality value.
 * 
 * @param consensus the consensus sequence of the contig that the assembled reads are from.
 * @param qualityValueStrategy the {@link GapQualityValueStrategy} used to compute the qualities of gaps in the reads.
 * @param qualities the {@link QualitySequenceDataStore} to use to look up the read's quality values.
 * 
 * @return a new Collector.
 */
public static SliceMapCollector toSliceMap(NucleotideSequence consensus, GapQualityValueStrategy qualityValueStrategy, QualitySequenceDataStore qualities){
    return toSliceMap(consensus, qualityValueStrategy, qualities, DEFAULT_QUALITY);
    
}
/**
 * Returns a Collector that creates a SliceMap from a stream of {@link AssembledRead}s
 * with all qualities in the SliceMap faked to be given default quality value.
 * 
 * @param consensus the consensus sequence of the contig that the assembled reads are from.
 * @param qualityValueStrategy the {@link GapQualityValueStrategy} used to compute the qualities of gaps in the reads.
 * @param qualities the {@link QualitySequenceDataStore} to use to look up the read's quality values.
 * @param defaultQuality the {@link PhredQuality} to use for all non-gap bases in the slices for reads that are not found
 * in the quality datastore.
 * 
 * @return a new Collector.
 */
public static SliceMapCollector toSliceMap(NucleotideSequence consensus, GapQualityValueStrategy qualityValueStrategy, QualitySequenceDataStore qualities, PhredQuality defaultQuality){
    return new SliceMapCollector(consensus, qualities, qualityValueStrategy, defaultQuality);
    
}
    private final NucleotideSequence consensus;
    private final QualitySequenceDataStore qualityDataStore;
    private final GapQualityValueStrategy qualityValueStrategy;
    private final PhredQuality defaultQuality;
    
    
    private SliceMapCollector(NucleotideSequence consensus,
            QualitySequenceDataStore qualityDataStore,
            GapQualityValueStrategy qualityValueStrategy,
            PhredQuality defaultQuality) {
        this.consensus = consensus;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = qualityValueStrategy;
        this.defaultQuality = defaultQuality;
    }

    @Override
    public Supplier<SliceCombiner> supplier() {
        return ()-> new SliceCombiner((int)consensus.getLength());
    }

    @Override
    public BiConsumer<SliceCombiner, AssembledRead> accumulator() {
        return this::addRead;
    }
    
    private void addRead(SliceCombiner combiner, AssembledRead read){
        int start = (int)read.getGappedStartOffset();
        String id =read.getId();
        Direction dir = read.getDirection();
        Iterator<PhredQuality> validRangeGappedQualitiesIterator =null;
        if(qualityDataStore==null){
                validRangeGappedQualitiesIterator = createNewDefaultQualityIterator(defaultQuality);

        }else{
                QualitySequence fullQualities;
                try {
                    fullQualities = qualityDataStore.get(id);
                } catch (DataStoreException e) {
                   throw new RuntimeException("error processing quality data for " + id);
                }
                
                if(fullQualities ==null){
                        throw new NullPointerException("could not get qualities for "+id);
                }
                validRangeGappedQualitiesIterator = qualityValueStrategy.getGappedValidRangeQualitySequenceFor(read, fullQualities)
                                                                                                .iterator();
                
        }
        combiner.add(id, start, read.getNucleotideSequence(), validRangeGappedQualitiesIterator, dir);
    }

    @Override
    public BinaryOperator<SliceCombiner> combiner() {
        return (a,b)-> a.combine(b);
    }

    @Override
    public Function<SliceCombiner, SliceMap> finisher() {        
        return this::toSliceMap;
    }

    private SliceMap toSliceMap(SliceCombiner combiner){
        Slice[] slices = combiner.toSlices(consensus);
        return new CollectedSliceMap(slices);
    }
    
    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        return UNORDERED_AND_CONCURRENT;
    }

    private Iterator<PhredQuality> createNewDefaultQualityIterator(
            final PhredQuality defaultQuality) {
    return new Iterator<PhredQuality>(){
                    
            @Override
            public boolean hasNext() {
                    //always return true
                    return true;
            }
            @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
                            value = {"IT_NO_SUCH_ELEMENT"}, 
                            justification = "only used for fake data will never have no such element exception")                            
            @Override
            public PhredQuality next() {
                    return defaultQuality;
            }

            @Override
            public void remove() {
                    //no-op                         
            }
            
    };
}
    private static class CollectedSliceMap implements SliceMap{
        private final Slice[] slices;
        
        protected CollectedSliceMap(Slice[] slices) {
            this.slices = slices;
        }

        @Override
        public Iterator<Slice> iterator() {
            return new ArrayIterator<Slice>(slices, false);
        }

        @Override
        public Slice getSlice(long offset) {
            return slices[(int)offset];
        }

        @Override
        public long getSize() {
            return slices.length;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(slices);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof CollectedSliceMap)) {
                return false;
            }
            CollectedSliceMap other = (CollectedSliceMap) obj;
            if (!Arrays.equals(slices, other.slices)) {
                return false;
            }
            return true;
        }
        
    }
   
    
    
}
