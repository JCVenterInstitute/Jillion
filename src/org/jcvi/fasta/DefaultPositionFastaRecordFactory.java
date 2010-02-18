/*
 * Created on Jan 27, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;

public class DefaultPositionFastaRecordFactory implements PositionFastaRecordFactory{
 private static final DefaultPositionFastaRecordFactory INSTANCE = new DefaultPositionFastaRecordFactory();
    
    private DefaultPositionFastaRecordFactory(){}
    
    public static DefaultPositionFastaRecordFactory getInstance(){
        return INSTANCE;
    }
    @Override
    public PositionFastaRecord<EncodedGlyphs<ShortGlyph>> createFastaRecord(
            String id, String comments, String recordBody) {
        return PositionsFastaRecordUtil.buildFastaRecord(id, comments, recordBody);
        
    }

    @Override
    public PositionFastaRecord<EncodedGlyphs<ShortGlyph>> createFastaRecord(
            String id, String recordBody) {
        return createFastaRecord(id, null,recordBody);
    }
}
