package org.jcvi.common.core.seq.fastx.fasta.pos;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

public interface PositionFastaDataStore extends FastaDataStore<ShortSymbol,Sequence<ShortSymbol>, PositionFastaRecord<Sequence<ShortSymbol>>>{

}
