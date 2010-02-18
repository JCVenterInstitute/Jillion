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
