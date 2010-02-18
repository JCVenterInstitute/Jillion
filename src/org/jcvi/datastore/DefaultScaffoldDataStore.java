package org.jcvi.datastore;

import org.jcvi.Range;
import org.jcvi.assembly.DefaultScaffold;
import org.jcvi.assembly.PlacedContig;
import org.jcvi.assembly.DefaultPlacedContig;
import org.jcvi.datastore.ScaffoldDataStore;
import org.jcvi.assembly.agp.AgpFileVisitor;
import org.jcvi.sequence.SequenceDirection;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

/**
 * User: aresnick
 * Date: Sep 9, 2009
 * Time: 2:45:50 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class DefaultScaffoldDataStore implements ScaffoldDataStore, AgpFileVisitor {
    private Map<String, DefaultScaffold.Builder> builderMap;
    private Map<String, DefaultScaffold> scaffolds;

    public DefaultScaffoldDataStore() {
        builderMap = new HashMap<String, DefaultScaffold.Builder>();
    }

    public void visitContigEntry(String scaffoldId, Range contigRange, String contigId, SequenceDirection dir) {
        if(!builderMap.containsKey(scaffoldId)){
            builderMap.put(scaffoldId, new DefaultScaffold.Builder(scaffoldId));
        }
        DefaultScaffold.Builder builder = builderMap.get(scaffoldId);
        PlacedContig contig = new DefaultPlacedContig(contigId, contigRange, dir);
        builder.add(contig);
    }

    public void visitLine(String line) {
        // do nothing for this visit method
    }

    public void visitEndOfFile() {
        scaffolds = new HashMap<String, DefaultScaffold>(builderMap.size());
        for(Map.Entry<String, DefaultScaffold.Builder> entry : builderMap.entrySet()){
            scaffolds.put(entry.getKey(), entry.getValue().build());
        }
    }


    public Set<String> getScaffoldIds() {
        return scaffolds.keySet();
    }

    public Iterator<DefaultScaffold> getScaffolds() {
        return scaffolds.values().iterator();
    }

    public DefaultScaffold getScaffold(String id) {
        return scaffolds.get(id);
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }
}
