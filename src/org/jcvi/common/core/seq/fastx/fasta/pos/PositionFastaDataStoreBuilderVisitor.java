package org.jcvi.common.core.seq.fastx.fasta.pos;

import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

public interface PositionFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<ShortSymbol, Sequence<ShortSymbol>, PositionSequenceFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore>{

	@Override
	<F extends PositionSequenceFastaRecord<Sequence<ShortSymbol>>> PositionFastaDataStoreBuilderVisitor addFastaRecord(
			F fastaRecord);
}
