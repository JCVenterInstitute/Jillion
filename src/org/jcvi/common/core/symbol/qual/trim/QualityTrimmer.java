package org.jcvi.common.core.symbol.qual.trim;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.jillion.core.Range;

public interface QualityTrimmer {

	Range trim(QualitySequence qualities);
}
