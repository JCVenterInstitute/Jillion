/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.Range;
import org.jcvi.assembly.annot.Strand;

public interface RefGene {

    String getName();
    int getBin();
    String getReferenceSequenceName();
    Strand getStrand();
    Range getTranscriptionRange();
    CodingRegion getCodingRegion();
    int getId();
    String getAlternateName();
    
    
}
