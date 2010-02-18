/*
 * Created on May 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.Properties;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.sequence.SequenceDirection;

public class DirectionalSequenceCoverageMapXMLCoverageWriter<T extends PlacedRead> extends XMLCoverageWriter<T> {

    @Override
    protected void addAdditionalAttributes(CoverageRegion<T> region,
            Properties attriubtes) {
        int numberReversedComplimented=0;
        int numberForwardComplimented=0;
        for(T placedRead : region.getElements()){
            if(placedRead.getSequenceDirection()== SequenceDirection.REVERSE){
                numberReversedComplimented++;
            }
            else{
                numberForwardComplimented++;
            }
        }
        attriubtes.put("forward_depth", numberForwardComplimented);
        attriubtes.put("reverse_depth", numberReversedComplimented);
    }

    @Override
    protected String getCoverageMapTagName() {
        return "sequencecoveragemap";
    }

    
}
