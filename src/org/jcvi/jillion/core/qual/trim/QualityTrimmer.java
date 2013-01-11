package org.jcvi.jillion.core.qual.trim;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public interface QualityTrimmer {

	Range trim(QualitySequence qualities);
}
