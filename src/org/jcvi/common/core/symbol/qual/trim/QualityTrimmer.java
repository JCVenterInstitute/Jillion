package org.jcvi.common.core.symbol.qual.trim;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public interface QualityTrimmer {

	Range trim(QualitySequence qualities);
}
