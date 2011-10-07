package org.jcvi.common.annotation.predict;

import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public interface PathDecoder<S extends Symbol> {

	List<Integer> decodePath(Hmm<S> hmm, Sequence<S> sequence);
}
