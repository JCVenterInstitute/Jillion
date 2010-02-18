/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.Range;

public class AmpliconCritiquorFileVisitor implements CritiquorFileVisitor {
    private final Set<CritiquorAmplicon> amplicons = new HashSet<CritiquorAmplicon>();
    @Override
    public void visitAmplicon(String id, String region, Range designedRange,
            String forwardPrimer, String reversePrimer) {
        CritiquorAmplicon amp = new DefaultCritiquorAmplicon(id, region, designedRange, forwardPrimer, reversePrimer);
        amplicons.add(amp);

    }

    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitStartOfFile() {
        // TODO Auto-generated method stub
        
    }

    public Set<CritiquorAmplicon> getAmplicons() {
        return amplicons;
    }
    
    

}
