/*
 * Created on May 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import org.jcvi.assembly.Placed;


public class CloneCoverageMapXMLCoverageWriter<T extends Placed> extends XMLCoverageWriter<T>{


    @Override
    protected String getCoverageMapTagName() {
        return "clonecoveragemap";
    }

    
}
