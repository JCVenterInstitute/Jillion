/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ArtificalPhdDataStore extends AbstractDataStore<Phd>{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern(
    "EEE MMM dd kk:mm:ss yyyy");
    private final DataStore<NucleotideEncodedGlyphs> seqDataStore;
    private final DataStore<EncodedGlyphs<PhredQuality>> qualDataStore;
    private final Properties comments = new Properties();
    
   
    
    /**
     * @param seqDataStore
     * @param qualDataStore
     * @param phdDate
     */
    public ArtificalPhdDataStore(DataStore<NucleotideEncodedGlyphs> seqDataStore,
            DataStore<EncodedGlyphs<PhredQuality>> qualDataStore, DateTime phdDate) {
        this.seqDataStore = seqDataStore;
        this.qualDataStore = qualDataStore;
        comments.put("TIME", DATE_FORMAT.print(phdDate));
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return seqDataStore.contains(id);
    }

    @Override
    public Phd get(String id) throws DataStoreException {
        super.get(id);
       final NucleotideEncodedGlyphs basecalls = seqDataStore.get(id);
       if(basecalls ==null){
           throw new NullPointerException("could not find basecalls for "+id);
       }
    return new ArtificialPhd(basecalls, 
                qualDataStore.get(id),
                comments,Collections.<PhdTag>emptyList(),
                12);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return seqDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        return seqDataStore.size();
    }

}
