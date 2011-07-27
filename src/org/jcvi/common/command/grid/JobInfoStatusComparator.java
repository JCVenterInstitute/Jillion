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

package org.jcvi.common.command.grid;

import java.util.Comparator;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;

public final class JobInfoStatusComparator implements Comparator<JobInfo>{
    public static final JobInfoStatusComparator INSTANCE = new JobInfoStatusComparator();
    /**
    * {@inheritDoc}
    */
    @Override
    public int compare(JobInfo o1, JobInfo o2) {
        try {
            return GridUtils.getJobStatus(o1).compareTo(GridUtils.getJobStatus(o2));
        } catch (DrmaaException e) {
            throw new IllegalStateException("error querying job status",e);
        }
    }
    
}
