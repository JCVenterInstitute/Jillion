package org.jcvi.jillion.sam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;

/**
 * {@link SamFileDataStore} that has extra optimizations
 * for dealing with sam and bam files that are sorted
 * by query name.
 * 
 * @author dkatzel
 *
 */
class QuerySortedSamFileDataStore extends DefaultSamFileDataStore{

    QuerySortedSamFileDataStore(SamParser parser, Predicate<SamRecord> filter) {
        super(parser, filter);
    }
    @Override
    protected List<SamRecord> getAllRecord(String id) throws IOException{
        List<SamRecord> ret = new ArrayList<SamRecord>();
        parser.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                String queryName = record.getQueryName();
                if(id.equals(queryName)){
                   ret.add(record);
                }
                if(queryName.compareTo(id) > 0){
                    //since we're sorted by query name
                    //any name that sorts after the id we
                    //care about means we won't see any matches beyond
                    //so we can stop parsing
                    haltParsing(callback);
                }
            }
            
        });
        return ret;
    }
    /**
     * Factored out to a method so we can check it
     * got invoked in testing. compiler should in-line anyway?
     * @param callback
     */
    protected void haltParsing(SamVisitorCallback callback){
        callback.haltParsing();
    }
}
