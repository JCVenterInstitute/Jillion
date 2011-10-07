package org.jcvi.common.annotation.predict;

import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public interface PathDecoder<S extends Symbol> {
    /**
     * Decode the path traversed by the given
     * Sequence in the given Hmm.
     * @param hmm the Hmm object which contains
     * the Hmm model to use.
     * @param sequence the sequence to get the path for.
     * @return a list of states in the Hmm 
     */
	List<Integer> decodePath(Hmm<S> hmm, Sequence<S> sequence);
}
