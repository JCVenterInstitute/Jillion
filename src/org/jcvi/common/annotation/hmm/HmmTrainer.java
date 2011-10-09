package org.jcvi.common.annotation.hmm;

import java.util.Collection;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public interface HmmTrainer<S extends Symbol> {

	Hmm<S> train(Hmm<S> initialModel, Collection<? extends Sequence<S>> trainingSequences);
}
