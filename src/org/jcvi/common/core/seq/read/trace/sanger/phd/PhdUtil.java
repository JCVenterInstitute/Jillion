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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.Properties;

import org.jcvi.common.core.assembly.ace.AceFileUtil;
import org.joda.time.DateTime;

/**
 * {@code PhdUtil} is a Utility class for Phred Phd data.
 * @author dkatzel
 *
 *
 */
public class PhdUtil {
    
    /**
     * Phd records must include a date time stamp as a comment,
     * this method will create the correctly formatted Phd {@code TIME}
     * comment.  
     * @param phdDate the {@link DateTime} to make into a Phd TIME
     * comment.
     * @return a Properties object (not null) that contains
     * a single property, TIME.
     */
    public static Properties createPhdTimeStampCommentFor(DateTime phdDate){
        Properties comments = new Properties();
        comments.put("TIME", AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.print(phdDate));        
        return comments;
    }
    /**
     * Phd records must include a date time stamp as a comment,
     * this method will create the correctly formatted Phd {@code TIME}
     * comment.  
     * @param phdDate the {@link DateTime} to make into a Phd TIME
     * comment.
     * @param filename the name of the chromatogram file to link back to.
     * @return a Properties object (not null) that contains
     * a single property, TIME.
     */
    public static Properties createPhdTimeStampAndChromatFileCommentsFor(DateTime phdDate, String filename){
        Properties comments = new Properties();
        comments.put("TIME", AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.print(phdDate));    
        comments.put("CHROMAT_FILE", filename);  
        return comments;
    }
}
