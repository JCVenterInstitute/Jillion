/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.jcvi.datastore;

import org.jcvi.Range;
import org.jcvi.assembly.DefaultScaffold;
import org.jcvi.assembly.agp.AgpFileVisitor;
import org.jcvi.sequence.SequenceDirection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

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
public class DefaultAgpScaffoldDataStore implements ScaffoldDataStore, AgpFileVisitor {
    private Map<String, DefaultScaffold.Builder> builderMap;
    private Map<String, DefaultScaffold> scaffolds;

    public DefaultAgpScaffoldDataStore() {
        builderMap = new LinkedHashMap<String, DefaultScaffold.Builder>();
    }

    public void visitContigEntry(String scaffoldId, Range contigRange, String contigId, SequenceDirection dir) {
        if(!builderMap.containsKey(scaffoldId)){
            builderMap.put(scaffoldId, new DefaultScaffold.Builder(scaffoldId));
        }
        DefaultScaffold.Builder builder = builderMap.get(scaffoldId);
        builder.add(contigId, contigRange, dir);
    }

    public void visitLine(String line) {
        // do nothing for this visit method
    }

    public void visitEndOfFile() {
        scaffolds = new LinkedHashMap<String, DefaultScaffold>(builderMap.size());
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
