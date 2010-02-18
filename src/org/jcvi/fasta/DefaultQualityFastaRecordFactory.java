/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultQualityFastaRecordFactory implements QualityFastaRecordFactory{
    private static final DefaultQualityFastaRecordFactory INSTANCE = new DefaultQualityFastaRecordFactory();
    
    private DefaultQualityFastaRecordFactory(){}
    
    public static DefaultQualityFastaRecordFactory getInstance(){
        return INSTANCE;
    }
    @Override
    public QualityFastaRecord<EncodedGlyphs<PhredQuality>> createFastaRecord(
            String id, String comments, String recordBody) {
        return QualityFastaRecordUtil.buildFastaRecord(id,comments, recordBody);
        
    }

    @Override
    public QualityFastaRecord<EncodedGlyphs<PhredQuality>> createFastaRecord(
            String id, String recordBody) {
        return createFastaRecord(id, null,recordBody);
    }
    

}
