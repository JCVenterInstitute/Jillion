package org.jcvi.common.annotation.hmm;

import java.util.Collection;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;

public interface HmmTrainer<S extends Symbol> {

	Hmm<S> train(Hmm<S> initialModel, Collection<? extends Sequence<S>> trainingSequences);
}
