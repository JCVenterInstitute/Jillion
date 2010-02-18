/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;

import java.util.List;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.DefaultAnalysisIssue;
import org.jcvi.assembly.contig.DefaultQualityDifference;

public class QualityDifferenceIssue extends DefaultAnalysisIssue{

    public QualityDifferenceIssue(PlacedRead read, List<DefaultQualityDifference> differences) {
        super(Severity.MEDIUM, createMessage(read, differences));
    }

    private static String createMessage(PlacedRead read,
            List<DefaultQualityDifference> differences) {
        StringBuilder result = new StringBuilder();
        result.append(read.getId())
                .append(" has ").append(differences.size())
                .append(String.format(" high quality differences%n"));
        for(DefaultQualityDifference difference : differences){
                result.append(String.format("\t%s%n",difference));
        }
        return result.toString();
    }


}
