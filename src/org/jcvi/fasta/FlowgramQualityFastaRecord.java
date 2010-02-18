/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class FlowgramQualityFastaRecord extends DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>{

    public FlowgramQualityFastaRecord(String id, Flowgram flowgram) {
        super(id, null,flowgram.getQualities());
    }

}
