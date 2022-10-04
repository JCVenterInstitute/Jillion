package org.jcvi.jillion.assembly.util.columns;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * An {@code AssemblyColumn} is a one base wide vertical cut of an assembly containing zero 
 * or more {@link AssemblyColumnElement}s.
 * 
 * @param <T> the type of {@link AssemblyColumnElement}.
 * @author dkatzel
 *
 * @since 6.0
 */
public interface AssemblyColumn<T extends AssemblyColumnElement> extends Iterable<T> {

	/**
	 * Get the coverage depth of this Slice.
	 * @return the coverage depth of this slice, will
	 * always be {@code >= 0}.
	 */
	int getCoverageDepth();

	/**
	 * Get a Mapping of how many of each {@link Nucleotide}
	 * is present in this slice.  
	 * @return a new {@link Map} containing just
	 * the {@link Nucleotide}s counts for this slice
	 * if a Nucleotide is not present in this slice,
	 * then the Map will contain that key with a value of 0.
	 * If {@link #getCoverageDepth()} ==0 then all the Nucletoides
	 * will have values of 0.
	 * Will never return null, 
	 */
	default Map<Nucleotide, Integer> getNucleotideCounts(){
		
		int[] counts = new int[Nucleotide.ALL_VALUES.size()];
		for(T element : this) {
			counts[element.getBase().ordinal()]++;
		}
		Map<Nucleotide, Integer> map = new EnumMap<>(Nucleotide.class);
		for(int i=0; i< counts.length; i++) {
			map.put(Nucleotide.getByOrdinal(i), counts[i]);
		}
		return map;
	}
	/**
	 * Get the AssemblyColumnElements as a {@link Stream}.
	 * @return
	 */
	Stream<T> elements();
	/**
     * Optional consensus of this slice.  If present,
     * this is what the consensus call of this Slice
     * was previously called.  If this Slice gets its
     * consensus recalled using a 
     * {@link org.jcvi.jillion.assembly.util.consensus.ConsensusCaller}
     * then this value will <strong>NOT</strong> be changed.
     * If the consensus for this slice is not available
     * or does not exist yet, then this will return {@code null}.
     * @return the {@link Nucleotide} consensus call for
     * this slice if present, or {@code null}
     * if the consensus is not available or does not
     * exist.
     */
    Nucleotide getConsensusCall();

}